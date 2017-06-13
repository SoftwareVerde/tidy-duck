package com.softwareverde.tidyduck.api;

import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.database.DatabaseException;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.Company;
import com.softwareverde.tidyduck.DateUtil;
import com.softwareverde.tidyduck.FunctionCatalog;
import com.softwareverde.tidyduck.database.DatabaseManager;
import com.softwareverde.tidyduck.database.MostCatalogInflater;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tidyduck.util.Util;
import com.softwareverde.tomcat.servlet.BaseServlet;
import com.softwareverde.tomcat.servlet.JsonServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.List;

public class AccountServlet extends JsonServlet {
    private final Logger _logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected Json handleRequest(final HttpServletRequest request, final HttpMethod httpMethod, final Environment environment) throws Exception {
        final String finalUrlSegment = BaseServlet.getFinalUrlSegment(request);

        final Boolean isPost = (httpMethod == HttpMethod.POST);
        final Boolean doAuthenticate = ("authenticate".equals(finalUrlSegment));

        if (isPost && doAuthenticate) {
            return new Json(); // TODO
        }

        return super.generateErrorJson("Invalid endpoint.");
    }
}
