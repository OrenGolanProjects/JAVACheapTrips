package com.orengolan.cheaptrips.jwt;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.regex.Matcher;


public class JwtRequest implements Serializable {
    private static final long serialVersionUID = 5926468583005150707L;
    @ApiModelProperty(position = 1)
    private String email;

    @ApiModelProperty(position = 2)
    private String password;

    //need default constructor for JSON Parsing
    public JwtRequest() {
    }

    public JwtRequest(String email, String password) {
        this.setEmail(email);
        this.setPassword(password);
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public void validateEmailFormat(String email) {
        if (!isValidEmailFormat(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private boolean isValidEmailFormat(String email) {
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        return email.matches(emailRegex);
    }

}
