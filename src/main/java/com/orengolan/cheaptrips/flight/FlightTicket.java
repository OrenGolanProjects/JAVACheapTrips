package com.orengolan.cheaptrips.flight;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateTimeSerializer;
import com.orengolan.cheaptrips.airline.Airline;
import org.joda.time.LocalDateTime;
import org.joda.time.Seconds;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * The {@code FlightTicket} class represents a flight ticket with essential information such as
 * price, flight number, airline details, departure and return times, and expiration time. It is
 * designed to encapsulate details about a specific flight ticket and provides methods for
 * generating a Redis key, calculating expiration time, and accessing various attributes.
 *
 * Key Features:
 * - {@code generateTicketKey}: Generates a unique Redis key for the flight ticket based on the
 *   origin and destination city IATA codes, airline IATA code, and ticket index.
 * - {@code generateExpireTime}: Calculates the remaining time until the flight ticket expires in
 *   seconds, ensuring proper handling of ticket expiration.
 *
 * Example:
 * The class can be used to create instances of flight tickets, calculate their expiration times,
 * and generate unique Redis keys for efficient storage and retrieval. It encapsulates key
 * information about a flight ticket, making it suitable for use in flight management scenarios.
 *
 * Note: This class is an essential part of the flight management module and serves as a
 * representation of individual flight tickets with associated details.
 */
public class FlightTicket implements Serializable {

    private static final Logger logger = Logger.getLogger(FlightTicket.class.getName());
    @NotNull
    private  Double price;
    @NotNull
    private  Integer transfers;
    private Airline airlineDetails;
    @NotNull
    private  Integer flightNumber;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private  LocalDateTime departureAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private  LocalDateTime returnAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private  LocalDateTime expiresAt;

    private  String ticketIndex;


    public FlightTicket() {
    }

    public FlightTicket(@NotNull Double price,@NotNull  Airline airlineDetails,@NotNull  Integer flightNumber,
                        @NotNull LocalDateTime departureAt,@NotNull  LocalDateTime returnAt,
                        @NotNull LocalDateTime expiresAt,@NotNull  String ticketIndex,@NotNull Integer transfers) {
        this.price = price;
        this.flightNumber = flightNumber;
        this.airlineDetails = airlineDetails;
        this.departureAt = departureAt;
        this.returnAt = returnAt;
        this.expiresAt = expiresAt;
        this.ticketIndex = ticketIndex;
        this.transfers = transfers;

    }

    /**
     * Generates a unique Redis key for the flight ticket based on the origin and destination city
     * IATA codes, airline IATA code, and ticket index.
     *
     * @param origin_cityIataCode The IATA code of the origin city.
     * @param destination_cityIataCode The IATA code of the destination city.
     * @return A unique Redis key for the flight ticket.
     */
    public String generateTicketKey(String origin_cityIataCode,String destination_cityIataCode) {
        // method: create a key for redis.
        return String.format("%s_%s_%s_%s",
                origin_cityIataCode,
                destination_cityIataCode,
                this.airlineDetails.getAirlineIATACode(),
                this.ticketIndex
        );
    }

    /**
     * Calculates the remaining time until the flight ticket expires in seconds. If the ticket is
     * already expired, a default expiration time of 60 seconds is returned.
     *
     * @return The remaining time until expiration in seconds.
     */
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

    public Integer getTransfers() {
        return transfers;
    }

    public void setTransfers(Integer transfers) {
        this.transfers = transfers;
    }

    public Airline getAirlineDetails() {
        return airlineDetails;
    }

    public void setAirlineDetails(Airline airlineDetails) {
        this.airlineDetails = airlineDetails;
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

    public String getTicketIndex() {
        return ticketIndex;
    }

    public void setTicketIndex(String ticketIndex) {
        this.ticketIndex = ticketIndex;
    }

    public String toJson() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(this);
    }

    public static FlightTicket fromJson(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, FlightTicket.class);
    }


    @Override
    public String toString() {
        return "FlightTicket{" +
                "price=" + price +
                ", transfers=" + transfers +
                ", airlineDetails=" + airlineDetails +
                ", flightNumber=" + flightNumber +
                ", departureAt=" + departureAt +
                ", returnAt=" + returnAt +
                ", expiresAt=" + expiresAt +
                ", ticketIndex='" + ticketIndex + '\'' +
                '}';
    }
}
