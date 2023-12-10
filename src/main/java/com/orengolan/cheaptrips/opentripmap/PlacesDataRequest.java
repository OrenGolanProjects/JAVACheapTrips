package com.orengolan.cheaptrips.opentripmap;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code PlacesDataRequest} class represents a request for information about places, including geographical coordinates,
 * radius, limits on the number of places, and filters based on kinds of places. It is designed to capture user input and
 * provide default values for unspecified parameters.
 *
 * Key Features:
 * - Represents a request for information about places with parameters such as radius, longitude, latitude, limits on the number of places, and kinds.
 * - Includes validation annotations like {@code @Max}, {@code @Min}, {@code @DecimalMax}, {@code @DecimalMin}, and {@code @Size} to ensure input constraints.
 * - Provides default values for unspecified parameters, such as a default list of kinds if none is provided.
 * - Implements setters and getters for each parameter, allowing flexibility in constructing and modifying requests.
 *
 * Example Usage:
 * The class is used to construct requests for information about places, allowing users to specify parameters like location, radius,
 * and preferred kinds of places. It ensures the validity of user input through validation annotations and provides default values
 * when certain parameters are not explicitly set.
 *
 * Note: This class serves as a crucial part of the application's interaction with the OpenTripMap service, facilitating user input
 * and customization of queries for retrieving information about places.
 */
public class PlacesDataRequest implements Serializable {

    @Max(50000)
    private  Integer radius;

    @DecimalMax(value = "180.0")
    @DecimalMin(value = "-180.0")
    private  Double lon;

    @DecimalMax(value = "90.0")
    @DecimalMin(value = "-90.0")
    private  Double lat;
    @Min(1)
    @Max(50)
    private  Integer limitPlaces;

    private  List<String> kinds;
    @Size(max = 10)
    private  String cityName;
    @Size(min = 2, max = 2, message = "Country IATA code must have exactly 2 characters")
    private  String countryIATACode;


    public PlacesDataRequest(Integer radius, Double lon, Double lat, Integer limitPlaces, List<String> kinds, String cityName, String countryIATACode) {
        this.radius = radius;
        this.lon = lon;
        this.lat = lat;
        this.limitPlaces = limitPlaces;
        this.kinds = (kinds != null && !kinds.isEmpty()) ? kinds : Arrays.asList("interesting_places", "amusements", "sport","tourist_facilities","accomodations","adult");
        this.cityName = cityName;
        this.countryIATACode = countryIATACode;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Integer getLimitPlaces() {
        return limitPlaces;
    }

    public void setLimitPlaces(Integer limitPlaces) {
        this.limitPlaces = limitPlaces;
    }

    public List<String> getKinds() {
        return kinds;
    }

    public void setKinds(List<String> kinds) {
        this.kinds = kinds;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryIATACode() {
        return countryIATACode;
    }

    public void setCountryIATACode(String countryIATACode) {
        this.countryIATACode = countryIATACode;
    }
}