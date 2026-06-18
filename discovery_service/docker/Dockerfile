FROM eclipse-temurin:21-jre

WORKDIR /srv

COPY target/*.jar .
COPY docker/discovery-service.sh .

RUN chmod +x discovery-service.sh

EXPOSE 8201

ENTRYPOINT ["./discovery-service.sh"]