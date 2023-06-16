FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY build/libs/gws.jar gws.jar

EXPOSE 8080

ENV DB_HOST localhost
ENV DB_PORT 5432
ENV DB_NAME gym_db
ENV DB_USER postgres
ENV DB_PASSWORD admin

CMD ["java", "-jar", "gws.jar"]