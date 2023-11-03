package com.orengolan.CheapTrips.flight;

import java.util.List;

public class Flight {
    private final Location origin;
    private final Location destination;
    private List<FlightTicket> ticketKeys;


    public Flight(Location origin, Location destination){
        // Constructor flight.
        this.origin = origin;
        this.destination = destination;
        this.ticketKeys = null;
    }


    public static class Location{
        // Location subclass.
        private final String cityName;
        private final String cityIataCode;
        private final String countryIataCode;
        private final Double lonCoordinates;
        private final Double latCoordinates;

        public Location(String cityName, String cityIataCode, String countryIataCode,Double lonCoordinates,Double latCoordinates) {
            // Constructor location.
            this.cityName = cityName;
            this.cityIataCode = cityIataCode;
            this.countryIataCode = countryIataCode;
            this.lonCoordinates = lonCoordinates;
            this.latCoordinates = latCoordinates;
        }

        public String getCityName() {return this.cityName;}
        public String getCityIataCode() {return cityIataCode;}
        public String getCountryIataCode() {return countryIataCode;}
        public Double getLonCoordinates() {return lonCoordinates;}
        public Double getLatCoordinates() {return latCoordinates;}

        @Override
        public String toString() {
            return "Location{" +
                    "cityName='" + cityName + '\'' +
                    ", cityIataCode='" + cityIataCode + '\'' +
                    ", countryIataCode='" + countryIataCode + '\'' +
                    ", lonCoordinates=" + lonCoordinates +
                    ", latCoordinates=" + latCoordinates +
                    '}';
        }
    }

    public Location getOrigin() {
        return origin ;
    }
    public Location getDestination() {
        return destination;
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
                "origin=" + getOrigin().toString() +
                ", destination=" + getDestination().toString() +
                ", ticketKeys=" + ticketKeys +
                '}';
    }
}
