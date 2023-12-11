FROM openjdk:11

# Copy the JAR file into the container
COPY target/CheapTrips*.jar /usr/src/CheapTrips.jar

# Copy the application.properties file
COPY src/main/resources/application.properties /opt/conf/application.properties

# Set the working directory
WORKDIR /usr/src/

# Adjust the memory settings based on your requirements
# Example: Set the maximum heap size to 1GB and the initial heap size to 512MB
CMD ["java", "-Xmx1g", "-Xms512m", "-jar", "CheapTrips.jar", "--spring.config.location=file:/opt/conf/application.properties"]
