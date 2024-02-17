package org.abc.authentication.view;

import org.abc.authentication.controller.UserController;
import org.abc.authentication.model.User;
import org.abc.authentication.security.Encryptor;
import org.abc.pageview.PageViewer;
import org.abc.singleton_scanner.SingletonScanner;
import org.abc.validation.Validator;

import java.util.Objects;
import java.util.Scanner;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * <p>
 * Handles user creation and user authentication.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
public class AuthenticationView {

    private static AuthenticationView authenticationView;
    private final Scanner scanner;
    private final Logger logger;
    private final UserController userController;
    private final Validator validator;
    private final Encryptor encryptor;
    private PageViewer homePageView;

    /**
     * <p>
     * Default constructor of AuthenticationView class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private AuthenticationView() {
        scanner = SingletonScanner.getScanner();
        logger = LogManager.getLogger(AuthenticationView.class);
        userController = UserController.getInstance();
        validator = Validator.getInstance();
        encryptor = Encryptor.getInstance();
    }

    /**
     * <p>
     * Creates a single object of AuthenticationView class and returns it.
     * </p>
     *
     * @return the single instance of AuthenticationView class.
     */
    public static AuthenticationView getInstance() {
        return Objects.isNull(authenticationView) ? authenticationView = new AuthenticationView() : authenticationView;
    }

    /**
     * <p>
     * Shows the authentication page for user login and signup and responsible for creating new user and authenticating the existing user.
     * </p>
     */
    public void showAuthenticationPage() {
        logger.info("1.Signup\n2.Login\n3.Exit\nEnter your choice:");

        switch (getChoice()) {
            case 1:
                signUp();
                break;
            case 2:
                login();
                break;
            case 3:
                exit();
            default:
               logger.warn("Enter a valid choice");
               showAuthenticationPage();
        }
    }

    /**
     * <p>
     * Exits the application.
     * </p>
     */
    private void exit() {
        logger.info("Exiting application");
        scanner.close();
        System.exit(0);
    }

    /**
     * <p>
     * Sets the HomePageView instance.
     * </p>
     *
     * @param homePageView Refers the HomePageView instance.
     */
    public void setHomePageView(final PageViewer homePageView) {
        this.homePageView = homePageView;
    }

    /**
     * <p>
     * Creates a new user by getting details from the user.
     * </p>
     *
     */
    private void signUp() {
        final User user = new User();

        logger.info("Enter the details to create new user or press '$' to go back");
        user.setName(getName());
        user.setMobileNumber(getMobileNumber());
        user.setEmailId(getEmailId());
        user.setPassword(encryptor.encrypt(getPassword()));

        if (userController.createUser(user)) {
            logger.info("User created successfully");
            homePageView.viewPage(user);
        } else {
            logger.info("User already exists. Please login");
            login();
        }
    }

    /**
     * <p>
     * Authenticates the existing user by the credentials entered by the user.
     * </p>
     */
    private void login() {
        logger.info("Login : [Press '$' to go back] \n1.Mobile number\n2.Email Id \nEnter your choice:");
        final int choice = getChoice();

        toGoBack(choice);

        final String emailIdOrMobileNumber = switch (choice) {
            case 1 -> getMobileNumber();
            case 2 -> getEmailId();
            default -> {
                logger.warn("Enter a valid choice");
                login();
                yield null;
            }
        };

        logger.info("Enter the Password :");
        final String password = encryptor.encrypt(scanner.nextLine());

        toGoBack(password);
        final User user = userController.getUser(emailIdOrMobileNumber, password);

        if (Objects.nonNull(user)) {
            logger.info("Login successful");
            homePageView.viewPage(user);
        } else {
            logger.warn("Wrong credentials or user doesn't exist.");
            login();
        }
    }

    /**
     * <p>
     * Shows the profile of the user and updates the details chosen by the user.
     * </p>
     *
     * @param user Refers the current {@link User}
     */
    public void viewAndEditProfile(final User user) {
        logger.info(String.format("User profile:\nName : %s\nEmail Id : %s\nMobile : +%s\nEnter 'yes' to edit details or press $ to go back",
                user.getName(), user.getEmailId(), user.getMobileNumber()));
        final String choice = scanner.nextLine().trim();

        if (validator.checkToGoBack(choice)) {
            homePageView.viewPage(user);
        }

        if (validator.hasAccessToProceed(choice)) {
            userController.updateDetails(editUserDetails(user));
            logger.info(String.format("User Id : %d - User details updated", user.getId()));
        } else {
            viewAndEditProfile(user);
        }
    }

    /**
     * <p>
     * Updates the details of the current user.
     * </p>
     *
     * @param user Refers the current {@link User}
     */
    private User editUserDetails(final User user) {
        logger.info("Do you want to edit name ?. Enter 'yes' to proceed or any key to no.");
        user.setName(validator.hasAccessToProceed(scanner.nextLine().trim()) ? getName() : user.getName());
        logger.info("Do you want to edit email id ?. Enter 'yes' to proceed or any key to no.");
        user.setEmailId(validator.hasAccessToProceed(scanner.nextLine().trim()) ? getEmailId() : user.getEmailId());
        logger.info("Do you want to edit mobile number ?. Enter 'yes' to proceed or any key to no.");
        user.setMobileNumber(validator.hasAccessToProceed(scanner.nextLine().trim()) ? getMobileNumber() : user.getMobileNumber());
        logger.info("Do you want to edit password ?. Enter 'yes' to proceed or any key to no.");
        user.setPassword(validator.hasAccessToProceed(scanner.nextLine().trim()) ? encryptor.encrypt(getPassword()) : user.getPassword());

        return user;
    }

    /**
     * <p>
     * Gets a valid name from the user and returns it.
     * </p>
     *
     * @return the name of the user.
     */
    private String getName() {
        logger.info("Enter your name :");
        final String name = scanner.nextLine().trim();

        toGoBack(name);

        if (validator.isValidName(name)) {
            return name;
        } else {
            logger.warn("Entered Name is invalid");
        }

        return getName();
    }

    /**
     * <p>
     * Gets a valid password from the user and returns it.
     * </p>
     *
     * @return the password of the user.
     */
    private String getPassword() {
        logger.info(String.join(" ", "Create a password:[Password should contain",
                "an uppercase, a lowercase, a special character and a digit. Minimum length is 8]"));
        final String password = scanner.nextLine().trim();

        toGoBack(password);

        if (validator.isValidPassword(password)) {
            return password;
        } else {
            logger.warn("Entered password is invalid");
        }

        return getPassword();
    }

    /**
     * <p>
     * Gets a valid email id from the user and returns it.
     * </p>
     *
     * @return the email id of the user.
     */
    private String getEmailId() {
        logger.info("Enter your email id:");
        final String emailId = scanner.nextLine().trim();

        toGoBack(emailId);

        if (validator.isValidEmail(emailId)) {
            return emailId;
        } else {
            logger.warn("Entered email id is invalid.");
        }

        return getEmailId();
    }

    /**
     * <p>
     * Gets a valid mobile number from the user and returns it.
     * </p>
     *
     * @return the mobile number of the user.
     */
    private String getMobileNumber() {
        logger.info("Choose your country code :\n1.America\n2.Australia\n3.China\n4.Germany\n5.India");
        final int availableCountryCode = 5;
        final int choice = getChoice();

        toGoBack(choice);

        if (availableCountryCode < choice) {
            logger.warn("Invalid choice");
            return getMobileNumber();
        }

        logger.info("Enter your mobile number with country code:");
        final String mobileNumber = scanner.nextLine().trim();

        toGoBack(mobileNumber);

        if (validator.isValidMobileNumber(mobileNumber, choice)) {
            return mobileNumber;
        } else {
            logger.warn("Entered mobile number is invalid.");
        }

        return getMobileNumber();
    }

    /**
     * <p>
     * Gets the choice from the user and returns it.
     * </p>
     * @return the choice of the user.
     */
    private int getChoice() {
        try {
            final String choice = scanner.nextLine().trim();

            if (validator.checkToGoBack(choice)) {
                return -1;
            }

            if (Integer.parseInt(choice) <= 0) {
                logger.warn("Invalid choice");

                return getChoice();
            }

            return Integer.parseInt(choice);
        } catch (final NumberFormatException exception) {
            logger.warn("Enter a valid choice");
        }

        return getChoice();
    }

    /**
     * <p>
     * Goes back to the previous page.
     * </p>
     *
     * @param input Refers the input given by the user
     */
    private void toGoBack(final String input) {
        if (validator.checkToGoBack(input)) {
            showAuthenticationPage();
        }
    }

    /**
     * <p>
     * Goes back to the previous page.
     * </p>
     *
     * @param input Refers the input given by the user
     */
    private void toGoBack(final int input) {
        if (-1 == input) {
            showAuthenticationPage();
        }
    }
}