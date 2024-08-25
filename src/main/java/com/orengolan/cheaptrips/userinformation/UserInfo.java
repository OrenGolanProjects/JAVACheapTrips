package com.orengolan.cheaptrips.userinformation;

import com.orengolan.cheaptrips.cheaptripsapp.CheapTripsResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.*;
import java.util.Date;

/**
 * The {@code UserInfo} class represents user information stored in a MongoDB collection for the CheapTrips application.
 * It includes details such as the user's first name, surname, email, phone number, username, and trip history.
 *
 * Key Features:
 * - Annotated with Spring Data MongoDB annotations for mapping to the "userinfo" collection.
 * - Includes validation constraints for ensuring data integrity and uniqueness.
 * - Utilizes a unique index for email and username fields to enforce uniqueness.
 * - Manages the user's trip history as a {@link CheapTripsResponse} object.
 * - Incorporates an expiration mechanism using MongoDB's "expireAfterSeconds" to automatically remove outdated user information.
 *
 * Example Usage:
 * The class is used to persist and retrieve user information within the CheapTrips application.
 * Developers can store and manage user profiles, including trip history, to offer personalized services and maintain user engagement.
 *
 * Note: MongoDB is employed to store user information, providing scalability and flexibility in handling user-related data.
 */
@Document(collection = "userinfo")
public class UserInfo {
    @Id
    private  String id;

    @NotNull
    @Size(min = 2, max = 10)
    private String firstName;

    @NotNull
    @Size(min = 2, max = 10)
    private String surName;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
    @Indexed(unique = true)
    private String email;

    @NotNull
    @Pattern(regexp = "^\\d{3}-\\d{7}$", message = "Phone number must be in the format XXX-XXXXXXX")
    private String phone;

    @NotNull
    @Size(min = 2, max = 10)
    @Indexed(unique = true)
    private String userName;

    private CheapTripsResponse tripHistory;

    @Indexed(expireAfterSeconds = 7 * 60 * 60) // One year expiration
    private Date expireAt;

    public UserInfo(String firstName, String surName, String email, String phone,String userName) {
        this.firstName = firstName;
        this.surName = surName;
        this.email = email;
        this.phone = phone;
        this.tripHistory = null;
        this.userName = userName;
        this.expireAt = new Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)); // 1 year in milliseconds
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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


    public CheapTripsResponse getTripHistory() {
        return tripHistory;
    }

    public void setTripHistory(CheapTripsResponse tripHistory) {
        this.tripHistory = tripHistory;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "firstName='" + firstName +
                ", surName='" + surName +
                ", email='" + email +
                ", phone='" + phone +
                ", last trip=" + tripHistory.toString() +
                '}';
    }
}
