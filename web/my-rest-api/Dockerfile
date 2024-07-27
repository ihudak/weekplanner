# choose base image to build off of
FROM node:lts-alpine
LABEL org.opencontainers.image.authors="dec21.eu"

ENV DOCKER_VER=1.0.0

# set the current working directory for all commands
WORKDIR /usr/src/app
COPY . .
RUN npm install

# expose internal docker container port to external environment
EXPOSE 3000

# specify default command to run when we run the image
ENTRYPOINT node app.js
