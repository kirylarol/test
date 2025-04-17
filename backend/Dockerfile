FROM maven:3.8.4-openjdk-17 as builder

WORKDIR /app
COPY . /app/

RUN mvn clean package -Dmaven.test.skip=true

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.war /app/app.war

EXPOSE 8181

ENTRYPOINT ["java", "-jar", "/app/app.war"]
