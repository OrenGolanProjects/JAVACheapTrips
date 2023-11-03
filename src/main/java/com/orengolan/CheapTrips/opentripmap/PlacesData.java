package com.orengolan.CheapTrips.opentripmap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "opentripmap")
public class PlacesData {
    @Id
    private  String _id;
    private final Integer radius;
    private final Double lon;
    private final Double lat;
    private  final Integer limitPlaces;
    private final String city;
    private final String countryIATAcode;
    @Indexed(unique = true)
    private String databaseKey;
    private List<PlaceDetails> places;
    private List<String> kinds;

    public PlacesData(Integer radius, Double lon, Double lat, Integer limitPlaces, String city, String countryIATAcode,  List<String> kinds) {
        this.radius = radius;
        this.lon = lon;
        this.lat = lat;
        this.limitPlaces = limitPlaces;
        this.city = city;
        this.countryIATAcode = countryIATAcode;
        this.databaseKey = countryIATAcode+"_"+city;
        this.kinds = kinds;
        this.places = new ArrayList<>();
    }


    public List<PlaceDetails> getPlaces() {
        return this.places;
    }

    public String getId() {
        return this._id;
    }

    public String getDatabaseKey() {
        return this.databaseKey;
    }

    public Integer getRadius() {
        return this.radius;
    }

    public Double getLon() {
        return this.lon;
    }

    public Double getLat() {
        return this.lat;
    }

    public List<String> getKinds() {
        return this.kinds;
    }
    public void setKinds(List<String> kinds) {
        this.kinds = kinds;
    }

    public Integer getLimitPlaces() {
        return this.limitPlaces;
    }

    public String getCity() {
        return this.city;
    }

    public String getCountryIATAcode() {
        return this.countryIATAcode;
    }

    public void setDatabaseKey(String databaseKey) {
        this.databaseKey = databaseKey;
    }

    public void setPlaces(List<PlaceDetails> places) {
        this.places = places;
    }

    @Override
    public String toString() {
        return "OpenTripMap{" +
                "_id='" + this.getId() +
                ", radius=" + this.getRadius() +
                ", lon=" + this.getLon() +
                ", lat=" + this.getLat() +
                ", limitPlaces=" + this.getLimitPlaces() +
                ", city='" + this.getCity() +
                ", countryIATAcode='" + this.getCountryIATAcode() +
                ", databaseKey='" + this.getDatabaseKey() +
                ", kinds=" + this.getKinds() +
                '}';
    }
}
