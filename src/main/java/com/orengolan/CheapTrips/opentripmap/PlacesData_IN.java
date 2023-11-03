package com.orengolan.CheapTrips.opentripmap;


import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

public class PlacesData_IN implements Serializable {

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


    public PlacesData_IN(Integer radius, Double lon, Double lat, Integer limitPlaces, List<String> kinds, String cityName, String countryIATACode) {
        this.radius = radius;
        this.lon = lon;
        this.lat = lat;
        this.limitPlaces = limitPlaces;
        this.kinds = kinds;
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