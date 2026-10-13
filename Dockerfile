# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage on Tomcat 9 with Java 17
FROM tomcat:9.0.85-jdk17
RUN rm -rf /usr/local/tomcat/webapps/*
COPY --from=build /app/target/SKBANKOFINDIA-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
CMD ["catalina.sh", "run"]
