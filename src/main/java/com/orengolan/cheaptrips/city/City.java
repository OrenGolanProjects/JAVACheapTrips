package com.orengolan.cheaptrips.city;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * The {@code City} class represents a city entity in the CheapTrips backend application. Instances of this class
 * are annotated with {@code @Document} from Spring Data MongoDB, indicating that they will be stored in a MongoDB
 * collection named "cities."
 *
 * The class includes attributes that encapsulate essential information about a city:
 * - {@code cityName}: The name of the city.
 * - {@code cityIATACode}: The IATA code of the city, a three-letter code, annotated with {@code @Size(max = 3)} and
 *   {@code @Indexed(unique = true)} to ensure uniqueness in the MongoDB collection.
 * - {@code timeZone}: The time zone of the city.
 * - {@code latCoordinates}: The latitude coordinates of the city location, constrained between -90 and 90.
 * - {@code lonCoordinates}: The longitude coordinates of the city location, constrained between -180 and 180.
 * - {@code expireAt}: A date representing the expiration of the city data in the database, set to one year from
 *   the creation date.
 * - {@code countryIATACode}: The IATA code of the country where the city is located, a three-letter code.
 *
 * The class provides a constructor to initialize its attributes, and getter and setter methods for each attribute
 * to facilitate access and modification. Additionally, it overrides the {@code toString()} method for easy
 * representation of the object.
 *
 * Usage Example:
 * <pre>
 * {@code
 * City city = new City("Example City", "XYZ", "UTC+2", 12.34, 56.78, "CTY");
 * }
 * </pre>
 *
 * This {@code City} class is designed to hold essential information about cities, making it suitable for integration
 * with MongoDB for data storage in the CheapTrips application.
 */
@Document(collection = "cities")
public class City {
    @Id
    private String _id;

    @NotNull
    @Size(min = 2, max = 50)
    private String cityName;

    @NotNull
    @Size(max = 3)
    @Indexed(unique = true)
    private final String cityIATACode;
    private final String timeZone;
    @NotNull
    @DecimalMin(value = "-90.0", message = "Latitude must be at least -90")
    @DecimalMax(value = "90.0", message = "Latitude must be at most 90")
    private final Double latCoordinates;

    @NotNull
    @DecimalMin(value = "-180.0", message = "Longitude must be at least -180")
    @DecimalMax(value = "180.0", message = "Longitude must be at most 180")
    private final Double lonCoordinates;

    @Indexed(expireAfterSeconds = 365 * 24 * 60 * 60) // One year expiration
    private Date expireAt;

    @NotNull
    @Size(max = 3)
    private String countryIATACode;

    public City(@NotNull String cityName, @NotNull String cityIATACode,
                @NotNull String timeZone, @NotNull Double latCoordinates, @NotNull Double lonCoordinates,@NotNull String countryIATACode) {
        this.cityName = cityName.toLowerCase();
        this.cityIATACode = cityIATACode.toUpperCase();
        this.timeZone = timeZone;
        this.latCoordinates = latCoordinates;
        this.lonCoordinates = lonCoordinates;
        this.countryIATACode = countryIATACode;
        this.expireAt = new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)); // 1 year in milliseconds
    }


    @NotNull
    public String getCountryIATACode() {
        return countryIATACode;
    }

    public void setCountryIATACode(@NotNull String countryIATACode) {
        this.countryIATACode = countryIATACode;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public String getTimeZone() {
        return timeZone;
    }

    @NotNull
    public String getCityName() {
        return this.cityName;
    }
    public void setCityName(String cityName) {
        if (cityName.isEmpty()){
            throw new IllegalArgumentException("City name is null, cannot setCityName.");
        }
        this.cityName = cityName;
    }

    @NotNull
    public String getCityIATACode() {
        return cityIATACode;
    }

    @NotNull
    public Double getLatCoordinates() {
        return latCoordinates;
    }

    @NotNull
    public Double getLonCoordinates() {
        return lonCoordinates;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityName='" + cityName +
                ", cityIATACode='" + cityIATACode +
                ", timeZone='" + timeZone +
                ", latCoordinates=" + latCoordinates +
                ", lonCoordinates=" + lonCoordinates +
                ", expireAt=" + expireAt +
                ", country=" + countryIATACode +
                '}';
    }
}
