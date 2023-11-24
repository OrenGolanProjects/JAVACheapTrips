package com.orengolan.cheaptrips.opentripmap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;


@Document(collection = "opentripmap")
public class PlacesData {
    @Id
    private  String _id;
    private final Integer radius;
    private final Double lon;
    private final Double lat;
    private final Integer limitPlaces;
    private final String city;
    private final String countryIATAcode;
    @Indexed(unique = true)
    private String databaseKey;
    private KindsCategory kindsCategory;
    @Indexed(expireAfterSeconds = 365 * 24 * 60 * 60) // One year expiration
    private Date expireAt;

    public PlacesData(Integer radius, Double lon, Double lat, Integer limitPlaces, String city, String countryIATAcode) {
        this.radius = radius;
        this.lon = lon;
        this.lat = lat;
        this.limitPlaces = limitPlaces;
        this.city = city;
        this.countryIATAcode = countryIATAcode;
        this.databaseKey = countryIATAcode+"_"+city;
        this.kindsCategory = new KindsCategory();
        this.expireAt = new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)); // 1 year in milliseconds

    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public Date getExpireAt() {
        return expireAt;
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

    public KindsCategory getKindsCategory() {
        return kindsCategory;
    }

    public void setKindsCategory(KindsCategory kindsCategory) {
        this.kindsCategory = kindsCategory;
    }

    @Override
    public String toString() {
        return "PlacesData{" +
                "radius=" + radius +
                ", lon=" + lon +
                ", lat=" + lat +
                ", limitPlaces=" + limitPlaces +
                ", city='" + city +
                ", countryIATAcode='" + countryIATAcode +
                ", databaseKey='" + databaseKey +
                ", kindsCategory=" + kindsCategory +
                '}';
    }
}
