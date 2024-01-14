# choose base image to build off of
FROM node:lts-alpine
LABEL org.opencontainers.image.authors="dec21.eu"

ENV DOCKER_VER=1.0.0

# set the current working directory for all commands
WORKDIR /usr/src/app
COPY . .
RUN npm install && /usr/src/app/node_modules/.bin/ng build

# expose internal docker container port to external environment
EXPOSE 4200

# specify default command to run when we run the image
ENTRYPOINT if [ -z ${WEEKPLAN_BASE_SRV_URL+x} ] || [ "$WP_ACTIVE_WEB_ENV" != custom ] ; then \
               /usr/src/app/node_modules/.bin/ng serve --host 0.0.0.0 --disable-host-check -c ${WP_ACTIVE_WEB_ENV}; \
            else \
               sed -i "s|http://kubernetes.docker.internal|$WEEKPLAN_BASE_SRV_URL|g" ./src/environments/* ; \
               sed -i "s|http://localhost|$WEEKPLAN_BASE_SRV_URL|g" ./src/environments/* ; \
               /usr/src/app/node_modules/.bin/ng build; \
               export WP_ACTIVE_WEB_ENV=production; \
               /usr/src/app/node_modules/.bin/ng serve --host 0.0.0.0 --disable-host-check -c ${WP_ACTIVE_WEB_ENV}; \
            fi
