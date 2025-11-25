#!/usr/bin/env bash

cd workitems/build/libs
export WP_SERVER_PORT=8084
export WP_PROFILE=dev
export WP_WRKITM_CNT=300
export OTEL_SERVICE_NAME=WorkItems
java -Dotel.service.name=$SERVICE_FULL_NAME -jar ./workitems-0.0.1-SNAPSHOT.jar
