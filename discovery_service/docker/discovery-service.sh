#!/usr/bin/env bash

PROFILE=${PROFILE:-local-idea}

echo "Starting discovery-service with profile: $PROFILE"

exec java -jar /srv/discovery_service-0.0.1-SNAPSHOT.jar --spring.profiles.active=$PROFILE