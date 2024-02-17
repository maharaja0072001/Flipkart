package org.abc.authentication.validation.annotations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import org.abc.authentication.validation.MobileNumberValidator;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.RetentionPolicy;

@Target({FIELD, METHOD, PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {MobileNumberValidator.class})
@Repeatable(ValidMobileNumber.List.class)
public @interface ValidMobileNumber {

    String message() default "The Mobile number is invalid";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * Defines several {@link ValidMobileNumber} annotations on the same element.
     *
     */
    @Target({ METHOD, FIELD, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        ValidMobileNumber[] value();
    }
}
