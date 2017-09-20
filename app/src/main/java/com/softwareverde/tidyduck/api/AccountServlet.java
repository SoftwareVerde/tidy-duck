package com.softwareverde.tidyduck.api;

import com.softwareverde.database.*;
import com.softwareverde.json.Json;
import com.softwareverde.security.SecureHashUtil;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.BaseServlet;
import com.softwareverde.tomcat.servlet.JsonServlet;
import com.softwareverde.tomcat.servlet.Session;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.util.List;

public class AccountServlet extends JsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleRequest(final HttpServletRequest request, final HttpMethod httpMethod, final Environment environment) {
        final Database<Connection> database = environment.getDatabase();

        final String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);

        final Boolean isPost = (httpMethod == HttpMethod.POST);
        final Boolean doSelect = ("account".equals(finalUrlSegment));
        final Boolean doAuthenticate = ("authenticate".equals(finalUrlSegment));
        final Boolean doLogout = ("logout".equals(finalUrlSegment));

        if ( (! isPost) && (doSelect) ) {
            final Boolean isAuthenticated = Session.isAuthenticated(request);
            if (! isAuthenticated) {
                return super._generateErrorJson("Not authorized.");
            }

            final Long accountId = Session.getAccountId(request);

            final Json responseJson = super._generateSuccessJson();
            responseJson.put("accountId", accountId);
            return responseJson;
        }
        else if (isPost && doAuthenticate) {
            final String username = Util.coalesce(request.getParameter("username"));
            final String password = Util.coalesce(request.getParameter("password"));

            try (final DatabaseConnection<Connection> databaseConnection = database.newConnection()) {
                if (! authenticateAccount(username, password, request, databaseConnection)) {
                    return super._generateErrorJson("Invalid credentials.");
                }

                return super._generateSuccessJson();
            }
            catch (final DatabaseException databaseException) {
                _logger.error("Error authenticating.", databaseException);
                return super._generateErrorJson("Error communicating with the database.");
            }
        }
        else if (doLogout) {
            Session.setAccountId(null, request);
            return super._generateSuccessJson();
        }

        return super._generateErrorJson("Invalid endpoint.");
    }

    private boolean authenticateAccount(final String username, final String password, final HttpServletRequest request, final DatabaseConnection<Connection> databaseConnection) throws DatabaseException {
        final Query query = new Query("SELECT id, password FROM accounts WHERE username = ?")
                .setParameter(username)
                ;

        final List<Row> rows = databaseConnection.query(query);
        if (rows.isEmpty()) {
            return false;
        }

        final Row row = rows.get(0);
        final Long accountId = row.getLong("id");
        final String storedPassword = row.getString("password");

        if (SecureHashUtil.validateHashWithPbkdf2(password, storedPassword)) {
            Session.setAccountId(accountId, request);
            return true;
        }

        return false;
    }
}
