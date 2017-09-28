package com.softwareverde.tomcat.api;

import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.Account;
import com.softwareverde.tidyduck.FakeEnvironment;
import com.softwareverde.tidyduck.Permission;
import com.softwareverde.tidyduck.Role;
import com.softwareverde.tidyduck.api.AuthenticatedJsonRequestHandler;
import com.softwareverde.tidyduck.environment.Environment;
import com.softwareverde.tomcat.FakeRequest;
import com.softwareverde.tomcat.servlet.BaseServlet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ApiUrlRouterTests {

    private Account masterAccount;

    @Before
    public void setup() {
        masterAccount = new Account();
        masterAccount.setId(0L);
        Role masterRole = new Role();
        for (Permission permission : Permission.values()) {
            masterRole.addPermission(permission);
        }
        masterAccount.addRole(masterRole);
    }

    @Test
    public void should_return_empty_json_if_no_match_and_no_error_handler_set() throws Exception {
        // Setup
        final String baseUrl = "/api/v1/";
        final ApiUrlRouter<AuthenticatedJsonRequestHandler> apiUrlRouter = new ApiUrlRouter<AuthenticatedJsonRequestHandler>(baseUrl, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return new Json();
            }
        });

        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", baseUrl + "objects");

        // Action
        final ApiRoute<AuthenticatedJsonRequestHandler> route = apiUrlRouter.route(fakeRequest, BaseServlet.HttpMethod.GET);
        final Json json = route.getRequestHandler().handleAuthenticatedRequest(route.getParameters(), fakeRequest, BaseServlet.HttpMethod.GET, masterAccount, new FakeEnvironment());


        // Assert
        Assert.assertEquals((new Json()).toString(), json.toString());
    }

    @Test
    public void should_return_error_handlers_json_if_no_match() throws Exception {
        // Setup
        final Json errorJson = new Json();
        errorJson.put("wasSuccess", 0);
        errorJson.put("errorMessage", "Invalid endpoint.");

        final String baseUrl = "/api/v1/";
        final ApiUrlRouter<AuthenticatedJsonRequestHandler> apiUrlRouter = new ApiUrlRouter<AuthenticatedJsonRequestHandler>(baseUrl, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return errorJson;
            }
        });

        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", baseUrl + "objects");

        // Action
        final ApiRoute<AuthenticatedJsonRequestHandler> route = apiUrlRouter.route(fakeRequest, BaseServlet.HttpMethod.GET);
        final Json json = route.getRequestHandler().handleAuthenticatedRequest(route.getParameters(), fakeRequest, BaseServlet.HttpMethod.GET, masterAccount, new FakeEnvironment());

        // Assert
        Assert.assertEquals(errorJson.toString(), json.toString());
    }

    @Test
    public void should_return_delegate_response_to_matched_path() throws Exception {
        // Setup
        final String baseUrl = "/api/v1/";

        final ApiUrlRouter<AuthenticatedJsonRequestHandler> apiUrlRouter = new ApiUrlRouter<AuthenticatedJsonRequestHandler>(baseUrl, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return null;
            }
        });

        final Json objectJson = new Json();
        objectJson.put("wasSuccess", 1);
        objectJson.put("errorMessage", null);
        objectJson.put("object", "Object Value");

        apiUrlRouter.defineEndpoint("objects", BaseServlet.HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return objectJson;
            }
        });

        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", baseUrl + "objects");

        // Action
        final ApiRoute<AuthenticatedJsonRequestHandler> route = apiUrlRouter.route(fakeRequest, BaseServlet.HttpMethod.GET);
        final Json json = route.getRequestHandler().handleAuthenticatedRequest(route.getParameters(), fakeRequest, BaseServlet.HttpMethod.GET, masterAccount, new FakeEnvironment());

        // Assert
        Assert.assertEquals(objectJson.toString(), json.toString());
    }

    @Test
    public void should_return_delegate_response_to_matched_path_with_parameter() throws Exception {
        // Setup
        final String baseUrl = "/api/v1/";
        final ApiUrlRouter<AuthenticatedJsonRequestHandler> apiUrlRouter = new ApiUrlRouter<AuthenticatedJsonRequestHandler>(baseUrl, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return null;
            }
        });

        final String objectId = "1";

        final Json objectJson = new Json();
        objectJson.put("wasSuccess", 1);
        objectJson.put("errorMessage", null);
        objectJson.put("object", objectId);

        apiUrlRouter.defineEndpoint("objects/<objectId>", BaseServlet.HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                final Json json = new Json();
                json.put("wasSuccess", 1);
                json.put("errorMessage", null);
                json.put("object", parameters.get("objectId"));
                return json;
            }
        });

        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", baseUrl + "objects/"+ objectId);

        // Action
        final ApiRoute<AuthenticatedJsonRequestHandler> route = apiUrlRouter.route(fakeRequest, BaseServlet.HttpMethod.GET);
        final Json json = route.getRequestHandler().handleAuthenticatedRequest(route.getParameters(), fakeRequest, BaseServlet.HttpMethod.GET, masterAccount, new FakeEnvironment());

        // Assert
        Assert.assertEquals(objectJson.toString(), json.toString());
    }

    @Test
    public void should_return_delegate_response_to_matched_path_with_multiple_parameters() throws Exception {
        // Setup
        final String baseUrl = "/api/v1/";

        final ApiUrlRouter<AuthenticatedJsonRequestHandler> apiUrlRouter = new ApiUrlRouter<AuthenticatedJsonRequestHandler>(baseUrl, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return null;
            }
        });

        final String objectId = "1";
        final String childId = "1";

        final Json objectJson = new Json();
        objectJson.put("wasSuccess", 1);
        objectJson.put("errorMessage", null);
        objectJson.put("object", objectId);
        objectJson.put("child", childId);

        apiUrlRouter.defineEndpoint("objects/<objectId>/children/<childId>", BaseServlet.HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                final Json json = new Json();
                json.put("wasSuccess", 1);
                json.put("errorMessage", null);
                json.put("object", parameters.get("objectId"));
                json.put("child", parameters.get("childId"));
                return json;
            }
        });

        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", baseUrl + "objects/"+ objectId +"/children/"+ childId);

        // Action
        final ApiRoute<AuthenticatedJsonRequestHandler> route = apiUrlRouter.route(fakeRequest, BaseServlet.HttpMethod.GET);
        final Json json = route.getRequestHandler().handleAuthenticatedRequest(route.getParameters(), fakeRequest, BaseServlet.HttpMethod.GET, masterAccount, new FakeEnvironment());

        // Assert
        Assert.assertEquals(objectJson.toString(), json.toString());
    }

    @Test
    public void should_return_error_response_to_matched_path_with_different_http_method() throws Exception {
        // Setup
        final Json errorJson = new Json();
        errorJson.put("wasSuccess", 0);
        errorJson.put("errorMessage", "Invalid endpoint.");

        final String baseUrl = "/api/v1/";
        final ApiUrlRouter<AuthenticatedJsonRequestHandler> apiUrlRouter = new ApiUrlRouter<AuthenticatedJsonRequestHandler>(baseUrl, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return errorJson;
            }
        });

        apiUrlRouter.defineEndpoint("objects", BaseServlet.HttpMethod.POST, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                final Json json = new Json();
                json.put("wasSuccess", 1);
                json.put("errorMessage", null);
                return json;
            }
        });

        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", baseUrl + "objects");

        // Action
        final ApiRoute<AuthenticatedJsonRequestHandler> route = apiUrlRouter.route(fakeRequest, BaseServlet.HttpMethod.GET);
        final Json json = route.getRequestHandler().handleAuthenticatedRequest(route.getParameters(), fakeRequest, BaseServlet.HttpMethod.GET, masterAccount, new FakeEnvironment());

        // Assert
        Assert.assertEquals(errorJson.toString(), json.toString());
    }

    @Test
    public void should_return_delegate_response_to_matched_path_with_trailing_slash() throws Exception {
        // Setup
        final String baseUrl = "/api/v1/";
        final ApiUrlRouter<AuthenticatedJsonRequestHandler> apiUrlRouter = new ApiUrlRouter<AuthenticatedJsonRequestHandler>(baseUrl, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return null;
            }
        });

        final Json objectJson = new Json();
        objectJson.put("wasSuccess", 1);
        objectJson.put("errorMessage", null);
        objectJson.put("object", "Object Value");

        apiUrlRouter.defineEndpoint("objects", BaseServlet.HttpMethod.GET, new AuthenticatedJsonRequestHandler() {
            @Override
            public Json handleAuthenticatedRequest(final Map<String, String> parameters, final HttpServletRequest request, final BaseServlet.HttpMethod httpMethod, final Account currentAccount, final Environment environment) throws Exception {
                return objectJson;
            }
        });

        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", baseUrl + "objects/");

        // Action
        final ApiRoute<AuthenticatedJsonRequestHandler> route = apiUrlRouter.route(fakeRequest, BaseServlet.HttpMethod.GET);
        final Json json = route.getRequestHandler().handleAuthenticatedRequest(route.getParameters(), fakeRequest, BaseServlet.HttpMethod.GET, masterAccount, new FakeEnvironment());

        // Assert
        Assert.assertEquals(objectJson.toString(), json.toString());
    }
}
