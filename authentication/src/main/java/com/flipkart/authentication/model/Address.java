package com.flipkart.authentication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import com.flipkart.authentication.validation.groups.OrderChecker;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * <p>
 * Represents the address of the user.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Entity
public class Address {

    @Positive(message = "Address id can't be zero or negative", groups = OrderChecker.class)
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;
    @Positive(message = "User id can't be zero or negative", groups = OrderChecker.class)
    private int userId;
    @NotNull(message = "Door number can't be null", groups = OrderChecker.class)
    @Column(name = "door_number")
    private String doorNumber;
    @NotNull(message = "Street can't be null", groups = OrderChecker.class)
    private String street;
    @NotNull(message = "City can't be null", groups = OrderChecker.class)
    private String city;
    @NotNull(message = "State can't be null", groups = OrderChecker.class)
    private String state;
    @NotNull(message = "Country can't be null", groups = OrderChecker.class)
    private String country;
    @Positive(message = "Pin-code can't be zero or negative", groups = OrderChecker.class)
    @Column(name = "pin_code")
    private int pinCode;

    public Address() {}

    public Address(final int userId, final String doorNumber, final String street, final String city, final String state,
                   final int pinCode, final String country) {
        this.userId = userId;
        this.doorNumber = doorNumber;
        this.street = street;
        this.city = city;
        this.state = state;
        this.pinCode = pinCode;
        this.country = country;
    }

    public void setUserId(final int userId) {
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public void setDoorNumber(final String doorNumber) {
        this.doorNumber = doorNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getDoorNumber() {
        return doorNumber;
    }

    public void setStreet(final String street) {
        this.street = street;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public void setState(final String state) {
        this.state = state;
    }

    public String getState() {
        return state;
    }

    public void setPinCode(final int pinCode) {
        this.pinCode = pinCode;
    }

    public int getPinCode() {
        return pinCode;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    @Override
    public String toString() {
        return """
                    Address:
                    streetAddress = %s
                    city = %s
                    state = %s
                    zipCode = %s
                    country = %s""".formatted(street, city, state, pinCode, country);
    }
}
