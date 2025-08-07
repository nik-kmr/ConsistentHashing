package com.consistenthashing.util;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class HashUtil {
    public static long getHash(final String key) {
        try {
            final MessageDigest digest = MessageDigest.getInstance("SHA-256");
            final byte[] hashBytes = digest.digest(key.getBytes(StandardCharsets.UTF_8));

            // Use the first 8 bytes to convert to a long (big-endian)
            return bytesToLong(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    private static long bytesToLong(final byte[] bytes) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value <<= 8;
            value |= (bytes[i] & 0xFF);
        }
        return value & 0x7FFFFFFFFFFFFFFFL; // ensure non-negative
    }
}
