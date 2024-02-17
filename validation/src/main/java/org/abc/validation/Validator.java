package org.abc.validation;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * <p>
 * Validates the input given by the users
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class Validator {

    private static Validator validator;

    /**
     * <p>
     * Default constructor of UserDataValidator class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private Validator() {}

    /**
     * <p>
     * Creates a single object of UserDataValidator class and returns it.
     * </p>
     *
     * @return the single instance of UserDataValidator class.
     */
    public static Validator getInstance() {
        return Objects.isNull(validator) ? validator = new Validator() : validator;
    }

    /**
     * <p>
     * Validates the name of the user by the given regular expression pattern.
     * </p>
     *
     * @param name Refers the name entered by the user to ba validated.
     * @return true if the entered name is valid or false otherwise.
     */
    public boolean isValidName(final String name) {
        return Pattern.matches("^[A-Za-z][A-Za-z\\s]{2,30}$", name);
    }

    /**
     * <p>
     * Validates the password of the user by the given regular expression pattern.
     * </p>
     *
     * @param password Refers the password entered by the user to be validated.
     * @return true if the entered password is valid or false otherwise.
     */
    public boolean isValidPassword(final String password) {
        return Pattern.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$", password);
    }

    /**
     * <p>
     * Validates the email id of the user by the given regular expression pattern.
     * </p>
     *
     * @param emailId Refers the email id entered by the user to be validated.
     * @return true if the entered email id is valid or false otherwise.
     */
    public boolean isValidEmail(final String emailId) {
        return Pattern.matches("^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*@[A-Za-z0-9]{2,}([.][a-zA-Z0-9]{2,})+$", emailId);
    }

    /**
     * <p>
     * Validates the mobile number by the given regular expression pattern.
     * </p>
     *
     * @param mobileNumber Refers the mobile number entered by the user to be validated.
     * @param choice the choice of the user for country code.
     * @return true if the entered mobile number is valid, otherwise false.
     */
    public boolean isValidMobileNumber(final String mobileNumber, final int choice) {
        final String forAmerica = "^1[2-9]\\d{2}[2-9]\\d{2}\\d{4}$";
        final String forAustralia = "^61(4|04)\\d{8}$";
        final String forGermany = "^491[5-79]\\d{9}$";
        final String forChina = "^861[3-9][0-9]{9}$";
        final String forIndia = "^91[6789]\\d{9}$";

        return switch (choice) {
            case 1 -> Pattern.matches(forAmerica, mobileNumber);
            case 2 -> Pattern.matches(forAustralia, mobileNumber);
            case 3 -> Pattern.matches(forChina, mobileNumber);
            case 4 -> Pattern.matches(forGermany, mobileNumber);
            case 5 -> Pattern.matches(forIndia, mobileNumber);
            default -> false;
        };
    }

    /**
     * <p>
     * Checks whether the given input indicates to navigate back.
     * </p>
     *
     * @param input Refers the input to be checked for the presence of the character.
     * @return true if the input indicates to navigate back , otherwise false.
     */
    public boolean checkToGoBack(final String input) {
        return "$".equals(input);
    }

    /**
     * <p>
     * Checks if the given input grants access to show filter menu.
     * </p>
     *
     * @param input Refers the input to be checked.
     * @return true if the input grants access to show filter menu or false otherwise.
     */
    public boolean toShowFilterMenu(final String input) {
        return "#".equals(input);
    }

    /**
     * <p>
     * Checks if the given input grants access to proceed.
     * </p>
     *
     * @param input Refers the input to be checked.
     * @return true if the input grants access to proceed.
     */
    public boolean hasAccessToProceed(final String input) {
        return "YES".equalsIgnoreCase(input);
    }

    /**
     * <p>
     * Checks if the given input grants access to proceed.
     * </p>
     *
     * @param input Refers the input to be checked.
     * @return true if the input grants access to proceed.
     */
    public boolean isPositiveNumber(final String input) {
        return Pattern.matches("^[1-9]+$", input);
    }
}
