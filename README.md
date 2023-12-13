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
  - [Step 1: Obtain Authentication Token](#step-1-obtain-authentication-token) 
  - [Step 2: Authorize](#step-2-authorize)  
  - [Step 3: Create New User Information (If New User)](#step-3-create-new-user-information-if-new-user)
  - [Step 4: Start Using the CheapTrips Services](#step-4-start-using-the-cheaptrips-services)


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
        "limitPlaces": 2  
       }' 'https://www.search-trips-api.com/cheap-trip/generate-monthly-trip'
```

Example JSON for generating a monthly trip:
```json
{
  "origin_cityIATACode": "TLV",
  "destination_cityIATACode": "AMS",
  "destination_cityName": "amsterdam",
  "radius": 10000,
  "limitPlaces": 2
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
Note: Replace <your-token>, <your-departure_at>, and <your-return_at> with the actual authentication token, departure date, and return date. Adjust the formatting or wording as needed.


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

### Create New User Example
To create a new user and register with CheapTrips, use the following curl command:
```bash
curl -X POST 
  --header 'Content-Type: application/json' 
  --header 'Accept: application/json' 
  --header 'Authorization: Bearer <your-token>' 
  -d '{
        "username": "your_username",
        "firstName": "your_first_name",
        "surname": "your_surname",
        "phone": "your_phone_number"
       }' 'https://www.search-trips-api.com/app/userinfo/create-specific-user-info'
```
Note: Replace the placeholder values (your_username, your_first_name, your_surname, your_phone_number, and your_email) with the actual user details. Adjust the formatting or wording as needed.

**Example JSON for creating a new user:**
```json
{
  "username": "your_username",
  "firstName": "your_first_name",
  "surname": "your_surname",
  "phone": "your_phone_number"
}
```
Note: Upon successful registration, a new user will be created, and you can use the generated JWT token for further authentication in other CheapTrips API endpoints

---

## API Documentation

Explore the CheapTrips API and interact with the available services using the following documentation.

### Step 1: Obtain Authentication Token

Before accessing CheapTrips services, you need to obtain an authentication token.

#### Using Existing User:
To get the authentication token for an existing user, make a request to the `/authentication` endpoint.

```bash
curl -X POST 
  --header 'Content-Type: application/json' 
  -d '{
        "username": "your_existing_username",
        "password": "your_password"
       }' 'https://www.search-trips-api.com/authentication'
```

For New User:
If you don't have a JWT token, create a new user and get the token by making a request to the /user endpoint.
```bash
curl -X POST 
  --header 'Content-Type: application/json' 
  -d '{
        "username": "your_username",
        "password": "your_password"
       }' 'https://www.search-trips-api.com/user'
```

### Step 2: Authorize
Once you have the authentication token, authorize your requests by including it in the headers.
- **Header:** Authorization
- **Value:** Bearer \<your-token\>


### Step 3: Create New User Information (If New User)
If you are a new user, create your user information using the following curl command:
```bash
curl -X POST 
  --header 'Content-Type: application/json' 
  --header 'Authorization: Bearer <your-token>' 
  -d '{
        "username": "your_username",
        "firstName": "your_first_name",
        "surname": "your_surname",
        "phone": "your_phone_number"
       }' 'https://www.search-trips-api.com/app/userinfo/create-specific-user-info?email=your_email'
```
Replace the placeholder values with your actual details.

### Step 4: Start Using the CheapTrips Services
Now, you're ready to explore CheapTrips services. 
Use the Swagger UI to interactively explore and test the available API endpoints.

- **URL:** [Swagger UI](https://www.search-trips-api.com/swagger-ui.html)


