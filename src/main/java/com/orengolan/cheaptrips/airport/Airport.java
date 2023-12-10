package com.orengolan.cheaptrips.airport;


import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Size;

/**
 * The {@code Airport} class represents an airport entity in the CheapTrips backend application. Instances of this
 * class are annotated with {@code @Document} from Spring Data MongoDB, indicating that they will be stored in a
 * MongoDB collection named "airports."
 *
 * The class includes attributes that encapsulate essential information about an airport:
 * - {@code airportName}: The name of the airport.
 * - {@code airportIATACode}: The IATA code of the airport, a three-letter code, annotated with {@code @Size(min = 3, max = 3)}
 *   and {@code @Indexed(unique = true)} to ensure uniqueness in the MongoDB collection.
 * - {@code cityIATACode}: The IATA code of the city where the airport is located, a three-letter code.
 * - {@code countryIATACode}: The IATA code of the country where the airport is situated, a two-letter code.
 * - {@code timeZone}: The time zone of the airport.
 * - {@code lonCoordinates}: The longitude coordinates of the airport location.
 * - {@code latCoordinates}: The latitude coordinates of the airport location.
 *
 * The class provides a constructor to initialize its attributes, and getter and setter methods for each attribute to
 * facilitate access and modification.
 *
 * Usage Example:
 * <pre>
 * {@code
 * Airport airport = new Airport("Example Airport", "XYZ", "CIT", "CT", "UTC+2", 12.34, 56.78);
 * }
 * </pre>
 *
 * This {@code Airport} class is designed to hold essential information about airports, making it suitable for
 * integration with MongoDB for data storage in the CheapTrips application.
 */
@Document(collection = "airports")
public class Airport {

    @Id
    private String _id;

    @javax.validation.constraints.NotNull
    private String airportName;

    @NotNull
    @Size(min = 3,max = 3)
    @Indexed(unique = true)
    private String airportIATACode;
    @NotNull
    @Size(min = 3,max = 3)
    private String cityIATACode;
    @NotNull
    @Size(min = 2,max = 2)
    private String countryIATACode;
    private String timeZone;
    private Double lonCoordinates;
    private Double latCoordinates;


    public Airport(String airportName, @NotNull String airportIATACode, @NotNull String cityIATACode, @NotNull String countryIATACode, String timeZone, Double lonCoordinates, Double latCoordinates) {
        this.airportName = airportName;
        this.airportIATACode = airportIATACode;
        this.cityIATACode = cityIATACode;
        this.countryIATACode = countryIATACode;
        this.timeZone = timeZone;
        this.lonCoordinates = lonCoordinates;
        this.latCoordinates = latCoordinates;
    }


    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    @NotNull
    public String getAirportIATACode() {
        return airportIATACode;
    }

    public void setAirportIATACode(@NotNull String airportIATACode) {
        this.airportIATACode = airportIATACode;
    }

    @NotNull
    public String getCityIATACode() {
        return cityIATACode;
    }

    public void setCityIATACode(@NotNull String cityIATACode) {
        this.cityIATACode = cityIATACode;
    }

    @NotNull
    public String getCountryIATACode() {
        return countryIATACode;
    }

    public void setCountryIATACode(@NotNull String countryIATACode) {
        this.countryIATACode = countryIATACode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Double getLonCoordinates() {
        return lonCoordinates;
    }

    public void setLonCoordinates(Double lonCoordinates) {
        this.lonCoordinates = lonCoordinates;
    }

    public Double getLatCoordinates() {
        return latCoordinates;
    }

    public void setLatCoordinates(Double latCoordinates) {
        this.latCoordinates = latCoordinates;
    }


}

