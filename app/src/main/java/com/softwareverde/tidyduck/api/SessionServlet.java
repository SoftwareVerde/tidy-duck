package com.softwareverde.tidyduck.api;

import com.softwareverde.http.HttpMethod;
import com.softwareverde.http.server.servlet.routed.account.AccountInformationRequestHandler;
import com.softwareverde.http.server.servlet.routed.account.LoginRequestHandler;
import com.softwareverde.http.server.servlet.routed.account.LogoutRequestHandler;
import com.softwareverde.http.server.servlet.routed.account.ResetPasswordRequestHandler;
import com.softwareverde.http.server.servlet.routed.json.JsonApplicationServlet;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;

public class SessionServlet extends JsonApplicationServlet<TidyDuckEnvironment> {
    public SessionServlet(final TidyDuckEnvironment environment, final SessionManager sessionManager, final TidyDuckAuthenticator authenticator) {
        super(environment);

        _defineEndpoint("/session/login", HttpMethod.POST, new LoginRequestHandler<>(sessionManager, authenticator));
        _defineEndpoint("/session/account", HttpMethod.GET, new AccountInformationRequestHandler<>(sessionManager, authenticator));
        _defineEndpoint("/session/reset-password", HttpMethod.POST, new ResetPasswordRequestHandler<>(sessionManager, authenticator));
        _defineEndpoint("/session/logout", HttpMethod.GET, new LogoutRequestHandler<>(sessionManager));
    }
}
