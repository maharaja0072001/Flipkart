package org.abc.authentication.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

import org.abc.authentication.validation.annotations.ValidMobileNumber;
import org.abc.authentication.validation.groups.GetUserChecker;
import org.abc.authentication.validation.groups.UserCreationChecker;
import org.abc.authentication.validation.groups.UserLoginChecker;

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

    @NotNull(groups = UserCreationChecker.class)
    @Pattern(regexp = "^[A-Za-z][A-Za-z\\s]{2,30}$", message = "Name is invalid", groups = UserCreationChecker.class)
    @Column(length = 30)
    private String name;
    @NotNull(groups = UserCreationChecker.class)
    @Pattern(regexp = "^[a-zA-Z0-9]+(?:\\.[a-zA-Z0-9]+)*@[A-Za-z0-9]{2,}([.][a-zA-Z0-9]{2,})+$",
            message = "Email id is invalid", groups = UserLoginChecker.class)
    @Column(name = "email", length = 60)
    private String emailId;
    @NotNull(groups = UserCreationChecker.class)
    @ValidMobileNumber(groups = UserLoginChecker.class)
    @Column(name = "mobile_number", length = 15)
    private String mobileNumber;
    @NotNull(groups = UserCreationChecker.class)
    @NotNull(groups = UserLoginChecker.class)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
            message = "Password is invalid", groups = UserLoginChecker.class)
    @Column(length = 20)
    private String password;
    @Positive(groups = GetUserChecker.class, message = "User id can't be negative")
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private int id;
    @Transient
    private List<String> addresses;

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

    public void addAddress(String address) {
        addresses = (Objects.isNull(addresses)) ? new ArrayList<>() : addresses;

        addresses.add(address);
    }

    public List<String> getAddresses() {
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