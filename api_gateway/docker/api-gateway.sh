#!/usr/bin/env bash

PROFILE=${PROFILE:-local-idea}

echo "Starting api-gateway with profile: $PROFILE"

exec java -jar /srv/api_gateway-0.0.1-SNAPSHOT.jar --spring.profiles.active=$PROFILE