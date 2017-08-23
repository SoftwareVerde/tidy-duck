package com.softwareverde.tomcat;

import com.softwareverde.test.utils.Mock;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;

public class FakeRequest implements HttpServletRequest {
    public final Mock _mock = new Mock();

    @Override
    public String getAuthType() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public Cookie[] getCookies() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public long getDateHeader(final String name) {
        _mock.recordInvocation(name);
        return _mock.getReturnValue();
    }

    @Override
    public String getHeader(final String name) {
        _mock.recordInvocation(name);
        return _mock.getReturnValue();
    }

    @Override
    public Enumeration<String> getHeaders(final String name) {
        _mock.recordInvocation(name);
        return _mock.getReturnValue();
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public int getIntHeader(final String name) {
        _mock.recordInvocation(name);
        return _mock.getReturnValue();
    }

    @Override
    public String getMethod() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getPathInfo() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getPathTranslated() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getContextPath() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getQueryString() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getRemoteUser() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public boolean isUserInRole(final String role) {
        _mock.recordInvocation(role);
        return _mock.getReturnValue();
    }

    @Override
    public Principal getUserPrincipal() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getRequestedSessionId() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getRequestURI() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public StringBuffer getRequestURL() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getServletPath() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public HttpSession getSession(final boolean create) {
        _mock.recordInvocation(create);
        return _mock.getReturnValue();
    }

    @Override
    public HttpSession getSession() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Deprecated
    @Override
    public boolean isRequestedSessionIdFromUrl() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public boolean authenticate(final HttpServletResponse response) throws IOException, ServletException {
        _mock.recordInvocation(response);
        return _mock.getReturnValue();
    }

    @Override
    public void login(final String username, final String password) throws ServletException {
        _mock.recordInvocation(username, password);
    }

    @Override
    public void logout() throws ServletException {
        _mock.recordInvocation();
    }

    @Override
    public Collection<Part> getParts() throws IOException, IllegalStateException, ServletException {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public Part getPart(final String name) throws IOException, IllegalStateException, ServletException {
        _mock.recordInvocation(name);
        return _mock.getReturnValue();
    }

    @Override
    public Object getAttribute(final String name) {
        _mock.recordInvocation(name);
        return _mock.getReturnValue();
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getCharacterEncoding() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public void setCharacterEncoding(final String env) throws UnsupportedEncodingException {
        _mock.recordInvocation(env);
    }

    @Override
    public int getContentLength() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getContentType() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getParameter(final String name) {
        _mock.recordInvocation(name);
        return _mock.getReturnValue();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String[] getParameterValues(final String name) {
        _mock.recordInvocation(name);
        return _mock.getReturnValue();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getProtocol() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getScheme() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getServerName() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public int getServerPort() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public BufferedReader getReader() throws IOException {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getRemoteAddr() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getRemoteHost() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public void setAttribute(final String name, final Object o) {
        _mock.recordInvocation(name, o);
    }

    @Override
    public void removeAttribute(final String name) {
        _mock.recordInvocation(name);
    }

    @Override
    public Locale getLocale() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public boolean isSecure() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public RequestDispatcher getRequestDispatcher(final String path) {
        _mock.recordInvocation(path);
        return _mock.getReturnValue();
    }

    @Deprecated
    @Override
    public String getRealPath(final String path) {
        _mock.recordInvocation(path);
        return _mock.getReturnValue();
    }

    @Override
    public int getRemotePort() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getLocalName() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public String getLocalAddr() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public int getLocalPort() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public ServletContext getServletContext() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public AsyncContext startAsync() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public AsyncContext startAsync(final ServletRequest servletRequest, final ServletResponse servletResponse) {
        _mock.recordInvocation(servletRequest, servletResponse);
        return _mock.getReturnValue();
    }

    @Override
    public boolean isAsyncStarted() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public boolean isAsyncSupported() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public AsyncContext getAsyncContext() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }

    @Override
    public DispatcherType getDispatcherType() {
        _mock.recordInvocation();
        return _mock.getReturnValue();
    }
}
