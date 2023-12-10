# CheapTrips Application

## Overview

Welcome to the CheapTrips application!

This travel management system offers a range of features, including user authentication, dynamic trip generation, and city information retrieval.

Utilizing modern technologies such as Spring Boot, MongoDB, Redis, and JWT authentication.

---

## Purpose

The CheapTrips application is designed to empower users to effortlessly plan and explore travel options.

With a focus on user-friendly interfaces and robust functionalities, CheapTrips aims to enhance the travel experience for individuals, making it an ideal tool for both personal and professional travel planning.

## Key Features

1. **Monthly Trip Generation:**
    - Generate monthly travel recommendations based on user preferences and travel parameters.

2. **Trip by Dates:**
    - Plan trips for specific dates, accommodating personalized travel schedules.

3. **City Search:**
    - Obtain detailed information about cities, facilitating informed travel decisions.

## Technology Stack

CheapTrips is built using the following technologies:

- **Java:** Core programming language.
- **Spring Boot:** Framework for building Java-based enterprise applications.
- **MongoDB:** NoSQL database for efficient data storage.
- **Redis:** In-memory data structure store for caching and improving application performance.
- **JWT Authentication:** Securing API endpoints with JSON Web Tokens.

---

## Table of Contents

- [Overview](#overview)
- [Purpose](#purpose)
- [Key Features](#key-features)
- [Technology Stack](#technology-stack)
- [Table of Contents](#table-of-contents)
- [Prerequisites](#prerequisites)
- [Getting Started](#getting-started)
    - [Using Docker](#using-docker)
- [Usage](#usage)
    - [City Search Example](#city-search-example)
    - [Generate Monthly Trip Example](#generate-monthly-trip-example)
    - [Generate Trip by Dates Example](#generate-trip-by-dates-example)
- [API Documentation](#api-documentation)

## Prerequisites

Before diving into the CheapTrips application, ensure you have the following prerequisites installed:

- [Java](https://www.java.com/)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MongoDB](https://www.mongodb.com/)
- [Redis](https://redis.io/)
- [Docker](https://www.docker.com/)

## Getting Started

1. **Clone the Repository:**

   ```bash
   git clone https://github.com/OrenGolanProjects/JAVACheapTrips.git
   cd cheap-trips
   ```
2. **Set Up MongoDB:**

   Ensure that you have a MongoDB instance ready.
3. Set Up Redis:

   Ensure that you have a Redis instance ready.

## Using Docker

Build and Run with Docker:

```bash
   Copy code
   docker-compose up --build
```
This command will build the Docker images and start the CheapTrips application.


## Usage

### City Search Example

Use the following curl command to search for a city:

```bash
   curl -X GET 
      --header 'Accept: application/json' 
      --header 'Authorization: Bearer <your-token>' 'https://www.search-trips-api.com/cheap-trip/city-search?cityName=<search-cityName>>'
```
---


### Generate Monthly Trip Example
Use the following curl command to generate a monthly trip:
```bash
   curl -X POST 
   --header 'Content-Type: application/json' 
   --header 'Accept: application/json' 
   --header 'Authorization: Bearer <your-token>' 
   -d '{
    "origin_cityIATACode": "TLV",
    "destination_cityIATACode": "AMS",
    "destination_cityName": "amsterdam",
    "radius": 10000,
    "limitPlaces": 2,
    "kinds": [
        "interesting_places",
        "amusements",
        "sport",
        "tourist_facilities",
        "accomodations",
        "adult"
    ]
}' 'https://www.search-trips-api.com/cheap-trip/generate-monthly-trip'
```

Example JSON for generating a monthly trip:
```json
{
   "origin_cityIATACode": "TLV",
   "destination_cityIATACode": "AMS",
   "destination_cityName": "amsterdam",
   "radius": 10000,
   "limitPlaces": 2,
   "kinds": [
      "interesting_places",
      "amusements",
      "sport",
      "tourist_facilities",
      "accomodations",
      "adult"
   ]
}
```

---
### Generate Trip by Dates Example

Use the following curl command to generate a trip by dates:

```bash
curl -X POST 
  --header 'Content-Type: application/json' 
  --header 'Accept: application/json' 
  --header 'Authorization: Bearer <your-token>' 
  -d '{
    "origin_cityIATACode": "TLV",
    "destination_cityIATACode": "AMS",
    "destination_cityName": "amsterdam",
    "radius": 10000,
    "limitPlaces": 2
  }' 'https://www.search-trips-api.com/cheap-trip/generate-trip-by-dates?departure_at=<your-departure_at>&return_at=<your-return_at>'

```
Replace <your-token>, <your-departure_at>, and <your-return_at> with the actual authentication token, departure date, and return date. Adjust the formatting or wording as needed.


**Example JSON for generating a trip by dates:**

```json
{
  "origin_cityIATACode": "TLV",
  "destination_cityIATACode": "AMS",
  "destination_cityName": "amsterdam",
  "radius": 10000,
  "limitPlaces": 2
}
```
Note: Make sure to replace the values (<departure_at>, <return_at>) when using the swagger-ui.

---

## API Documentation

Explore the CheapTrips API and interact with the available services using the following documentation.

### Authentication Token

To access the CheapTrips API services, you need to include your authentication token in the headers of your HTTP requests.

- **Header:** Authorization
- **Value:** Bearer \<your-token\>

### Swagger UI

Visit the Swagger UI to interactively explore and test the available API endpoints.

- **URL:** [Swagger UI](https://www.search-trips-api.com/swagger-ui.html)