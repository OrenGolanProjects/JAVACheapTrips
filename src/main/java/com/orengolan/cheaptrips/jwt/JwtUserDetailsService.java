package com.orengolan.cheaptrips.jwt;


import com.orengolan.cheaptrips.userinformation.UserInfoRequest;
import com.orengolan.cheaptrips.userinformation.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindException;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private final DBUserService userService;

    private final UserInfoService userInfoService;

    public JwtUserDetailsService(DBUserService userService, UserInfoService userInfoService) {
        this.userService = userService;
        this.userInfoService = userInfoService;
    }

    @Override
    public UserDetails loadUserByUsername(String UserEmail) throws UsernameNotFoundException {

        Optional<DBUser> dbUser = userService.findUserEmail(UserEmail);
        if (dbUser.isPresent()) {
            return new User(dbUser.get().getEmail(), dbUser.get().getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found : " + UserEmail);
        }
    }

    public void createUserInfo(UserInfoRequest userInfoRequest,String UserEmail) throws BindException {
        userInfoService.createNewUser(userInfoRequest.toUserInfo(UserEmail));
    }
}
