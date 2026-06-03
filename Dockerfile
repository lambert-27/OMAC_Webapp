FROM maven:3.8.5-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

FROM jetty:10.0.5-jre11
COPY --from=build /app/target/Omac_Webapp.war /var/lib/jetty/webapps/root.war