package com.softwareverde.tidyduck.api;

import com.softwareverde.http.HttpMethod;
import com.softwareverde.http.server.servlet.routed.ApplicationServlet;
import com.softwareverde.http.server.servlet.routed.account.AccountInformationRequestHandler;
import com.softwareverde.http.server.servlet.routed.account.LoginRequestHandler;
import com.softwareverde.http.server.servlet.routed.account.LogoutRequestHandler;
import com.softwareverde.http.server.servlet.routed.account.ResetPasswordRequestHandler;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;

public class SessionServlet extends ApplicationServlet<TidyDuckEnvironment> {
    public SessionServlet(final TidyDuckEnvironment environment, final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        super(environment);

        _defineEndpoint("/session/api/v1/login", HttpMethod.POST, new LoginRequestHandler<>(sessionManager, authenticator));
        _defineEndpoint("/session/api/v1/account", HttpMethod.GET, new AccountInformationRequestHandler<>(sessionManager, authenticator));
        _defineEndpoint("/session/api/v1/reset-password", HttpMethod.POST, new ResetPasswordRequestHandler<>(sessionManager, authenticator));
        _defineEndpoint("/session/api/v1/logout", HttpMethod.GET, new LogoutRequestHandler<>(sessionManager));
    }
}
