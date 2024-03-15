package com.flipkart.authentication.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import com.flipkart.authentication.validation.MobileNumberValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

import static java.lang.annotation.ElementType.FIELD;

/**
 * <p>
 * Annotation to validate mobile number.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Target({FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {MobileNumberValidator.class})
public @interface ValidMobileNumber {

    /**
     * <p>
     * Specifies the error message to use when the validation fails.
     * </p>
     *
     * @return The error message.
     */
    String message() default "The Mobile number is invalid";

    /**
     * <p>
     * Specifies the validation groups to which this constraint belongs.
     * </p>
     *
     * @return The validation groups.
     */
    Class<?>[] groups() default { };

    /**
     * <p>
     * Specifies the payload object associated with the constraint.
     * </p>
     *
     * @return The payload.
     */
    Class<? extends Payload>[] payload() default { };
}

