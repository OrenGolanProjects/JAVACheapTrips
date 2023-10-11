package com.orengolan.CheapTrips.city;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

@Document(collection = "cities")
public class City {
    @Id
    private  String _id;
    private String cityName;
    private String countryIATACode;
    @Indexed(unique = true)
    private String cityIATACode;
    private String timeZone;
    private Double latCoordinates;
    private Double lonCoordinates;

    public City(String cityName, String countryIATACode, String cityIATACode, String timeZone, Double latCoordinates,Double lonCoordinates) {
        if (cityName == null || countryIATACode == null || cityIATACode == null || timeZone == null || latCoordinates == null || lonCoordinates == null) {
            throw new IllegalArgumentException("All constructor arguments must be non-null.");
        }
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
                "id='" + _id + '\'' +
                ", cityName='" + cityName + '\'' +
                ", countryIATACode='" + countryIATACode + '\'' +
                ", cityIATACode='" + cityIATACode + '\'' +
                ", timeZone='" + timeZone + '\'' +
                ", latCoordinates=" + latCoordinates +
                ", lonCoordinates=" + lonCoordinates +
                '}';
    }

    public String getId() {
        return _id;
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

    public void setCountryIATACode(String countryIATA) {
        this.countryIATACode = countryIATA;
    }

    public String getCityIATACode() {
        return cityIATACode;
    }

    public void setCityIATACode(String cityIATA) {
        this.cityIATACode = cityIATA;
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

}
