FROM openjdk:17-alpine
ARG JAR_FILE=build/libs/eatmate-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java","-Dspring.profiles.active=dev","-jar","/application.jar"]
