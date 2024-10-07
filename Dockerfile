FROM openjdk:11

# Copy the JAR file into the container
COPY target/CheapTrips*.jar /usr/src/CheapTrips.jar

# Copy the resources, including the .env and application.properties files
COPY src/main/resources /opt/conf

# Set the working directory
WORKDIR /usr/src/

# Adjust the memory settings based on your requirements
# Example: Set the maximum heap size to 1GB and the initial heap size to 512MB
CMD ["java", "-Xmx256m", "-Xms256m", "-jar", "CheapTrips.jar", "--spring.config.location=file:/opt/conf/application.properties"]