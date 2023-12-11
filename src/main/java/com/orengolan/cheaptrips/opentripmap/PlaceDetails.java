package com.orengolan.cheaptrips.opentripmap;

import com.mongodb.lang.Nullable;
import javax.validation.constraints.NotNull;

/**
 * The {@code PlaceDetails} class represents detailed information about a place, including its unique identifier, name, rating,
 * kinds, geographical coordinates, address, and additional metadata. It includes nested classes for {@code Address} and {@code Point}
 * to encapsulate related information.
 *
 * Key Features:
 * - Stores comprehensive details about a place, such as xid, name, rate, kinds, sources, and additional metadata.
 * - Utilizes nested classes for {@code Address} and {@code Point} to encapsulate address-related and geographical coordinate details.
 * - Facilitates the representation of a place's attributes, including OpenStreetMap (osm), Wikidata, image, and Wikipedia extracts.
 *
 * Example Usage:
 * The class is employed to encapsulate detailed information about a place obtained from the OpenTripMap service. It includes methods
 * to retrieve specific attributes such as xid, name, rate, kinds, sources, OpenStreetMap (osm) identifier, URL, and more.
 * The {@code toString} method provides a formatted string representation of the place details for easy logging or debugging.
 *
 * Note: This class serves as a crucial data structure in the application, encapsulating detailed information about places
 *   obtained from external services such as the OpenTripMap API.
 */
public class PlaceDetails {

    @NotNull
    private final String xid;
    @NotNull
    private final String name;
    @NotNull
    private final String rate;
    @NotNull
    private final String kinds;
    @NotNull
    private final String sources;
    @Nullable
    private final String otm;
    @NotNull
    private final Point point;
    @NotNull
    private final Address address;

    @NotNull
    private final String osm;
    @Nullable
    private final String url;
    @Nullable
    private final String wikidata;
    @Nullable
    private final String image;
    @Nullable
    private final String preview;
    @Nullable
    private final String wikipedia_extracts;

    public static class Address{
        @NotNull
        private final String city;
        @Nullable
        private final String road;
        @NotNull
        private final String state;
        @Nullable
        private final String suburb;
        @NotNull
        private final String country;
        @NotNull
        private final String postcode;
        @NotNull
        private final String country_code;
        @Nullable
        private final String house_number;
        @Nullable
        private final String neighbourhood;

        public Address(String city,
                       String state,
                       String country,
                       String postcode,
                       String country_code,
                       @Nullable String road,
                       @Nullable String suburb,
                       @Nullable String house_number,
                       @Nullable String neighbourhood) {
            this.city = city;
            this.road = road;
            this.state = state;
            this.suburb = suburb;
            this.country = country;
            this.postcode = postcode;
            this.country_code = country_code;
            this.house_number = house_number;
            this.neighbourhood = neighbourhood;
        }

        public String getCity() {
            return this.city;
        }

        @Nullable
        public String getRoad() {
            return this.road;
        }

        public String getState() {
            return this.state;
        }

        @Nullable
        public String getSuburb() {
            return this.suburb;
        }

        public String getCountry() {
            return this.country;
        }

        public String getPostcode() {
            return this.postcode;
        }

        public String getCountry_code() {
            return this.country_code;
        }
        @Nullable
        public String getHouse_number() {
            return this.house_number;
        }

        @Nullable
        public String getNeighbourhood() {
            return this.neighbourhood;
        }

        @Override
        public String toString() {
            return "Address{" +
                    "city='" + this.getCity() +
                    ", road='" + this.getRoad() +
                    ", state='" + this.getState() +
                    ", suburb='" + this.getSuburb() +
                    ", country='" + this.getCountry() +
                    ", postcode='" + this.getPostcode() +
                    ", country_code='" + this.getCountry_code() +
                    ", house_number='" + this.getHouse_number() +
                    ", neighbourhood='" + this.getNeighbourhood() +
                    '}';
        }
    }
    public static class Point{
        private final Double lon;
        private final Double lat;

        public Point(Double lon, Double lat) {
            this.lon = lon;
            this.lat = lat;
        }

        public Double getLon() {
            return this.lon;
        }

        public Double getLat() {
            return this.lat;
        }

        @Override
        public String toString() {
            return "Point{" +
                    "lon=" + this.getLon() +
                    ", lat=" + this.getLat() +
                    '}';
        }
    }

    public PlaceDetails(String xid, String name,
                        String rate, String kinds,
                        String sources,@Nullable String otm,
                        String osm, @Nullable String url,
                        @Nullable String wikidata,@Nullable String image,
                        @Nullable String preview,@Nullable String wikipedia_extracts,
                        Point point, Address address) {
        this.xid = xid;
        this.name = name;
        this.rate = rate;
        this.kinds = kinds;
        this.sources = sources;
        this.otm = otm;
        this.point = point;
        this.address = address;
        this.osm = osm;
        this.url = url;
        this.wikidata = wikidata;
        this.image = image;
        this.preview = preview;
        this.wikipedia_extracts = wikipedia_extracts;
    }

    public String getXid() {
        return xid;
    }

    public String getName() {
        return name;
    }

    public String getRate() {
        return rate;
    }

    public String getKinds() {
        return kinds;
    }

    public String getSources() {
        return sources;
    }

    @Nullable
    public String getOtm() {
        return otm;
    }

    public Point getPoint() {
        return point;
    }

    public Address getAddress() {
        return address;
    }

    public String getOsm() {
        return osm;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    @Nullable
    public String getWikidata() {
        return wikidata;
    }

    @Nullable
    public String getImage() {
        return image;
    }

    @Nullable
    public String getPreview() {
        return preview;
    }

    @Nullable
    public String getWikipedia_extracts() {
        return wikipedia_extracts;
    }

    @Override
    public String toString() {
        return "PlaceDetails{" +
                "xid='" + this.getXid() +
                ", name='" + this.getName() +
                ", rate='" + this.getRate() +
                ", kinds='" + this.getKinds() +
                ", sources='" + this.getSources() +
                ", otm='" + this.getOtm() +
                ", osm='" + this.getOsm() +
                ", url='" + this.getUrl() +
                ", wikidata='" + this.getWikidata() +
                ", image='" + this.getImage() +
                ", preview='" + this.getPreview() +
                ", wikipedia_extracts='" +getWikipedia_extracts() +
                ", point=" + this.getPoint() +
                ", address=" + this.getAddress() +
                '}';
    }
}
