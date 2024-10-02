#Docker
FROM openjdk:17
# Set the working directory
WORKDIR /app
# Copy only Gradle files first
COPY build.gradle gradle.properties /app/
COPY settings.gradle /app/
# Install libraries using Gradle Wrapper
RUN ./gradlew dependencies
# Copy the rest of the source code
COPY . /app
# Build the application
RUN ./gradlew build
# Copy the JAR file
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} mementee-0.0.1-SNAPSHOT.jar
# Set the port
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "mementee-0.0.1-SNAPSHOT.jar"]
