package com.orengolan.CheapTrips.city;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Size;

@Document(collection = "cities")
public class City {
    @Id
    private String _id;

    @NotNull
    @Size(min = 2, max = 10)
    private String cityName;
    @NotNull
    @Size(max = 3)
    private final String countryIATACode;
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


    public City(@NotNull String cityName, @NotNull String countryIATACode, @NotNull String cityIATACode,
                @NotNull String timeZone, @NotNull Double latCoordinates, @NotNull Double lonCoordinates) {
        this.cityName = cityName;
        this.countryIATACode = countryIATACode;
        this.cityIATACode = cityIATACode;
        this.timeZone = timeZone;
        this.latCoordinates = latCoordinates;
        this.lonCoordinates = lonCoordinates;
    }


    @Override
    public String toString() {
        return "{" +
                "id='" + this.getId() + '\'' +
                ", cityName='" + this.getCityName() + '\'' +
                ", countryIATACode='" + this.getCountryIATACode() + '\'' +
                ", cityIATACode='" + this.getCityIATACode() + '\'' +
                ", timeZone='" + this.getTimeZone() + '\'' +
                ", latCoordinates=" + this.getLatCoordinates() +
                ", lonCoordinates=" + this.getLonCoordinates() +
                '}';
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getId() {
        return this._id;
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
    public String getCountryIATACode() {
        return countryIATACode;
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

}
