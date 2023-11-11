package com.orengolan.cheaptrips.usersearch;

import com.orengolan.cheaptrips.cheaptripsapp.CheapTripsResponse;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.*;
import java.util.Date;

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
    @Pattern(regexp = "[0-9]{10}")
    private String phone;

    @NotNull
    @Size(max=10)
    @Indexed(unique = true)
    private String userName;

    private CheapTripsResponse tripHistory;

    @Indexed(expireAfterSeconds = 7 * 60 * 60) // One year expiration
    private final Date expireAt;

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