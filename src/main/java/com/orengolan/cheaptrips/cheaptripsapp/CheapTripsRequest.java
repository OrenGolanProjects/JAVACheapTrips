package com.orengolan.cheaptrips.cheaptripsapp;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;

/**
 * The {@code CheapTripRequest} class represents a request object for querying cheap trips information.
 * It encapsulates the parameters required to search for travel destinations, including origin and destination
 * city IATA codes, city names, radius, departure and return dates, and other optional parameters.
 *
 * Key Features:
 * - Validation annotations ensure that the request parameters meet specific constraints.
 * - Provides default values and validation for optional date parameters (departure_at and return_at).
 * - Defines a list of kinds representing the types of places or activities to include in the search.
 *
 * Example Usage:
 * CheapTripRequest tripRequest = new CheapTripRequest("TLV", "AMS", "2023-11-01", "2023-11-10", "amsterdam", 100, 5);
 * tripRequest.setKinds(Arrays.asList("interesting_places", "amusements", "sport"));
 * // Use the request object to query information about cheap trips.
 */
public class CheapTripsRequest {

    @ApiModelProperty(position = 1, example = "TLV")
    @NotNull
    @Size(min = 3,max=3)
    private String origin_cityIATACode;
    @ApiModelProperty(position = 2,example = "AMS")
    @NotNull
    @Size(min = 3,max=3)
    private String destination_cityIATACode;

    @ApiModelProperty(position = 3,example ="amsterdam")
    @NotNull
    @Size(min = 3,max=50)
    private String destination_cityName;

    @ApiModelProperty(position = 4,example ="10000")
    @Max(50000)
    private Integer radius;
    @ApiModelProperty(position = 5,example ="2")
    @Max(20)
    private Integer limitPlaces;

    @ApiModelProperty(position = 6,hidden = true)
    private List<String> kinds;

    public CheapTripsRequest(String origin_cityIATACode, String destination_cityIATACode, String destination_cityName, Integer radius, Integer limitPlaces) {
        this.origin_cityIATACode = origin_cityIATACode !=null ? origin_cityIATACode :"TLV";
        this.destination_cityIATACode = destination_cityIATACode;
        this.destination_cityName = destination_cityName;
        this.radius = radius != null ? radius :10000;
        this.limitPlaces = limitPlaces != null ? limitPlaces :2;
        this.kinds = Arrays.asList("interesting_places", "amusements", "sport","tourist_facilities","accomodations","adult");
    }

    public String getOrigin_cityIATACode() {
        return origin_cityIATACode;
    }

    public void setOrigin_cityIATACode(String origin_cityIATACode) {
        this.origin_cityIATACode = origin_cityIATACode;
    }

    public String getDestination_cityIATACode() {
        return destination_cityIATACode;
    }

    public void setDestination_cityIATACode(String destination_cityIATACode) {
        this.destination_cityIATACode = destination_cityIATACode;
    }

    public String getDestination_cityName() {
        return destination_cityName;
    }

    public void setDestination_cityName(String destination_cityName) {
        this.destination_cityName = destination_cityName;
    }

    public Integer getRadius() {
        return radius;
    }

    public void setRadius(Integer radius) {
        this.radius = radius;
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
}

