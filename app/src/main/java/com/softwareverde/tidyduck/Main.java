package com.softwareverde.tidyduck;

import com.softwareverde.http.server.HttpServer;
import com.softwareverde.http.server.endpoint.Endpoint;
import com.softwareverde.http.server.servlet.DirectoryServlet;
import com.softwareverde.http.server.servlet.Servlet;
import com.softwareverde.http.server.servlet.session.SessionManager;
import com.softwareverde.logging.LogLevel;
import com.softwareverde.logging.Logger;
import com.softwareverde.logging.log.AnnotatedLog;
import com.softwareverde.tidyduck.api.*;
import com.softwareverde.tidyduck.authentication.TidyDuckAuthenticator;
import com.softwareverde.tidyduck.environment.Configuration;
import com.softwareverde.tidyduck.environment.TidyDuckEnvironment;
import com.softwareverde.util.Util;

import java.io.File;

public class Main {
    public static void main(final String[] args) {
        final Main main = new Main();
        main.run();
    }

    protected final Configuration.ServerProperties _serverProperties;
    protected final HttpServer _apiServer;

    public Main() {
        final TidyDuckEnvironment environment;
        try {
            Logger.setLog(AnnotatedLog.getInstance());
            Logger.setLogLevel(LogLevel.ON);
            Logger.setLogLevel("com.softwareverde.util", LogLevel.ERROR);

            environment = TidyDuckEnvironment.getInstance();
        }
        catch (final Exception exception) {
            Logger.error("Unable to initialize environment", exception);
            throw new RuntimeException();
        }

        final String cookiesDirectory = environment.getCookiesDirectory();
        final boolean shouldCreateSecureCookies = environment.shouldCreateSecureCookies();
        final Integer cookieMaxAgeInSeconds = Util.coalesce(environment.getCookieMaxAgeInSeconds(), SessionManager.DEFAULT_SESSION_TIMEOUT);
        final SessionManager sessionManager = new SessionManager(cookiesDirectory, shouldCreateSecureCookies, cookieMaxAgeInSeconds);

        final Configuration configuration = environment.getConfiguration();
        _serverProperties = configuration.getServerProperties();

        _apiServer = new HttpServer();
        _apiServer.setPort(_serverProperties.getPort());
        if (_serverProperties.hasTlsEnabled()) {
            _apiServer.setTlsPort(_serverProperties.getTlsPort());
            _apiServer.setCertificate(_serverProperties.getTlsCertificateFile(), _serverProperties.getTlsKeyFile());
            _apiServer.enableEncryption(true);
            _apiServer.redirectToTls(true, _serverProperties.getExternalTlsPort());
        }
        else {
            _apiServer.enableEncryption(false);
            _apiServer.redirectToTls(false);
        }

        final boolean isTwoFactorEnabled = environment.isTwoFactorEnabled();
        final TidyDuckAuthenticator authenticator = new TidyDuckAuthenticator(isTwoFactorEnabled);

        final SessionServlet sessionServlet = new SessionServlet(environment, sessionManager, authenticator);
        final Endpoint sessionEndpoint = new Endpoint(sessionServlet);
        sessionEndpoint.setStrictPathEnabled(false);
        sessionEndpoint.setPath("/session");

        final FunctionCatalogServlet functionCatalogServlet = new FunctionCatalogServlet(environment, sessionManager, authenticator);
        final Endpoint functionCatalogEndpoint = new Endpoint(functionCatalogServlet);


        final FunctionBlockServlet functionBlockServlet = new FunctionBlockServlet(environment, sessionManager, authenticator);

        final MostFunctionServlet mostFunctionServlet = new MostFunctionServlet(environment, sessionManager, authenticator);

        final MostFunctionStereotypeServlet mostFunctionStereotypeServlet = new MostFunctionStereotypeServlet(environment, sessionManager, authenticator);


        final MostGeneratorServlet mostGeneratorServlet = new MostGeneratorServlet(environment, sessionManager, authenticator);

        final MostInterfaceServlet mostInterfaceServlet = new MostInterfaceServlet(environment, sessionManager, authenticator);

        final MostTypeServlet mostTypeServlet = new MostTypeServlet(environment, sessionManager, authenticator);

        final ReviewServlet reviewServlet = new ReviewServlet(environment, sessionManager, authenticator);

        final SettingsServlet settingsServlet = new SettingsServlet(environment, sessionManager, authenticator);

        final AccountManagementServlet accountManagementServlet = new AccountManagementServlet(environment, sessionManager, authenticator);

        final ApplicationSettingsServlet applicationSettingsServlet = new ApplicationSettingsServlet(environment, sessionManager, authenticator);

        _apiServer.addEndpoint(sessionEndpoint);

        _apiServer.addEndpoint(functionCatalogEndpoint);

        {
            final File webAppRoot = new File("www/");
            final DirectoryServlet webAppServlet = new DirectoryServlet(webAppRoot);
            webAppServlet.setShouldServeDirectories(true);
            webAppServlet.setIndexFile("index.html");

            {
                final Endpoint webAppEndpoint = new Endpoint(webAppServlet);
                webAppEndpoint.setStrictPathEnabled(false);
                webAppEndpoint.setPath("/");
                _apiServer.addEndpoint(webAppEndpoint);
            }
        }
    }

    protected void _addEndpoint(final Servlet servlet, final String path, final Boolean alsoEnablePlural) {
        if (alsoEnablePlural) {
            final Endpoint endpoint = new Endpoint(servlet);
            endpoint.setStrictPathEnabled(false);
            endpoint.setPath(path + "s");
            _apiServer.addEndpoint(endpoint);
        }

        final Endpoint endpoint = new Endpoint(servlet);
        endpoint.setStrictPathEnabled(false);
        endpoint.setPath(path);
        _apiServer.addEndpoint(endpoint);
    }

    public void run() {
        System.out.println("[Starting Server]");
        _apiServer.start();
        System.out.println("[Server Started On Port " + (_serverProperties.hasTlsEnabled() ? _serverProperties.getTlsPort() : _serverProperties.getPort()) + "]");

        while (true) {
            try { Thread.sleep(10000L); }
            catch (final Exception exception) { break; }
        }
    }
}
