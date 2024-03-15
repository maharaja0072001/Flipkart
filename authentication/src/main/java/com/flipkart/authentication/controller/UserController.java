package com.flipkart.authentication.controller;

import com.flipkart.authentication.json.JsonObject;
import com.flipkart.authentication.model.User;
import com.flipkart.authentication.model.UserCredential;
import com.flipkart.authentication.service.UserService;
import com.flipkart.authentication.service.UserServiceImpl;
import com.flipkart.authentication.validation.groups.GetUserChecker;
import com.flipkart.authentication.validation.groups.UserCreationChecker;
import com.flipkart.authentication.validation.groups.UserLoginChecker;
import com.flipkart.authentication.validation.groups.UserUpdateChecker;
import com.flipkart.authentication.json.Json;
import com.flipkart.authentication.json.JsonFactory;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import java.util.Objects;
import java.util.Optional;

/**
 * <p>
 * Interacts between UserView and UserService for creating new user and getting existing user for login.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/user")
public class UserController {

    private final UserService userService;
    private final JsonFactory jsonFactory;
    private final Validator validator;
    private static final Logger LOGGER = LogManager.getLogger(UserController.class);

    /**
     * <p>
     * Default constructor of UserController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private UserController() {
        userService = UserServiceImpl.getInstance();
        jsonFactory = new JsonFactory();
        validator = Validation.byProvider(HibernateValidator.class).configure()
                .messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
    }

    /**
     * <p>
     * Creates a single instance of RestUserController class.
     * </p>
     */
    private static class InstanceHolder {

        private static final UserController REST_USER_CONTROLLER = new UserController();
    }

    /**
     * <p>
     * Gets a single object of UserController class and returns it.
     * </p>
     *
     * @return the single instance of UserController class.
     */
    public static UserController getInstance() {
        return InstanceHolder.REST_USER_CONTROLLER;
    }

    /**
     * <p>
     * Checks if the user already exists, if not then creates a new user.
     * </p>
     *
     * @param user Refers the {@link User}to be created.
     * @return the json node.
     */
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public byte[] createUser(final User user) {
        final JsonObject violationsInJson = jsonFactory.createJsonObject();

        validator.validate(user, UserCreationChecker.class).forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        if (violationsInJson.isEmpty()) {
            if (userService.createUser(user)) {
                LOGGER.info("User account created successfully");

                return violationsInJson.put("status", "user created successfully").asBytes();
            } else {
                LOGGER.warn("User account already exists");

                return violationsInJson.put("status", "user already registered").asBytes();
            }
        }

        return violationsInJson.asBytes();
    }

    /**
     * <p>
     * Gets the existing user by the given credentials.
     * </p>
     *
     * @param userCredential Refers the credentials of the user
     * @return the json node.
     */
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @GET
    public byte[] getUser(final UserCredential userCredential) {
        final JsonObject jsonObject = jsonFactory.createJsonObject();
        final String username = userCredential.getUsername();
        final String password = userCredential.getPassword();
        final User user = new User();

        if (Objects.isNull(username)) {
            return jsonObject.put("error :", "username can't be null").asBytes();
        }

        if (username.matches("\\d+")) {
            user.setMobileNumber(username);
        } else {
            user.setEmailId(username);
        }
        user.setPassword(password);
        final JsonObject violationsInJson = (JsonObject) validate(UserLoginChecker.class, user);

        if (violationsInJson.isEmpty()) {
            final Optional<User> optionalUser = userService.getUser(username, password);

            if (optionalUser.isPresent()) {
                return violationsInJson.put("status", "Login successful").asBytes();
            } else {
                return violationsInJson.put("error", "Wrong credentials").asBytes();
            }
        }

        return violationsInJson.asBytes();
    }

    /**
     * <p>
     * Updates the details of the user.
     * </p>
     *
     * @param user Refers the current {@link User}.
     * @return the json node.0
     */
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public byte[] updateUser(@PathParam("userId") final int userId, final User user) {
        final JsonObject violationsInJson = (JsonObject) validate(UserUpdateChecker.class, user);

        if (violationsInJson.isEmpty()) {
            user.setId(userId);
            userService.updateUser(user);
            LOGGER.info(String.format("User : id - %d - User details updated", user.getId()));

            return jsonFactory.createJsonObject().put("status", "update successful").asBytes();
        } else {
            return violationsInJson.asBytes();
        }
    }

    /**
     * <p>
     * Gets the user by id.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return the json node.
     */
    @Path("/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    public byte[] getUserById(@PathParam("userId") final int userId) {
        final User user = new User();

        user.setId(userId);
        final JsonObject violationsInJson = (JsonObject) validate(GetUserChecker.class, user);

        if (violationsInJson.isEmpty()) {
            final Optional<User> optionalUser = userService.getUserById(userId);

            if (optionalUser.isPresent()) {
                return violationsInJson.set("user", jsonFactory.toJson(optionalUser.get())).asBytes();
            } else {
                return violationsInJson.put("status", "User not found").asBytes();
            }
        }

        return violationsInJson.asBytes();
    }

    /**
     * <p>
     * Validates the object by the given group and returns object node containing the violations.
     * </p>
     *
     * @param clazz Refers the group class.
     * @param user  Refers the {@link User}.
     * @return the object node contains the violations.
     */
    private Json validate(final Class<?> clazz, final User user) {
        final JsonObject violationsInJson = jsonFactory.createJsonObject();

        validator.validate(user, clazz).forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        return violationsInJson;
    }
}
