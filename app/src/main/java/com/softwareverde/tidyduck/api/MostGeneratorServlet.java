package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.jdbc.JdbcDatabaseConnection;
import com.softwareverde.http.server.servlet.request.Request;
import com.softwareverde.http.server.servlet.response.Response;
import com.softwareverde.http.server.servlet.routed.AuthenticatedApplicationServlet;
import com.softwareverde.http.server.servlet.routed.account.LoginRequestHandler;
import com.softwareverde.http.server.servlet.session.Session;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.json.Json;
import com.softwareverde.logging.Logger;
import com.softwareverde.mostadapter.MostAdapter;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.AccountId;
import com.softwareverde.tidyduck.AuthorizationException;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.database.AccountInflater;
import com.softwareverde.tidyduck.database.FunctionCatalogInflater;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;
import com.softwareverde.tidyduck.most.FunctionCatalog;
import com.softwareverde.tidyduck.most.MostTypeConverter;
import com.softwareverde.util.Util;

public class MostGeneratorServlet extends AuthenticatedApplicationServlet<TidyDuckEnvironment> {
    protected final TidyDuckEnvironment _environment;
    protected final SessionManager _sessionManager;
    protected final TidyDuckAuthenticator _authenticator;

    public MostGeneratorServlet(final TidyDuckEnvironment environment, final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        super(environment, sessionManager);

        _environment = environment;
        _sessionManager = sessionManager;
        _authenticator = authenticator;
    }

    @Override
    public Response onRequest(final Request request) {
        final Session session = _sessionManager.getSession(request);
        if (session == null) {
            return _getUnauthenticatedErrorResponse();
        }

        final Json sessionData = session.getMutableData();
        if (! sessionData.hasKey(LoginRequestHandler.ACCOUNT_SESSION_KEY)) {
            return _getUnauthenticatedErrorResponse();
        }

        final long functionCatalogId = Util.parseLong(request.getGetParameters().get("function_catalog_id"));
        if (functionCatalogId < 1) {
            Logger.error("Invalid function catalog id.");
            return _getBadRequestResponse();
        }

        try (final JdbcDatabaseConnection databaseConnection = _environment.getDatabase().newConnection()) {
            try {
                checkPermissions(session, databaseConnection);
            } catch (Exception e) {
                Logger.error("Unable to authorize user.", e);
                return _getUnauthenticatedErrorResponse();
            }

            return _returnMostXmlAsAttachment(functionCatalogId, databaseConnection);
        }
        catch (final Exception exception) {
            final String errorMessage = "Problem generating MOST XML for function catalog " + functionCatalogId + ".";
            Logger.error(errorMessage, exception);

            final Response errorResponse = new Response();
            errorResponse.setCode(Response.Codes.SERVER_ERROR);
            errorResponse.setContent(errorMessage);

            return errorResponse;
        }
        finally {
            // save any session changes
            _sessionManager.saveSession(session);
        }
    }

    private void checkPermissions(final Session session, final JdbcDatabaseConnection databaseConnection) throws DatabaseException, AuthorizationException {
        final AccountId accountId = TidyDuckAuthenticator.getAccountId(session);
        final AccountInflater accountInflater = new AccountInflater(databaseConnection);
        final Account currentAccount = accountInflater.inflateAccount(accountId);

        currentAccount.requirePermission(Permission.MOST_COMPONENTS_VIEW);
    }

    private Response _returnMostXmlAsAttachment(final long functionCatalogId, final JdbcDatabaseConnection databaseConnection) throws Exception {
        final FunctionCatalogInflater functionCatalogInflater = new FunctionCatalogInflater(databaseConnection);
        final FunctionCatalog functionCatalog = functionCatalogInflater.inflateFunctionCatalog(functionCatalogId, true);

        final MostAdapter mostAdapter = new MostAdapter();
        mostAdapter.setIndented(true);
        mostAdapter.setIndentationAmount(2);

        MostTypeConverter mostTypeConverter = new MostTypeConverter();
        final String mostXml = mostAdapter.getMostXml(mostTypeConverter.convertFunctionCatalog(functionCatalog));

        final int mostXmlLength = mostXml.getBytes().length;
        final String functionCatalogName = functionCatalog.getName();
        Logger.info(String.format("Generated %d bytes for %s (id: %d).", mostXmlLength, functionCatalogName, functionCatalogId));

        final String fileName = functionCatalog.getName()+".xml";
        final Response response = new Response();
        response.setHeader("Content-Disposition", "attachment;filename="+fileName);
        response.setCode(Response.Codes.OK);
        response.setContent(mostXml);

        return response;
    }
}
