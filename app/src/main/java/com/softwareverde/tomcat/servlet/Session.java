package com.softwareverde.tomcat.servlet;

import com.softwareverde.util.Util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class Session {
    protected static final String SESSION_ACCOUNT_ID_KEY = "account_id";

    public static Long getAccountId(final HttpServletRequest request) {
        final HttpSession session = request.getSession();
        final Long accountId = (Long) session.getAttribute(SESSION_ACCOUNT_ID_KEY);
        return Util.coalesce(accountId);
    }

    public static Boolean isAuthenticated(final HttpServletRequest request) {
        return (Util.coalesce(getAccountId(request)) > 0);
    }

    public static void setAccountId(final Long accountId, final HttpServletRequest request) {
        final HttpSession session = request.getSession();
        session.setAttribute(SESSION_ACCOUNT_ID_KEY, accountId);
    }
}
