package com.softwareverde.tidyduck;

/**
 * <p>An AuthorizationException is thrown when a user attempts to perform
 * an action for which it is not authorized.</p>
 */
public class AuthorizationException extends Exception {

    public AuthorizationException(final String message) {
        super(message);
    }

    public AuthorizationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
