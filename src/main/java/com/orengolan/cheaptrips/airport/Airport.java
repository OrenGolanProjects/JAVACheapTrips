package com.orengolan.cheaptrips.airport;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "airports")
public class Airport {

    @Id
    private String _id;
    private String cityIATACode;
    private String airportName;
    private String timeZone;
    private String countryIATACode;
    @Indexed(unique = true)
    private String airportIATACode;
    private Double lonCoordinates;
    private Double latCoordinates;

    public Airport(String airportIATACode,String airportName ,Double lonCoordinates ,Double latCoordinates,String timeZone ,String countryIATACode ,String cityIATACode){
        this.airportIATACode    = airportIATACode;
        this.airportName        = airportName;
        this.lonCoordinates     = lonCoordinates;
        this.latCoordinates     = latCoordinates;
        this.timeZone           = timeZone;
        this.countryIATACode    = countryIATACode;
        this.cityIATACode       = cityIATACode;
    }

    @Override
    public String toString() {
        return "{" +
                "id='" + _id + '\'' +
                ", airportName='" + airportName + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", countryIATACode='" + countryIATACode + '\'' +
                ", airportIATACode='" + airportIATACode + '\'' +
                ", lat_coordinates=" + latCoordinates +
                ", lon_coordinates=" + lonCoordinates +
                '}';
    }

    public String get_id() {
        return _id;
    }

    public String getCityIATACode() {
        return cityIATACode;
    }

    public void setCityIATACode(String cityIATACode) {
        this.cityIATACode = cityIATACode;
    }

    public String getAirportName() {
        return airportName;
    }

    public void setAirportName(String airportName) {
        this.airportName = airportName;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getCountryIATACode() {
        return countryIATACode;
    }

    public void setCountryIATACode(String countryIATACode) {
        this.countryIATACode = countryIATACode;
    }

    public String getAirportIATACode() {
        return airportIATACode;
    }

    public void setAirportIATACode(String airportIATACode) {
        this.airportIATACode = airportIATACode;
    }

    public Double getLonCoordinates() {
        return lonCoordinates;
    }

    public void setLonCoordinates(Double lonCoordinates) {
        this.lonCoordinates = lonCoordinates;
    }

    public Double getLatCoordinates() {
        return latCoordinates;
    }

    public void setLatCoordinates(Double latCoordinates) {
        this.latCoordinates = latCoordinates;
    }
}

