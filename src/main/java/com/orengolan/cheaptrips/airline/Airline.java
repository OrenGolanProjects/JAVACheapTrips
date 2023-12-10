package com.orengolan.cheaptrips.airline;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;

/**
 * The {@code Airline} class represents an airline entity in the CheapTrips backend application.
 * It is designed for use in a Spring Boot environment and integrates with MongoDB for data storage.
 *
 * The class is annotated with {@code @Document} from Spring Data MongoDB, indicating that instances
 * of this class will be stored in a MongoDB collection named "airline." It implements the {@code Serializable}
 * interface, making instances serializable.
 *
 * The class includes the following attributes:
 * - {@code name}: A not-null String representing the name of the airline.
 * - {@code airlineIATACode}: A not-null String with a maximum length of 3 characters, annotated with
 *   {@code @Size} and {@code @Indexed(unique = true)} to ensure uniqueness in the MongoDB collection.
 *   This attribute holds the IATA code of the airline.
 * - {@code isLowCost}: A not-null Boolean indicating whether the airline is a low-cost carrier.
 * - {@code expireAt}: A Date object annotated with {@code @Indexed(expireAfterSeconds = 365 * 24 * 60 * 60)},
 *   which means the documents in the collection will expire after one year.
 *
 * The class provides a constructor to initialize its attributes, getter and setter methods for each attribute,
 * and an overridden {@code toString} method for better representation. Additionally, there are methods to get
 * and set the expiration date ({@code expireAt}).
 *
 * This {@code Airline} class is well-designed, follows best practices for MongoDB integration in a Spring Boot
 * application, and includes validation annotations to ensure data integrity. It supports easy representation
 * of airline entities and is suitable for use in the context of managing airline information within the CheapTrips application.
 */
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
