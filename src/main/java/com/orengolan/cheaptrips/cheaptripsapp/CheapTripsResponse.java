package com.orengolan.cheaptrips.cheaptripsapp;

import com.orengolan.cheaptrips.flight.Flight;
import com.orengolan.cheaptrips.news.News;
import com.orengolan.cheaptrips.opentripmap.PlacesData;

public class CheapTripsResponse {

    private Flight flight;
    private News news;
    private PlacesData placesData;

    public CheapTripsResponse(Flight flight, News news, PlacesData placesData) {
        this.flight = flight;
        this.news = news;
        this.placesData = placesData;
    }



    public Flight getFlight() {
        return flight;
    }

    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    public News getNews() {
        return news;
    }

    public void setNews(News news) {
        this.news = news;
    }

    public PlacesData getPlacesData() {
        return placesData;
    }

    public void setPlacesData(PlacesData placesData) {
        this.placesData = placesData;
    }

    @Override
    public String toString() {
        return "CheapTripsResponse{" +
                "flight=" + flight +
                ", news=" + news +
                ", placesData=" + placesData +
                '}';
    }
}
