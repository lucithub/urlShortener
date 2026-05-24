package com.example.urlshortener.util;

import java.security.SecureRandom;

public final class Base62 {

    private static final char[] ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final SecureRandom RANDOM = new SecureRandom();

    public static final int DEFAULT_CODE_LENGTH = 7;

    private Base62() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static String generateCode() {
        return generateCode(DEFAULT_CODE_LENGTH);
    }

    public static String generateCode(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be positive");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(ALPHABET[RANDOM.nextInt(ALPHABET.length)]);
        }
        return sb.toString();
    }
}
