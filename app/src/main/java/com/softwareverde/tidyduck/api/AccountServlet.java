package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.database.Row;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.servlet.AuthenticatedJsonServlet;
import com.softwareverde.tomcat.servlet.BaseServlet;
import com.softwareverde.tomcat.servlet.JsonServlet;
import com.softwareverde.util.HashUtil;
import com.softwareverde.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Connection;
import java.util.List;

public class AccountServlet extends JsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleRequest(final HttpServletRequest request, final HttpMethod httpMethod, final Environment environment) {
        final String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);

        final Boolean isPost = (httpMethod == HttpMethod.POST);
        final Boolean doAuthenticate = ("authenticate".equals(finalUrlSegment));

        if (isPost && doAuthenticate) {
            final String username = Util.coalesce(request.getParameter("username"));
            final String password = Util.coalesce(request.getParameter("password"));

            try {
                final DatabaseConnection<Connection> databaseConnection = environment.getNewDatabaseConnection();
                final List<Row> rows = databaseConnection.query(
                    "SELECT id FROM accounts WHERE username = ? AND password = ?",
                    new String[] {
                        username, HashUtil.sha256(password)
                    }
                );

                if (rows.isEmpty()) {
                    return super._generateErrorJson("Invalid credentials.");
                }

                final Row row = rows.get(0);
                final Long accountId = row.getLong("id");

                final HttpSession session = request.getSession();
                session.setAttribute(AuthenticatedJsonServlet.SESSION_ACCOUNT_ID_KEY, accountId);

                return super._generateSuccessJson();
            }
            catch (final DatabaseException databaseException) {
                _logger.error("Error authenticating.", databaseException);
                return super._generateErrorJson("Error communicating with the database.");
            }
        }

        return super._generateErrorJson("Invalid endpoint.");
    }
}
