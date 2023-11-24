package com.orengolan.cheaptrips.cheaptripsapp;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import io.swagger.annotations.ApiModelProperty;

public class CheapTripsRequestMonthly implements Serializable {

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
    @ApiModelProperty(position = 6,example = "['interesting_places', 'amusements', 'sport', 'tourist_facilities', 'accommodations', 'adult']")
    private List<String> kinds;

    public CheapTripsRequestMonthly(String origin_cityIATACode, String destination_cityIATACode, String destination_cityName, Integer radius, Integer limitPlaces, List<String> kinds) {
        this.origin_cityIATACode = origin_cityIATACode !=null ? origin_cityIATACode :"TLV";
        this.destination_cityIATACode = destination_cityIATACode;
        this.destination_cityName = destination_cityName;
        this.radius = radius != null ? radius :10000;
        this.limitPlaces = limitPlaces != null ? limitPlaces :2;
        this.kinds = (kinds != null && !kinds.isEmpty()) ? kinds : Arrays.asList("interesting_places", "amusements", "sport","tourist_facilities","accomodations","adult");
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

