package com.softwareverde.security;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class SecureHashUtil {

    public static String hashWithPbkdf2 (final String s) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final int iterations = 4096;
        final int randomizedIterations = (int)(iterations + Math.round(iterations * Math.random()));
        final int keyLength = 512;

        final char[] passwordChars = s.toCharArray();
        final byte[] salt = _getSalt();

        final PBEKeySpec pbeKeySpec = new PBEKeySpec(passwordChars, salt, randomizedIterations, keyLength);
        final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        final byte [] hash = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();

        return randomizedIterations + ":" + _toHex(salt) + ":" + _toHex(hash);
    }

    public static boolean validateHashWithPbkdf2 (final String s, final String h) throws NoSuchAlgorithmException, InvalidKeySpecException {
        final String[] parts = h.split(":");
        final int iterations = Integer.parseInt(parts[0]);
        final byte[] salt = _fromHex(parts[1]);
        final byte[] hash = _fromHex(parts[2]);

        final PBEKeySpec pbeKeySpec = new PBEKeySpec(s.toCharArray(), salt, iterations, hash.length * 8);
        final SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        final byte[] testHash = secretKeyFactory.generateSecret(pbeKeySpec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }

        return diff == 0;
     }

    private static byte[] _getSalt() throws NoSuchAlgorithmException {
        final SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        final byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    private static String _toHex(final byte[] array) throws NoSuchAlgorithmException {
        final BigInteger bigInteger = new BigInteger(1, array);
        final String hex = bigInteger.toString(16);

        final int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        }
        else {
            return hex;
        }
    }

    private static byte[] _fromHex(final String hex) throws NoSuchAlgorithmException {
        final byte[] hexBytes = new byte[hex.length() / 2];
        for (int i = 0; i < hexBytes.length; i++) {
            hexBytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return hexBytes;
    }
}
