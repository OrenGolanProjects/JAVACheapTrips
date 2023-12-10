package com.orengolan.cheaptrips.userinformation;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.mongodb.core.index.Indexed;
import javax.validation.constraints.*;

public class UserInfoRequest {
    @ApiModelProperty(position = 1)
    @NotBlank(message = "User name cannot be blank")
    @Size(min=3,max=10)
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

    public UserInfo toUserInto(String email){

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
