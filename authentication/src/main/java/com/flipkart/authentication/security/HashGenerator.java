package com.flipkart.authentication.security;

import com.flipkart.authentication.exceptions.AlgorithmNotFoundException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * <p>
 * Hashes the give data using the specified hashing algorithm.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class HashGenerator {

    private static HashGenerator hashGenerator;

    /**
     * <p>
     * Default constructor of HashGenerator class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private HashGenerator() {}

    /**
     * <p>
     * Creates a single instance of HashGenerator class.
     * </p>
     */
    private static class InstanceHolder {

        private static final HashGenerator HASH_GENERATOR = new HashGenerator();
    }

    /**
     * <p>
     * Gets a single instance of HashGenerator class and returns it.
     * </p>
     *
     * @return the single instance of HashGenerator class.
     */
    public static HashGenerator getInstance() {
        return InstanceHolder.HASH_GENERATOR;
    }

    /**
     * <p>
     * Hashes the given string value and returns it.
     * </p>
     *
     * @param value Refers the value to be hashed.
     * @return the encrypted value.
     */
    public String hash(final String value) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA3-256");

            messageDigest.update(value.getBytes());
            final byte[] hashInBytes = messageDigest.digest();
            final StringBuilder hashedValue = new StringBuilder();

            for (final byte hashByte : hashInBytes) {
                hashedValue.append(String.format("%x", hashByte));
            }

            return hashedValue.substring(1,30);
        } catch (NoSuchAlgorithmException exception) {
            throw new AlgorithmNotFoundException(exception.getMessage());
        }
    }
}
