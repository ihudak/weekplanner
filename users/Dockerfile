ARG BASE_REPO=ihudak
ARG AGENT=agents
ARG PLATFORM=x64
ARG BASE_IMG_TAG=latest

ENV spring_profiles_active=prod

FROM ${BASE_REPO}/java-${AGENT}-${PLATFORM}:${BASE_IMG_TAG}
MAINTAINER dec21.eu

ARG JAR_FILE=build/libs/*0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /opt/app/app.jar
