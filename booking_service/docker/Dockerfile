FROM eclipse-temurin:21-jre

WORKDIR /srv

COPY target/*.jar .
COPY docker/booking-service.sh .

RUN chmod +x booking-service.sh

EXPOSE 8199

ENTRYPOINT ["./booking-service.sh"]