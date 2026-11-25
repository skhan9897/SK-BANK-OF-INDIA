# ==============================================================================
# SK BANK OF INDIA - DOCKER BUILD & DEPLOYMENT
# Multi-stage Docker build: Maven compile -> Tomcat 9 deployment
# ==============================================================================

FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM tomcat:9.0-jdk17-temurin
WORKDIR /usr/local/tomcat
RUN rm -rf webapps/*
COPY --from=build /app/target/SKBankOfIndia-1.0-SNAPSHOT.war webapps/ROOT.war

EXPOSE 8080
CMD ["catalina.sh", "run"]
