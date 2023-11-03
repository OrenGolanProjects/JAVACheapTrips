package com.orengolan.CheapTrips.user;

import com.orengolan.CheapTrips.flight.Flight;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;

@Document(collection = "users")
public class User {
    @Id
    private  String _id;

    private  Boolean _isAdmin;

    @NotNull
    @Size(min = 2, max = 10)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 10)
    private String surName;

    @NotNull
    @Email
    @Indexed(unique = true)
    private String email;

    @NotNull
    @Pattern(regexp = "[0-9]{10}")
    private String phone;

    public User(String firstName, String surName, String email, String phone) {
        this.firstName = firstName;
        this.surName = surName;
        this.email = email;
        this.phone = phone;
        this._isAdmin =false;
        @Null Flight flight = null;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "User{" +
                "_id='" + _id + '\'' +
                ", firstName='" + firstName + '\'' +
                ", surName='" + surName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
