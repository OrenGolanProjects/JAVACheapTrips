package com.orengolan.cheaptrips.flight;

import com.orengolan.cheaptrips.city.City;
import com.orengolan.cheaptrips.countries.Country;
import org.jetbrains.annotations.NotNull;

/**
 * The {@code Location} class represents a geographical location, combining information about
 * a specific city and its associated country. It serves as a convenient container for bundling
 * details about a city and its corresponding country within the context of flight management.
 *
 * Key Features:
 * - {@code city}: Represents the city associated with the location.
 * - {@code country}: Represents the country associated with the location.
 *
 * Example:
 * The class can be used to create instances of locations, where each location encapsulates
 * information about a city and its corresponding country. It is commonly used in flight-related
 * scenarios to provide a unified representation of departure and destination locations.
 *
 * Note: This class is an integral part of the flight management module, contributing to the
 * modeling of flight-related entities by combining city and country details into a single
 * location object.
 */
public class Location {
    private City city;
    private Country country;

    /**
     * Constructs a new {@code Location} instance with the specified city and country.
     *
     * @param city The city associated with the location.
     * @param country The country associated with the location.
     */
    public Location(@NotNull City city,@NotNull Country country) {
        this.city = city;
        this.country = country;
    }

    @Override
    public String toString() {
        return "Location{" +
                "city=" + city +
                ", country=" + country +
                '}';
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
