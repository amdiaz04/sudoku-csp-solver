FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build


COPY sudokuproject/pom.xml sudokuproject/pom.xml
COPY sudokuproject/src    sudokuproject/src
RUN mvn -f sudokuproject/pom.xml clean install -DskipTests

COPY sudoku-api/pom.xml sudoku-api/pom.xml
COPY sudoku-api/src     sudoku-api/src
RUN mvn -f sudoku-api/pom.xml clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app


RUN addgroup -S spring && adduser -S spring -G spring
USER spring


COPY --from=build /build/sudoku-api/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]