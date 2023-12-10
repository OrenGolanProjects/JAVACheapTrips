package com.orengolan.cheaptrips.flight;

import com.mongodb.lang.Nullable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;


/**
 * The {@code Flight} class represents a flight request with details such as origin,
 * destination, date of departure, date of return, currency, and associated flight tickets.
 * It encapsulates information needed for flight search and booking functionalities.
 *
 * Key Features:
 * - {@code origin} and {@code destination}: Locations representing the departure and arrival points.
 * - {@code ticketKeys}: List of flight tickets associated with the flight request.
 * - {@code currency}: The currency in which flight prices are presented.
 * - {@code departure_at} and {@code return_at}: Optional date parameters for departure and return.
 *
 * Example:
 * An instance of this class can be created to specify a user's flight request,
 * including the origin, destination, desired dates, and currency. The associated
 * flight tickets can be retrieved and processed based on the user's preferences.
 *
 * Note: This class demonstrates the structure of a flight request object used in the application.
 * It encapsulates essential details for handling flight-related functionalities.
 */
public class Flight {
    private Location origin;
    private Location destination;
    private List<FlightTicket> ticketKeys;
    @NotNull
    @Size(min = 3,max = 3)
    private String currency;

    @Nullable
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in the yyyy-MM-dd format")
    private String departure_at;

    @Nullable
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date must be in the yyyy-MM-dd format")
    private String return_at;

    /**
     * Constructs a new {@code Flight} object with specified origin and destination locations.
     *
     * @param origin The location representing the departure point.
     * @param destination The location representing the arrival point.
     */
    public Flight(Location origin, Location destination){
        this.origin = origin;
        this.destination = destination;
        this.currency = "USD";
        this.ticketKeys = null;
        this.return_at = null;
        this.departure_at=null;
    }

    @Nullable
    public String getReturn_at() {
        return return_at;
    }

    public void setReturn_at(@Nullable String return_at) {
        this.return_at = return_at;
    }

    @Nullable
    public String getDeparture_at() {
        return departure_at;
    }

    public void setDeparture_at(@Nullable String departure_at) {
        this.departure_at = departure_at;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setOrigin(Location origin) {
        this.origin = origin;
    }

    public void setDestination(Location destination) {
        this.destination = destination;
    }

    public Location getOrigin() {
        return origin;
    }

    public Location getDestination() {
        return destination;
    }

    public String getCurrency() {
        return currency;
    }

    public List<FlightTicket> getTicketKeys() {
        return ticketKeys;
    }
    public void setTicketKeys(List<FlightTicket> ticketKeys) {
        this.ticketKeys = ticketKeys;
    }

    @Override
    public String toString() {
        return "Flight{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", ticketKeys=" + ticketKeys +
                ", currency='" + currency + '\'' +
                ", departure_at='" + departure_at + '\'' +
                ", return_at='" + return_at + '\'' +
                '}';
    }
}
