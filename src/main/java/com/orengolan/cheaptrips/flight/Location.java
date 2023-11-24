package com.orengolan.cheaptrips.flight;

import com.orengolan.cheaptrips.city.City;
import com.orengolan.cheaptrips.countries.Country;
import org.jetbrains.annotations.NotNull;

public class Location {
    private City city;
    private Country country;

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
