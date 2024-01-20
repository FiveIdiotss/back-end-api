#Docker
FROM openjdk:17
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} mementee-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/mementee-0.0.1-SNAPSHO.jar"]
