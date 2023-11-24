FROM openjdk:11

# Copy the JAR file into the container
COPY ./CheapTrips*.jar /usr/src/CheapTrips.jar

# Copy the application.properties file
COPY src/main/resources/application.properties /opt/conf/application.properties

# Set the working directory
WORKDIR /usr/src/

# Set the memory limit for the container
CMD ["java", "-Xmx512m", "-jar", "CheapTrips.jar", "--spring.config.location=file:/opt/conf/application.properties"]
