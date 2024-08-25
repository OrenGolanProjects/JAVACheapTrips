package com.orengolan.cheaptrips.cheaptripsapp;

import com.orengolan.cheaptrips.flight.Flight;
import com.orengolan.cheaptrips.news.News;
import com.orengolan.cheaptrips.opentripmap.PlacesData;

/**
 * The {@code CheapTripsResponse} class represents a response object containing information about a cheap trip.
 * It encapsulates details such as flight information, relevant news, and data about places at the destination.
 * This class is designed to provide a comprehensive overview of a travel opportunity, combining flight details,
 * news updates, and information about places from OpenTripMap.
 *
 * Key Features:
 * - Aggregates information from multiple sources, including flight details, news, and places data.
 * - Enables a holistic view of a travel opportunity by combining various aspects of the trip.
 * - Supports easy retrieval and modification of flight, news, and places data components.
 *
 * Example Usage:
 * Flight flightDetails = new Flight();
        * News travelNews = new News();
        * PlacesData destinationData = new PlacesData();
        * CheapTripsResponse tripResponse = new CheapTripsResponse(flightDetails, travelNews, destinationData);
        * // Access and utilize information about the cheap trip from the response object.
        */
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
