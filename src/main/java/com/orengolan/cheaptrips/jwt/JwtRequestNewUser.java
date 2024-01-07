package com.orengolan.cheaptrips.jwt;

import com.orengolan.cheaptrips.userinformation.UserInfoRequest;

public class JwtRequestNewUser {

    private JwtRequest jwtRequest;
    private UserInfoRequest userInfoRequest;

    public JwtRequestNewUser() {
    }

    public JwtRequest getJwtRequest() {
        return jwtRequest;
    }

    public void setJwtRequest(JwtRequest jwtRequest) {
        this.jwtRequest = jwtRequest;
    }

    public UserInfoRequest getUserInfoRequest() {
        return userInfoRequest;
    }

    public void setUserInfoRequest(UserInfoRequest userInfoRequest) {
        this.userInfoRequest = userInfoRequest;
    }
}
