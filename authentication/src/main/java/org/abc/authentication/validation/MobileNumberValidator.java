package org.abc.authentication.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import org.abc.authentication.validation.annotations.ValidMobileNumber;

import java.util.Objects;
import java.util.regex.Pattern;

public class MobileNumberValidator implements ConstraintValidator<ValidMobileNumber, String> {

    @Override
    public void initialize(final ValidMobileNumber constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final String value, final ConstraintValidatorContext context) {
        return isValidMobileNumber(value);
    }

    /**
     * <p>
     * Validates the mobile number by the given regular expression pattern.
     * </p>
     *
     * @param mobileNumber Refers the mobile number to be validated.
     * @return true if the entered mobile number is valid, otherwise false.
     */
    private boolean isValidMobileNumber(final String mobileNumber) {
        if (Objects.isNull(mobileNumber)) {
            return true;
        }
        final String forAmerica = "^1[2-9]\\d{2}[2-9]\\d{2}\\d{4}$";
        final String forAustralia = "^61(4|04)\\d{8}$";
        final String forGermany = "^491[5-79]\\d{9}$";
        final String forChina = "^861[3-9][0-9]{9}$";
        final String forIndia = "^91[6789]\\d{9}$";
        final String forAllCountries = String.join("|", forAmerica, forAustralia, forGermany, forChina, forIndia);

        return Pattern.matches(forAllCountries, mobileNumber);
    }
}
