FROM eclipse-temurin:21-jre

WORKDIR /srv

COPY target/*.jar .
COPY docker/authentication-service.sh .

RUN chmod +x authentication-service.sh

EXPOSE 8197

ENTRYPOINT ["./authentication-service.sh"]