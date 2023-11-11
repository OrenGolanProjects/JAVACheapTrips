package com.orengolan.cheaptrips.cheaptripsapp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import io.swagger.annotations.ApiModelProperty;



public class CheapTripsRequest implements Serializable {

    @ApiModelProperty(position = 1)
    @NotNull
    @Size(max=10)
    private String origin_cityName;
    @ApiModelProperty(position = 2)
    @NotNull
    @Size(max=10)
    private String destination_cityName;
    @ApiModelProperty(position = 3)
    @Max(10000)
    private Integer radius;
    @ApiModelProperty(position = 4)
    @Max(20)
    private Integer limitPlaces;
    @ApiModelProperty(position = 5)
    private List<String> kinds;

    public CheapTripsRequest(String origin_cityName, String destination_cityName,
                             Integer radius, Integer limitPlaces,List<String> kinds) {
        this.origin_cityName = origin_cityName;
        this.destination_cityName = destination_cityName;
        this.radius = radius;
        this.limitPlaces = limitPlaces;
        this.kinds = (kinds != null && !kinds.isEmpty()) ? kinds :Arrays.asList("interesting_places", "amusements", "sport","tourist_facilities","accomodations","adult");
    }

    public String getOrigin_cityName() {
        return origin_cityName;
    }

    public void setOrigin_cityName(String origin_cityName) {
        this.origin_cityName = origin_cityName;
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

