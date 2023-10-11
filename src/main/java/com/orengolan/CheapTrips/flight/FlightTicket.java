package com.orengolan.CheapTrips.flight;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.logging.Logger;

public class FlightTicket implements Serializable {
    private Double price;
    private Integer flightNumber;
    private String airlineIataCode;
    private LocalDateTime departureAt;
    private LocalDateTime returnAt;
    private LocalDateTime expiresAt;
    private final Integer ticketIndex;
    private final String currency;
    private static final Logger logger = Logger.getLogger(FlightTicket.class.getName());

    public FlightTicket(Double price, String airlineIataCode, Integer flightNumber, LocalDateTime departureAt, LocalDateTime returnAt, LocalDateTime expiresAt, Integer ticketIndex, String currency) {
        this.price = price;
        this.flightNumber = flightNumber;
        this.airlineIataCode = airlineIataCode;
        this.departureAt = departureAt;
        this.returnAt = returnAt;
        this.expiresAt = expiresAt;
        this.ticketIndex = ticketIndex;
        this.currency = currency;
    }


    // method: create a key for redis.
    public String generateTicketKey(String origin_cityIataCode,String destination_cityIataCode) {
        return String.format("%s_%s_%s_%s",
                origin_cityIataCode,
                destination_cityIataCode,
                this.getAirlineIataCode(),
                this.ticketIndex
        );
    }

    // method: takes the variable expiresAt and calculate the seconds that the deal is break
    // when a key will be created at redis then the key will get also the expiration time.
    public long generateExpireTimeToSeconds() {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, this.getExpiresAt());
        logger.warning("FlightTicket>>generateExpireTimeToSeconds: Ducation time: "+duration.getSeconds());
        if (duration.getSeconds()<0){
            return 600;
        }

        return duration.getSeconds();
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(Integer flightNumber) {
        this.flightNumber = flightNumber;
    }

    public String getAirlineIataCode() {
        return airlineIataCode;
    }

    public void setAirlineIataCode(String airlineIataCode) {
        this.airlineIataCode = airlineIataCode;
    }

    public LocalDateTime getDepartureAt() {
        return departureAt;
    }

    public void setDepartureAt(LocalDateTime departureAt) {
        this.departureAt = departureAt;
    }

    public LocalDateTime getReturnAt() {
        return returnAt;
    }

    public void setReturnAt(LocalDateTime returnAt) {
        this.returnAt = returnAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    public Integer getTicketIndex() {return ticketIndex;}

    public String getCurrency() {return currency;}
}