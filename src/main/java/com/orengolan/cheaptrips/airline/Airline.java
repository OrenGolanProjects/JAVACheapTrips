package com.orengolan.cheaptrips.airline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

@Document(collection = "airline")
public class Airline implements Serializable {

    @NotNull
    private String name;

    @NotNull
    @Size(max = 3)
    @Indexed(unique = true)
    private String airlineIATACode;

    @NotNull
    private Boolean isLowCost;

    @Indexed(expireAfterSeconds = 365 * 24 * 60 * 60) // One year expiration
    private Date expireAt;

    @JsonCreator
    public Airline(
            @JsonProperty("name") @NotNull String name,
            @JsonProperty("airlineIATACode") @NotNull String airlineIATACode,
            @JsonProperty("isLowCost") Boolean isLowCost
    ) {
        this.name = name;
        this.airlineIATACode = airlineIATACode;
        this.isLowCost = isLowCost;
        this.expireAt = new Date(System.currentTimeMillis() + (365L * 24 * 60 * 60 * 1000)); // 1 year in milliseconds
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    @NotNull
    public String getName() {
        return name;
    }

    public void setName(@NotNull String name) {
        this.name = name;
    }

    @NotNull
    public String getAirlineIATACode() {
        return airlineIATACode;
    }

    public void setAirlineIATACode(@NotNull String airlineIATACode) {
        this.airlineIATACode = airlineIATACode;
    }

    public Boolean getLowCost() {
        return isLowCost;
    }

    public void setLowCost(Boolean lowCost) {
        isLowCost = lowCost;
    }

    @Override
    public String toString() {
        return "Airline{" +
                "name='" + name + '\'' +
                ", airlineIATACode='" + airlineIATACode + '\'' +
                ", isLowCost=" + isLowCost +
                ", expireAt=" + expireAt +
                '}';
    }
}
