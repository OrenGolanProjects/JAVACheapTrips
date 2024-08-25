package com.orengolan.cheaptrips.userinformation;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import javax.validation.constraints.*;

/**
 * The {@code UserInfoRequest} class represents a request object for creating or updating user information in the CheapTrips application.
 * It is used as a data transfer object (DTO) to encapsulate user details received from clients.
 *
 * Key Features:
 * - Includes validation annotations such as {@link NotBlank}, {@link Size}, and {@link Pattern} for ensuring data integrity.
 * - Utilizes Swagger's {@link ApiModelProperty} to provide additional information for API documentation.
 * - Provides a convenient method {@link #toUserInto(String)} to convert the request object into a {@link UserInfo} entity.
 *
 * Example Usage:
 * This class is used in conjunction with API endpoints to receive user information as input.
 * Developers can create instances of this class, validate the input using annotations, and convert it into a {@link UserInfo} entity.
 * The class promotes a clean separation between the incoming request payload and the internal entity representation.
 *
 * Note: Validation annotations ensure that the received data adheres to specific criteria, enhancing data quality and consistency.
 */
public class UserInfoRequest {

    @ApiModelProperty(position = 1)
    @NotBlank(message = "User name cannot be blank")
    @Size(min = 3, max = 10, message = "User name must be between 3 and 10 characters")
    @Indexed(unique = true)
    private String userName;

    @ApiModelProperty(position = 2)
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 10, message = "First name must be between 2 and 10 characters")
    private String firstName;

    @ApiModelProperty(position = 3)
    @NotBlank(message = "Surname cannot be blank")
    @Size(min = 2, max = 10, message = "Surname must be between 2 and 10 characters")
    private String surName;

    @ApiModelProperty(position = 4)
    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "[0-9]{10}", message = "Invalid phone number format")
    private String phone;


    public UserInfoRequest(String firstName, String surName, String phone, String userName) {
        this.firstName = firstName;
        this.surName = surName;
        this.phone = phone;
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


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
    * Converts the {@code UserInfoRequest} object into a {@link UserInfo} entity.
    *
    * @param email The email associated with the user.
    * @return The corresponding {@link UserInfo} entity.
    */
    public UserInfo toUserInfo(String email) {

        return new UserInfo(
                this.getFirstName(),
                this.getSurName(),
                email,
                this.getPhone(),
                this.getUserName()
        );

    }


    @Override
    public String toString() {
        return "UserInfoRequest{" +
                "firstName='" + firstName +
                ", surName='" + surName +
                ", phone='" + phone +
                ", userName='" + userName +
                '}';
    }

}
