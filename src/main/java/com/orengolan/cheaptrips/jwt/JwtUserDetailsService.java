package com.orengolan.cheaptrips.jwt;


import com.orengolan.cheaptrips.userinformation.UserInfoRequest;
import com.orengolan.cheaptrips.userinformation.UserInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(JwtUserDetailsService.class);

    @Autowired
    private final DBUserService userService;

    private final UserInfoService userInfoService;

    public JwtUserDetailsService(DBUserService userService, UserInfoService userInfoService) {
        this.userService = userService;
        this.userInfoService = userInfoService;
    }

    @Override
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {
        logger.debug("Attempting to load user by email: {}", userEmail);

        Optional<DBUser> dbUser = userService.findUserEmail(userEmail);
        if (dbUser.isPresent()) {
            logger.debug("User found: {}. Encoded password: {}", userEmail, dbUser.get().getPassword());
            return new User(dbUser.get().getEmail(), dbUser.get().getPassword(), new ArrayList<>());
        } else {
            logger.error("User not found: {}", userEmail);
            throw new UsernameNotFoundException("User not found : " + userEmail);
        }
    }

    public void createUserInfo(UserInfoRequest userInfoRequest, String userEmail) throws BindException {
        logger.debug("Creating user info for email: {}", userEmail);
        userInfoService.createNewUser(userInfoRequest.toUserInfo(userEmail));
        logger.debug("User info created successfully for email: {}", userEmail);
    }

}
