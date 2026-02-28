FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package


FROM eclipse-temurin:17-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar bank-app.jar
ENTRYPOINT ["java", "-jar", "bank-app.jar"]