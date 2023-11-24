package com.orengolan.cheaptrips.city;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

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
        this.cityName = cityName;
        this.cityIATACode = cityIATACode;
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
