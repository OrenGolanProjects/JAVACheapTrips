package com.orengolan.CheapTrips.model;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "cities")
public class City {
    @Id
    private  String _id;

    public String getId() {
        return _id;
    }

    public void setId(String _id) {
        this._id = _id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryIATA() {
        return countryIATA;
    }

    public void setCountryIATA(String countryIATA) {
        this.countryIATA = countryIATA;
    }

    public String getCityIATA() {
        return cityIATA;
    }

    public void setCityIATA(String cityIATA) {
        this.cityIATA = cityIATA;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public Double getLatCoordinates() {
        return latCoordinates;
    }

    public void setLatCoordinates(Double latCoordinates) {
        this.latCoordinates = latCoordinates;
    }

    public Double getLonCoordinates() {
        return lonCoordinates;
    }

    public void setLonCoordinates(Double lonCoordinates) {
        this.lonCoordinates = lonCoordinates;
    }

    @Indexed(unique = true)
    private String cityName;
    private String countryIATA;
    private String cityIATA;
    private String timeZone;
    private Double latCoordinates;
    private Double lonCoordinates;



    public City(String cityName, String countryIATA, String cityIATA, String timeZone, Double latCoordinates,Double lonCoordinates) {
        this.cityName = cityName;
        this.countryIATA = countryIATA;
        this.cityIATA = cityIATA;
        this.timeZone = timeZone;
        this.latCoordinates = latCoordinates;
        this.lonCoordinates = lonCoordinates;
    }


    @Override
    public String toString() {
        return "City{" +
                "id='" + _id + '\'' +
                ", city_name='" + cityName + '\'' +
                ", country_IATA='" + countryIATA + '\'' +
                ", city_IATA='" + cityIATA + '\'' +
                ", time_zone='" + timeZone + '\'' +
                ", lat_coordinates=" + latCoordinates +
                ", lon_coordinates=" + lonCoordinates +
                '}';
    }
}
