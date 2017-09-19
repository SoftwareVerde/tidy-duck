package com.softwareverde.security;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SecureHashUtilTests {
    protected String _originalPassword;
    protected String _badPassword1;
    protected String _badPassword2;
    protected String _badPassword3;
    protected String _verbosePassword;
    protected String _tinyPassword;

    protected void _printGeneratedHash(final String s, final boolean isOriginalHash) {
        final String message = isOriginalHash ? "Original string's hash: " : "Proposed string's hash: ";
        System.out.println(message + s);
    }

    @Before
    public void setup() throws Exception {
        _originalPassword = "b!gB@ll0fF!ERYde@th";
        _badPassword1 = _originalPassword.replace("!", "i");
        _badPassword2 = _originalPassword.toLowerCase();
        _badPassword3 = _originalPassword.split("E")[0];
        _verbosePassword = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Nunc pharetra libero vel ultricies efficitur. Cras imperdiet eu diam sed blandit. Suspendisse dapibus malesuada suscipit. Donec eu commodo felis. Aenean rhoncus laoreet lectus, a imperdiet velit fermentum vitae. Integer fringilla elit semper, iaculis arcu ac, auctor lorem. Vivamus ac risus sed sapien accumsan fringilla eu feugiat sem.";
        _tinyPassword = "a";
    }

    @Test
    public void should_hash_and_successfully_validate_original_password() throws Exception {
        // Setup
        final String originalPasswordHash = SecureHashUtil.hashWithPbkdf2(_originalPassword);

        // Action
        final boolean validationWasSuccessful = SecureHashUtil.validateHashWithPbkdf2(_originalPassword, originalPasswordHash);
        _printGeneratedHash(originalPasswordHash, true);

        // Assert
        Assert.assertEquals(true, validationWasSuccessful);

    }

    @Test
    public void should_hash_with_many_iterations_and_successfully_validate_original_password() throws Exception {
        // Setup
        final String originalPasswordHash = SecureHashUtil.hashWithPbkdf2(_originalPassword, 65536, 512);

        // Action
        final boolean validationWasSuccessful = SecureHashUtil.validateHashWithPbkdf2(_originalPassword, originalPasswordHash);
        _printGeneratedHash(originalPasswordHash, true);

        // Assert
        Assert.assertEquals(true, validationWasSuccessful);

    }

    @Test
    public void should_return_false_for_an_invalid_hash() throws Exception {
        // Setup
        final String invalidHash = "1234";

        // Action
        final boolean validationWasSuccessful = SecureHashUtil.validateHashWithPbkdf2(_originalPassword, invalidHash);

        // Assert
        Assert.assertEquals(false, validationWasSuccessful);

    }

    @Test
    public void should_return_false_for_an_invalid_hash_with_colons() throws Exception {
        // Setup
        final String invalidHash = "1:2:3:4:5:6:7:8:9";

        // Action
        final boolean validationWasSuccessful = SecureHashUtil.validateHashWithPbkdf2(_originalPassword, invalidHash);

        // Assert
        Assert.assertEquals(false, validationWasSuccessful);

    }

    @Test
    public void should_hash_and_fail_validation() throws Exception {
        // Setup
        final String originalPasswordHash = SecureHashUtil.hashWithPbkdf2(_originalPassword);

        // Action
        final boolean validationWasSuccessful1 = SecureHashUtil.validateHashWithPbkdf2(_badPassword1, originalPasswordHash);
        final boolean validationWasSuccessful2 = SecureHashUtil.validateHashWithPbkdf2(_badPassword2, originalPasswordHash);
        final boolean validationWasSuccessful3 = SecureHashUtil.validateHashWithPbkdf2(_badPassword3, originalPasswordHash);

        // Assert
        Assert.assertEquals(false, validationWasSuccessful1);
        Assert.assertEquals(false, validationWasSuccessful2);
        Assert.assertEquals(false, validationWasSuccessful3);
    }

    @Test
    public void should_hash_and_successfully_validate_verbose_password() throws Exception {
        // Setup
        final String originalPasswordHash = SecureHashUtil.hashWithPbkdf2(_verbosePassword);

        // Action
        final boolean validationWasSuccessful = SecureHashUtil.validateHashWithPbkdf2(_verbosePassword, originalPasswordHash);

        // Assert
        Assert.assertEquals(true, validationWasSuccessful);
    }

    @Test
    public void should_hash_and_successfully_validate_tiny_password() throws Exception {
        // Setup
        final String originalPasswordHash = SecureHashUtil.hashWithPbkdf2(_tinyPassword);

        // Action
        final boolean validationWasSuccessful = SecureHashUtil.validateHashWithPbkdf2(_tinyPassword, originalPasswordHash);

        // Assert
        Assert.assertEquals(true, validationWasSuccessful);
    }
}
