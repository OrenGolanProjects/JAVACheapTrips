package com.orengolan.cheaptrips.cheaptripsapp;

import com.mongodb.lang.Nullable;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.List;

public class CheapTripsRequestByDates {

    @ApiModelProperty(position = 1, example = "TLV")
    @NotNull
    @Size(min = 3, max = 3)
    private String origin_cityIATACode;
    @ApiModelProperty(position = 2,example = "AMS")
    @NotNull
    @Size(min = 3, max = 3)
    private String destination_cityIATACode;

    @ApiModelProperty(position = 3,example ="amsterdam")
    @Nullable
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date departure_at must be in the yyyy-MM-dd format")
    private String departure_at;

    @ApiModelProperty(position = 4,example ="2023-11-01")
    @Nullable
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "Date return_at must be in the yyyy-MM-dd format")
    private String return_at;

    @ApiModelProperty(position = 5,example ="2023-11-01")
    @NotNull
    @Size(min = 3, max = 50)
    private String destination_cityName;

    @ApiModelProperty(position = 6,example ="10000")
    @Max(50000)
    private Integer radius;

    @ApiModelProperty(position = 7,example ="2")
    @Max(20)
    private Integer limitPlaces;
    @ApiModelProperty(position = 8, example = "['interesting_places', 'amusements', 'sport', 'tourist_facilities', 'accommodations', 'adult']")
    private List<String> kinds;

    public CheapTripsRequestByDates(String origin_cityIATACode, String destination_cityIATACode, @Nullable String departure_at, @Nullable String return_at, String destination_cityName, Integer radius, Integer limitPlaces, List<String> kinds) {
        this.origin_cityIATACode = origin_cityIATACode;
        this.destination_cityIATACode = destination_cityIATACode;
        this.departure_at = departure_at;
        this.return_at = return_at;
        this.destination_cityName = destination_cityName;
        this.radius = radius;
        this.limitPlaces = limitPlaces;
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

    @Nullable
    public String getDeparture_at() {
        return departure_at;
    }

    public void setDeparture_at(@Nullable String departure_at) {
        this.departure_at = departure_at;
    }

    @Nullable
    public String getReturn_at() {
        return return_at;
    }

    public void setReturn_at(@Nullable String return_at) {
        this.return_at = return_at;
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
