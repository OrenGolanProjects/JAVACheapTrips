package com.orengolan.cheaptrips.service;

import com.mongodb.lang.Nullable;

public class RateLimitExceededException extends RuntimeException {

    public RateLimitExceededException(String message) {
        super(message);
    }
}
