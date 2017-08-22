package com.softwareverde.tomcat.servlet;

import com.softwareverde.tomcat.FakeRequest;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class BaseServletTests {
    @Test
    public void getApiPath_should_decode_consecutive_string_segments_to_keys() {
        // Setup
        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", "/api/v1/objects");

        // Action
        final Map<String, Long> apiPath = BaseServlet.getApiPath(fakeRequest);

        // Assert
        Assert.assertTrue(apiPath.containsKey("api"));
        Assert.assertEquals(apiPath.get("api"), null);

        Assert.assertTrue(apiPath.containsKey("v1"));
        Assert.assertEquals(apiPath.get("v1"), null);

        Assert.assertTrue(apiPath.containsKey("objects"));
        Assert.assertEquals(apiPath.get("objects"), null);
    }

    @Test
    public void getApiPath_should_decode_segments_to_keys_and_values() {
        // Setup
        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", "/api/v1/objects/1/children/2");

        // Action
        final Map<String, Long> apiPath = BaseServlet.getApiPath(fakeRequest);

        // Assert
        Assert.assertTrue(apiPath.containsKey("api"));
        Assert.assertEquals(apiPath.get("api"), null);

        Assert.assertTrue(apiPath.containsKey("v1"));
        Assert.assertEquals(apiPath.get("v1"), null);

        Assert.assertTrue(apiPath.containsKey("objects"));
        Assert.assertEquals(apiPath.get("objects").longValue(), 1L);

        Assert.assertTrue(apiPath.containsKey("children"));
        Assert.assertEquals(apiPath.get("children").longValue(), 2L);
    }

    @Test
    public void getApiPath_should_return_null_given_an_invalid_path() {
        // Setup
        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", "/api/v1/objects/1/2");

        // Action
        final Map<String, Long> apiPath = BaseServlet.getApiPath(fakeRequest);

        // Assert
        Assert.assertNull(apiPath);
    }


    @Test
    public void getApiPath_should_return_null_given_an_invalid_path2() {
        // Setup
        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", "/api/v1/objects/1/2/3/");

        // Action
        final Map<String, Long> apiPath = BaseServlet.getApiPath(fakeRequest);

        // Assert
        Assert.assertNull(apiPath);
    }

    @Test
    public void getApiPath_should_return_an_empty_map_on_root_path() {
        // Setup
        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", "/");

        // Action
        final Map<String, Long> apiPath = BaseServlet.getApiPath(fakeRequest);

        // Assert
        Assert.assertTrue(apiPath.isEmpty());
    }

    @Test
    public void getApiPath_should_ignore_consecutive_slashes() {
// Setup
        final FakeRequest fakeRequest = new FakeRequest();
        fakeRequest._mock.setReturnValue("getRequestURI", "/api//v1///objects////1/////children//////2");

        // Action
        final Map<String, Long> apiPath = BaseServlet.getApiPath(fakeRequest);

        // Assert
        Assert.assertTrue(apiPath.containsKey("api"));
        Assert.assertEquals(apiPath.get("api"), null);

        Assert.assertTrue(apiPath.containsKey("v1"));
        Assert.assertEquals(apiPath.get("v1"), null);

        Assert.assertTrue(apiPath.containsKey("objects"));
        Assert.assertEquals(apiPath.get("objects").longValue(), 1L);

        Assert.assertTrue(apiPath.containsKey("children"));
        Assert.assertEquals(apiPath.get("children").longValue(), 2L);
    }
}
