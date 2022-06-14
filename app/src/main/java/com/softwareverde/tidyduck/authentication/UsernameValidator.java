package com.softwareverde.tidyduck.authentication;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.ArrayList;
import java.util.List;

public class UsernameValidator {
    private final int _requiredLength;

    public UsernameValidator() {
        this(8);
    }

    public UsernameValidator(final int requiredLength) {
        _requiredLength = requiredLength;
    }

    public List<String> validateUsername(final CharSequence username) {
        final List<String> errors = new ArrayList<>(1);

        if (username.length() < _requiredLength) {
            errors.add("Username must be contain least " + _requiredLength + " characters.");
        }

        final EmailValidator emailValidator = EmailValidator.getInstance();
        if (!emailValidator.isValid(username.toString())) {
            errors.add("Username is not a valid email address");
        }

        return errors;
    }
}
