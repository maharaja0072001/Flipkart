package org.abc.authentication.security;

import org.abc.authentication.exceptions.AlgorithmNotFoundException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * <p>
 * Starts and stops the osgi bundle.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class Encryptor {

    private static Encryptor encryptor;

    /**
     * <p>
     * Default constructor of Encryptor class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private Encryptor() {}

    /**
     * <p>
     * Creates a single object of Encryptor class and returns it.
     * </p>
     *
     * @return the single instance of Encryptor class.
     */
    public static Encryptor getInstance() {
        return Objects.isNull(encryptor) ? encryptor = new Encryptor() : encryptor;
    }

    /**
     * <p>
     * Encrypts the given string value and returns it.
     * </p>
     *
     * @param value Refers the value to be hashed.
     * @return the encrypted value.
     */
    public String encrypt(final String value) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA3-256");

            messageDigest.update(value.getBytes());
            final byte[] hashInBytes = messageDigest.digest();
            final StringBuilder encryptedValue = new StringBuilder();

            for (final byte hashByte : hashInBytes) {
                encryptedValue.append(String.format("%02x", hashByte));
            }

            return encryptedValue.substring(1,30);
        } catch (NoSuchAlgorithmException exception) {
            throw new AlgorithmNotFoundException(exception.getMessage());
        }
    }
}
