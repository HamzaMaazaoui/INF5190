package com.inf5190.chat.auth.session;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class SessionDataAccessor {
    private static final String TOKEN_KEY = "TOKEN_KEY";
    private static final String SESSION_DATA_KEY = "SESSION_DATA_KEY";

    public void setSessionData(HttpServletRequest request, SessionData sessionData) {
        request.setAttribute(SESSION_DATA_KEY, sessionData);
    }

    public void setToken(HttpServletRequest request, String token) {
        request.setAttribute(TOKEN_KEY, token);
    }

    public SessionData getSessionData(HttpServletRequest request) {
        return (SessionData) request.getAttribute(SESSION_DATA_KEY);
    }

    public String getToken(HttpServletRequest request) {
        return (String) request.getAttribute(TOKEN_KEY);
    }
}
