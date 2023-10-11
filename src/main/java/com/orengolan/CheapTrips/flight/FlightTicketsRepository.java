package com.orengolan.CheapTrips.flight;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Repository
public class FlightTicketsRepository {
    private final RedisTemplate redis;

    public FlightTicketsRepository(@Qualifier("redisTemplate") RedisTemplate redis){
        this.redis = redis;
    }

    public void saveFlightTickets(FlightTicket flightTicket, Flight flight ) {

        String generateTicketKey = flightTicket.generateTicketKey(flight.getOrigin().getCityIataCode(),flight.getDestination().getCityIataCode());

        // Checks if the flight is already saved in Redis.
        FlightTicket existFlightTicket = getSpecificFlightTicket(generateTicketKey);

        // If flight did not save then create a key and save it in Redis.
        if (existFlightTicket == null )
        // Sets the: Key, Value, Expire time in seconds.
        // Key value gets from the input flight entity.
        {
            this.redis.opsForValue().set(generateTicketKey,flightTicket, flightTicket.generateExpireTimeToSeconds());
        }
    }


    public FlightTicket getSpecificFlightTicket(String key) {
        // Searches for flight in Redis DB.
        return (FlightTicket) this.redis.opsForValue().get(key);
    }

    public List<FlightTicket> getTicketsByParseKey(String partKey) {
        Set<Object> keys = redis.keys(partKey + "*");
        List<FlightTicket> tickets = new ArrayList<>();

        if (keys != null && !keys.isEmpty()) {
            for (Object key : keys) {
                Object value = redis.opsForValue().get((String) key);

                FlightTicket flightTicket;
                if (value instanceof FlightTicket) {
                    flightTicket = (FlightTicket) value;
                    // Check if the ticket is expired
//                    if (flightTicket.getExpiresAt().isAfter(LocalDateTime.now())){
//                        this.redis.delete(key);
//                        continue;
//                    }
                } else {
                    continue;
                }
                tickets.add(flightTicket);
            }
        }
        return tickets;
    }
}
