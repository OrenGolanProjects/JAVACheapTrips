package com.orengolan.cheaptrips.countries;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * The {@code Country} class represents a country entity in the CheapTrips application.
 * It is annotated with {@link org.springframework.data.mongodb.core.mapping.Document}
 * to indicate that instances of this class should be stored in the "countries" collection
 * in the MongoDB database.
 *
 * The class includes fields representing country information such as name, IATA code, currency,
 * and expiration date. The expiration date is annotated with {@link org.springframework.data.mongodb.core.index.Indexed}
 * and {@link org.springframework.data.mongodb.core.index.Indexed#expireAfterSeconds()}, ensuring that
 * country documents in the database expire after one year.
 *
 * Key Features:
 * - {@code countryName}: Represents the name of the country. Must be not null and between 2 to 10 characters.
 * - {@code countryIATACode}: Represents the IATA code of the country. Must be not null, unique, and 2 characters in length.
 * - {@code currency}: Represents the currency of the country. Must be not null and between 3 to 3 characters.
 * - {@code expireAt}: Represents the expiration date of the country document in the database, set to one year from creation.
 *
 * Example:
 * An instance of the {@code Country} class may represent a country with the name "United States",
 * IATA code "US", currency "USD", and an expiration date one year from creation.
 *
 * Note: This class is a data model for country entities and is used for MongoDB document mapping.
 */
@Document(collection = "countries")
public class Country implements Serializable {

    @NotNull
    @Size(min = 2, max = 10)
    private String countryName;
    @NotNull
    @Size(min = 2,max = 2)
    @Indexed(unique = true)
    private String countryIATACode;
    @NotNull
    @Size(min = 3, max = 3)
    private String currency;

    @Indexed(expireAfterSeconds = 365 * 24 * 60 * 60) // One year expiration
    private Date expireAt;

    public Country(@NotNull String countryName, @NotNull String countryIATACode, @NotNull String currency) {
        this.countryName = countryName;
        this.countryIATACode = countryIATACode;
        this.currency = currency;
        this.expireAt = new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)); // 1 year in milliseconds
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    public void setCountryIATACode(@NotNull String countryIATACode) {
        this.countryIATACode = countryIATACode;
    }

    @NotNull
    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(@NotNull String countryName) {
        this.countryName = countryName;
    }

    @NotNull
    public String getCountryIATACode() {
        return countryIATACode;
    }

    @NotNull
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(@NotNull String currency) {
        this.currency = currency;
    }

    @Override
    public String toString() {
        return "Country{" +
                "countryName='" + countryName +
                ", countryIATACode='" + countryIATACode +
                ", currency='" + currency +
                '}';
    }
}
