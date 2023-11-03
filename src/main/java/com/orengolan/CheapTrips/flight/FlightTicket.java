package com.orengolan.CheapTrips.flight;

import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import java.io.Serializable;
import java.util.logging.Logger;

public class FlightTicket implements Serializable {

    private static final Logger logger = Logger.getLogger(FlightTicket.class.getName());
    private final Double price;
    private final Integer flightNumber;
    private final String airlineIataCode;
    private final LocalDateTime departureAt;
    private final LocalDateTime returnAt;
    private final LocalDateTime expiresAt;
    private final Integer ticketIndex;
    private final String currency;

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

    public String generateTicketKey(String origin_cityIataCode,String destination_cityIataCode) {
        // method: create a key for redis.
        return String.format("%s_%s_%s_%s",
                origin_cityIataCode,
                destination_cityIataCode,
                this.getAirlineIataCode(),
                this.ticketIndex
        );
    }
    public long generateExpireTime() {
        // method: takes the variable expiresAt and calculate the seconds that the deal is break
        // when a key will be created at redis then the key will get also the expiration time.

        int sec = Seconds.secondsBetween(LocalDateTime.now(), this.expiresAt).getSeconds();
        if (sec<0){
            logger.warning("FlightTicket>>generateExpireTime: Ticket is expired.");
            return 60;
        }
        return sec;
    }
    public String getAirlineIataCode() {
        return airlineIataCode;
    }
    @Override
    public String toString() {
        return "FlightTicket{" +
                "price=" + price +
                ", flightNumber=" + flightNumber +
                ", airlineIataCode='" + airlineIataCode + '\'' +
                ", departureAt=" + departureAt +
                ", returnAt=" + returnAt +
                ", expiresAt=" + expiresAt +
                ", ticketIndex=" + ticketIndex +
                ", currency='" + currency + '\'' +
                '}';
    }
}