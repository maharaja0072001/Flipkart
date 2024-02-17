package org.abc.authentication.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

import org.abc.authentication.model.User;
import org.abc.authentication.service.UserService;
import org.abc.authentication.service.impl2.UserServiceImpl;
import org.abc.authentication.validation.groups.GetUserChecker;
import org.abc.authentication.validation.groups.UserCreationChecker;
import org.abc.authentication.validation.groups.UserLoginChecker;
import org.abc.authentication.validation.groups.UserUpdateChecker;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;

import java.util.Objects;

/**
 * <p>
 * Interacts between UserView and UserService for creating new user and getting existing user for login.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UserControllerREST {

    private static UserControllerREST userController;
    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final Validator validator;

    /**
     * <p>
     * Default constructor of UserController class. Kept private to restrict from creating object outside this class.
     * </p>
     */
    private UserControllerREST() {
        userService = UserServiceImpl.getInstance();
        objectMapper = new ObjectMapper();
        validator = Validation.byProvider(HibernateValidator.class)
                .configure().messageInterpolator(new ParameterMessageInterpolator()).buildValidatorFactory().getValidator();
    }

    /**
     * <p>
     * Creates a single object of UserController class and returns it.
     * </p>
     *
     * @return the single instance of UserController class.
     */
    public static UserControllerREST getInstance() {
        return Objects.isNull(userController) ? userController = new UserControllerREST() : userController;
    }

    /**
     * <p>
     * Checks if the user already exists, if not then creates a new user.
     * </p>
     *
     * @return true if the user created or false if user already exists.
     * @param user Refers the {@link User}to be created.
     */
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public ObjectNode createUser(final User user) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

        validator.validate(user, UserCreationChecker.class).stream().forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        final ObjectNode objectNode = objectMapper.createObjectNode();

        if (violationsInJson.isEmpty()) {
            return userService.createUser(user)
                    ? objectNode.put("status", "user created successfully")
                    : objectNode.put("status", "user already registered");
        }

        return violationsInJson;
    }

    /**
     * <p>
     * Gets the existing user by the given credentials.
     * </p>
     *
     * @param emailIdOrMobileNumber Refers the mobile number or email id of the user
     * @param password Refers the password of the user.
     * @return {@link User} if the credentials are correct and the user exists or null otherwise.
     */
    @Path("/getUser/{username}/{password}")
    @GET
    public ObjectNode getUser(@PathParam("username") final String emailIdOrMobileNumber,
                              @PathParam("password") final String password) {
        final User user = new User();

        if (emailIdOrMobileNumber.matches("\\d+")) {
            user.setMobileNumber(emailIdOrMobileNumber);
        } else {
            user.setEmailId(emailIdOrMobileNumber);
        }
        user.setPassword(password);
        final ObjectNode violationsInJson = validate(UserLoginChecker.class, user);

        if (violationsInJson.isEmpty()) {
            final User retrievedUser = userService.getUser(emailIdOrMobileNumber, password);
            ObjectNode objectNode = objectMapper.createObjectNode();

            if (Objects.nonNull(retrievedUser)) {
                objectNode.set("user", objectMapper.valueToTree(retrievedUser));
            } else {
                objectNode.put("status", "Wrong credentials");
            }

            return objectNode;
        }

        return violationsInJson;
    }

    /**
     * <p>
     * Updates the details of the user.
     * </p>
     *
     * @param user Refers the current {@link User}.
     */
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @PUT
    public ObjectNode updateDetails(final User user) {
        final ObjectNode violationsInJson = validate(UserUpdateChecker.class, user);

        if (violationsInJson.isEmpty()) {
            userService.updateDetails(user);

            return objectMapper.createObjectNode().put("status","update successful");
        } else {
            return  violationsInJson;
        }
    }

    /**
     * <p>
     * Gets the user by id.
     * </p>
     *
     * @param userId Refers the id of the user.
     * @return {@link User}.
     */
    @Path("/getById/{userId}")
    @GET
    public ObjectNode getUserById(@PathParam("userId") final int userId) {
        final User user = new User();

        user.setId(userId);
        final ObjectNode violationsInJson = validate(GetUserChecker.class, user);

        if (violationsInJson.isEmpty()) {
            final User retrievedUser = userService.getUserById(userId);
            ObjectNode objectNode = objectMapper.createObjectNode();

            if (Objects.nonNull(retrievedUser)) {
                objectNode.set("user", objectMapper.valueToTree(retrievedUser));
            } else {
                objectNode.put("status", "User not found");
            }

            return objectNode;
        }

        return violationsInJson;
    }

    /**
     * <p>
     * Validates the object by the given group and returns object node containing the violations.
     * </p>
     * @param clazz Refers the group class.
     * @param user Refers the {@link User}.
     * @return the object node contains the violations.
     */
    private ObjectNode validate(final Class clazz, final User user) {
        final ObjectNode violationsInJson = objectMapper.createObjectNode();

        validator.validate(user, clazz).stream().forEach(violation -> violationsInJson
                .put(violation.getPropertyPath().toString(), violation.getMessage()));

        return violationsInJson;
    }
}
