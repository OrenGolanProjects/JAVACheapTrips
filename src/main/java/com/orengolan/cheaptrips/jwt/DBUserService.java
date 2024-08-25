package com.orengolan.cheaptrips.jwt;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;


@Service
public class DBUserService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DBUserService.class);

    @Autowired
    private DBUserRepository repository;

    @Autowired
    private Validator validator;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    public Optional<DBUser> findUserEmail(String userEmail) {
        return repository.findByEmail(userEmail);
    }

    public void saveDBUser(DBUser user) throws BindException {

        BeanPropertyBindingResult result = new BeanPropertyBindingResult(user, "user");
        validator.validate(user, result);

        if (result.hasErrors()) {
            throw new BindException(result);
        }
        String encodedPass = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPass);

        if (repository.findByEmail(user.getEmail()).isEmpty()) {
            repository.save(user);
        }
    }
}
