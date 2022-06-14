package com.softwareverde.tidyduck;

import com.softwareverde.http.server.HttpServer;
import com.softwareverde.http.server.endpoint.Endpoint;
import com.softwareverde.http.server.servlet.DirectoryServlet;
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
        functionCatalogEndpoint.setStrictPathEnabled(false);
        functionCatalogEndpoint.setPath("/function-catalogs");


        final FunctionBlockServlet functionBlockServlet = new FunctionBlockServlet(environment, sessionManager, authenticator);
        final Endpoint functionBlockEndpoint = new Endpoint(functionBlockServlet);
        functionBlockEndpoint.setStrictPathEnabled(false);
        functionBlockEndpoint.setPath("/function-blocks");

        final MostFunctionServlet mostFunctionServlet = new MostFunctionServlet(environment, sessionManager, authenticator);
        final Endpoint mostFunctionEndpoint = new Endpoint(mostFunctionServlet);
        mostFunctionEndpoint.setStrictPathEnabled(false);
        mostFunctionEndpoint.setPath("/most-functions");

        final MostFunctionStereotypeServlet mostFunctionStereotypeServlet = new MostFunctionStereotypeServlet(environment, sessionManager, authenticator);
        final Endpoint mostFunctionStereotypeEndpoint = new Endpoint(mostFunctionStereotypeServlet);
        mostFunctionStereotypeEndpoint.setStrictPathEnabled(false);
        mostFunctionStereotypeEndpoint.setPath("/mostFunctionStereotypeEndpoint");

        final MostGeneratorServlet mostGeneratorServlet = new MostGeneratorServlet(environment, sessionManager, authenticator);
        final Endpoint mostGeneratorEndpoint = new Endpoint(mostGeneratorServlet);
        mostGeneratorEndpoint.setStrictPathEnabled(false);
        mostGeneratorEndpoint.setPath("/generate-most");

        final MostInterfaceServlet mostInterfaceServlet = new MostInterfaceServlet(environment, sessionManager, authenticator);
        final Endpoint mostInterfaceEndpoint = new Endpoint(mostInterfaceServlet);
        mostInterfaceEndpoint.setStrictPathEnabled(false);
        mostInterfaceEndpoint.setPath("/most-interfaces");

        final MostTypeServlet mostTypeServlet = new MostTypeServlet(environment, sessionManager, authenticator);
        final Endpoint mostTypeEndpoint = new Endpoint(mostTypeServlet);
        mostTypeEndpoint.setStrictPathEnabled(false);
        mostTypeEndpoint.setPath("/most-types");

        final ReviewServlet reviewServlet = new ReviewServlet(environment, sessionManager, authenticator);
        final Endpoint reviewEndpoint = new Endpoint(reviewServlet);
        reviewEndpoint.setStrictPathEnabled(false);
        reviewEndpoint.setPath("/reviews");

        final SettingsServlet settingsServlet = new SettingsServlet(environment, sessionManager, authenticator);
        final Endpoint settingsEndpoint = new Endpoint(settingsServlet);
        settingsEndpoint.setStrictPathEnabled(false);
        settingsEndpoint.setPath("/settings");

        final AccountManagementServlet accountManagementServlet = new AccountManagementServlet(environment, sessionManager, authenticator);
        final Endpoint accountManagementEndpoint = new Endpoint(accountManagementServlet);
        accountManagementEndpoint.setStrictPathEnabled(false);
        accountManagementEndpoint.setPath("/accounts");

        final ApplicationSettingsServlet applicationSettingsServlet = new ApplicationSettingsServlet(environment, sessionManager, authenticator);
        final Endpoint applicationSettingsEndpoint = new Endpoint(applicationSettingsServlet);
        applicationSettingsEndpoint.setStrictPathEnabled(false);
        applicationSettingsEndpoint.setPath("/application-settings");

        _apiServer.addEndpoint(sessionEndpoint);
        _apiServer.addEndpoint(functionCatalogEndpoint);
        _apiServer.addEndpoint(functionBlockEndpoint);
        _apiServer.addEndpoint(mostFunctionEndpoint);
        _apiServer.addEndpoint(mostFunctionStereotypeEndpoint);
        _apiServer.addEndpoint(mostGeneratorEndpoint);
        _apiServer.addEndpoint(mostInterfaceEndpoint);
        _apiServer.addEndpoint(mostTypeEndpoint);
        _apiServer.addEndpoint(reviewEndpoint);
        _apiServer.addEndpoint(settingsEndpoint);
        _apiServer.addEndpoint(settingsEndpoint);
        _apiServer.addEndpoint(accountManagementEndpoint);
        _apiServer.addEndpoint(applicationSettingsEndpoint);

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
