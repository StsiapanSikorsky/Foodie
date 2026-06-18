#!/usr/bin/env bash

PROFILE=${PROFILE:-local-idea}

echo "Starting booking-service with profile: $PROFILE"

exec java -jar /srv/booking_service-0.0.1-SNAPSHOT.jar --spring.profiles.active=$PROFILE