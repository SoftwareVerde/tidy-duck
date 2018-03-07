package com.softwareverde.tidyduck.api;

import com.softwareverde.database.Database;
import com.softwareverde.database.DatabaseConnection;
import com.softwareverde.json.Json;
import com.softwareverde.tidyduck.TestBase;
import com.softwareverde.tidyduck.database.TestDataLoader;
import com.softwareverde.tidyduck.environment.Environment;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.util.Arrays;

public class ReviewServletTests extends TestBase {
    private DatabaseConnection<Connection> _databaseConnection;
    private long reviewId;
    private long functionCatalogId;

    @Before
    public void setup() throws Exception {
        super.setup();
        _databaseConnection = TestBase._database.newConnection();

        functionCatalogId = TestDataLoader.insertFakeCompleteFunctionCatalog(_databaseConnection);
        reviewId = TestDataLoader.insertFakeReview(_databaseConnection, functionCatalogId, null, null, 1L);
    }

    @Test
    public void should_add_comment_when_max_length() throws ServletException, IOException {
        // Setup
        final String maxLengthString = getLargeString(65535);
        final Json commentJson = new Json(false);
        commentJson.put("commentText", maxLengthString);
        final Json requestJson = new Json(false);
        requestJson.put("reviewComment", commentJson);

        final ReviewServlet reviewServlet = getMockReviewServlet();

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/reviews/" + reviewId + "/comments");
        final InputStream inputStream = new ByteArrayInputStream(requestJson.toString().getBytes());
        Mockito.when(request.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        });
        final HttpSession mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockSession.getAttribute("account_id")).thenReturn(1L);
        Mockito.when(request.getSession()).thenReturn(mockSession);

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter responseStringWriter = new StringWriter();
        PrintWriter responseWriter = new PrintWriter(responseStringWriter);

        Mockito.when(response.getWriter()).thenReturn(responseWriter);

        // Action
        reviewServlet.service(request, response);

        // Assert
        responseWriter.close();
        final String responseString = responseStringWriter.getBuffer().toString();
        final Json json = Json.parse(responseString);
        Assert.assertEquals("true", json.getString("wasSuccess"));
    }

     /*
      * TODO: figure out how to get this test working.
      *       It appears that H2 aliases TEXT to CLOB which is much larger.
      *       http://www.h2database.com/html/datatypes.html#clob_type
      */
    //@Test
    public void should_not_add_comment_when_text_too_large() throws ServletException, IOException {
        // Setup
        final String tooLargeString = getLargeString(65537);
        final Json commentJson = new Json(false);
        commentJson.put("commentText", tooLargeString);
        final Json requestJson = new Json(false);
        requestJson.put("reviewComment", commentJson);

        final ReviewServlet reviewServlet = getMockReviewServlet();

        final HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Mockito.when(request.getMethod()).thenReturn("POST");
        Mockito.when(request.getRequestURI()).thenReturn("/api/v1/reviews/" + reviewId + "/comments");
        final InputStream inputStream = new ByteArrayInputStream(requestJson.toString().getBytes());
        Mockito.when(request.getInputStream()).thenReturn(new ServletInputStream() {
            @Override
            public int read() throws IOException {
                return inputStream.read();
            }
        });
        final HttpSession mockSession = Mockito.mock(HttpSession.class);
        Mockito.when(mockSession.getAttribute("account_id")).thenReturn(1L);
        Mockito.when(request.getSession()).thenReturn(mockSession);

        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
        StringWriter responseStringWriter = new StringWriter();
        PrintWriter responseWriter = new PrintWriter(responseStringWriter);
        Mockito.when(response.getWriter()).thenReturn(responseWriter);

        // Action
        reviewServlet.service(request, response);

        // Assert
        responseWriter.close();
        final String responseString = responseStringWriter.getBuffer().toString();
        final Json json = Json.parse(responseString);
        Assert.assertEquals("false", json.getString("wasSuccess"));
    }

    private String getLargeString(final int length) {
        char[] chars = new char[length];
        Arrays.fill(chars, 'x');

        final String longString = new String(chars);
        return longString;
    }

    private ReviewServlet getMockReviewServlet() {
        final Environment mockEnvironment = Mockito.mock(Environment.class);
        final Database<Connection> mockDatabase = Mockito.mock(Database.class);
        try {
            Mockito.when(mockDatabase.newConnection()).thenReturn(_databaseConnection);
        } catch (Exception e) {}
        Mockito.when(mockEnvironment.getDatabase()).thenReturn(mockDatabase);

        ReviewServlet reviewServlet = new ReviewServlet() {
            @Override
            protected void handleRequest(HttpServletRequest request, HttpServletResponse response, HttpMethod method) throws ServletException, IOException {
                try {
                    this.handleRequest(request, response, method, mockEnvironment);
                } catch (final Exception e) {
                    e.printStackTrace(System.err);
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    PrintWriter writer = response.getWriter();
                    writer.append("Server error.");
                }
            }
        };

        return reviewServlet;
    }

}
