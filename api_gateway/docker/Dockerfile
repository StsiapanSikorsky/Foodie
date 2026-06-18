FROM eclipse-temurin:21-jre

WORKDIR /srv

COPY target/*.jar .
COPY docker/api-gateway.sh .

RUN chmod +x api-gateway.sh

EXPOSE 8200

ENTRYPOINT ["./api-gateway.sh"]