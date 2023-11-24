package com.orengolan.cheaptrips.countries;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

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
