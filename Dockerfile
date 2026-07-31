FROM maven:3.9.16-eclipse-temurin-21-noble AS build

WORKDIR /app

COPY pom.xml .

RUN mvn --batch-mode --no-transfer-progress dependency:go-offline

COPY src ./src

RUN mvn --batch-mode --no-transfer-progress package



FROM eclipse-temurin:21-jre AS runtime

WORKDIR /app

COPY --from=build /app/target/cinema-1.0-SNAPSHOT-all.jar app.jar

USER 10001

ENTRYPOINT ["java", "-cp", "app.jar"]
CMD ["fr.diginamic.App"]
