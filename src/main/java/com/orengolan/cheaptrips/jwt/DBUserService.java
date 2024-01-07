package com.orengolan.cheaptrips.jwt;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class DBUserService {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(DBUserService.class);

    @Autowired
    private DBUserRepository repository;

    public Optional<DBUser> findUserEmail(String userEmail) {
            return repository.findByEmail(userEmail);
    }

    public void save(DBUser user) {
        if (repository.findByEmail(user.getName()).isEmpty()) {
            repository.save(user);
        }
    }
}
