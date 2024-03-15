package com.flipkart.authentication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import jakarta.validation.constraints.Size;

import com.flipkart.authentication.validation.annotations.ValidMobileNumber;
import com.flipkart.authentication.validation.groups.GetUserChecker;
import com.flipkart.authentication.validation.groups.UserCreationChecker;
import com.flipkart.authentication.validation.groups.UserLoginChecker;
import com.flipkart.authentication.validation.groups.UserUpdateChecker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static jakarta.persistence.GenerationType.IDENTITY;

/**
 * <p>
 * Represents an individual user who can create account, login, and place the orders. It encapsulates all the user related information.
 * </p>
 *
 * @author Maharaja S
 * @version 1.0
 */
@Entity(name = "users")
public class User {

    @Positive(groups = GetUserChecker.class, message = "User id can't be negative or zero")
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;
    @NotNull(groups = {UserCreationChecker.class, UserUpdateChecker.class})
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s]{2,30}$", message = "Name is invalid", groups = {UserCreationChecker.class, UserUpdateChecker.class})
    @Column(length = 30)
    private String name;
    @NotNull(groups = {UserCreationChecker.class, UserUpdateChecker.class})
    @Pattern(regexp = "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*@[A-Za-z0-9]{2,}([.][a-zA-Z0-9]{2,})+$",
            message = "Email id is invalid", groups = {UserLoginChecker.class , UserCreationChecker.class})
    @Column(name = "email", length = 60)
    @Size(max = 60, message = "Email length is exceeded", groups = UserCreationChecker.class)
    private String emailId;
    @NotNull(groups = {UserCreationChecker.class, UserUpdateChecker.class})
    @ValidMobileNumber(message = "Mobile number can't be null", groups = {UserLoginChecker.class, UserCreationChecker.class, UserUpdateChecker.class})
    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;
    @NotNull(groups = {UserLoginChecker.class, UserCreationChecker.class, UserUpdateChecker.class})
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "Password is invalid", groups = {UserLoginChecker.class, UserCreationChecker.class})
    @Column(length = 20)
    private String password;
    @Transient
    private List<Address> addresses;

    public void setId(final int id) {
        this.id = id;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public void setMobileNumber(final String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public void setEmailId(final String emailId) {
        this.emailId = emailId;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmailId() {
        return emailId;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getPassword() {
        return password;
    }

    public void addAddress(final Address address) {
        addresses = (Objects.isNull(addresses)) ? new ArrayList<>() : addresses;

        addresses.add(address);
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    @Override
    public boolean equals(final Object object) {
        return !Objects.isNull(object) && getClass() == object.getClass() && this.hashCode() == object.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(emailId, mobileNumber);
    }
}