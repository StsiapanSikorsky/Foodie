FROM eclipse-temurin:21-jre

WORKDIR /srv

COPY target/*.jar .
COPY docker/restaurant-service.sh .

RUN chmod +x restaurant-service.sh

EXPOSE 8198

ENTRYPOINT ["./restaurant-service.sh"]