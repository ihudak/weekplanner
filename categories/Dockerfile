FROM ubuntu:jammy
LABEL org.opencontainers.image.authors="dec21.eu"

ENV EXECUTABLE=categories

RUN mkdir -p /opt/agent
COPY build/native/graalnative/ /opt/agent/
ENV DT_GRAALNATIVE_AGENT_DIR=/opt/agent

RUN mkdir -p /opt/app
WORKDIR /opt/app
COPY build/native/nativeCompile/ .
RUN chmod +x "$EXECUTABLE"

RUN touch /opt/app/dynatrace/agent/dt_fips_disabled.flag && \
    touch /opt/app/dynatrace/agent/any/dt_fips_disabled.flag && \
    touch /opt/agent/runtime/dt_fips_disabled.flag && \
    touch /opt/agent/runtime/any/dt_fips_disabled.flag && \
    touch /opt/agent/buildtime/dt_fips_disabled.flag && \
    touch /opt/agent/buildtime/any/dt_fips_disabled.flag

RUN apt-get update && \
    apt-get install -y curl sed && \
    rm -rf /var/lib/apt/lists/*

ENTRYPOINT export TENANT_URL=$(echo "$TENANT_URL" | sed -E 's/[ '\$'/'']+\$//') && \
           export TENANT_TOKEN=$(curl -X GET "$TENANT_URL"/api/v1/deployment/installer/agent/connectioninfo \
            -H "accept: application/json" \
            -H "Authorization: Api-Token $OA_TOKEN" | \
           grep tenantToken | \
           sed s/\ \ \"tenantToken\"\ :\ \"// | sed s/\",//) && \
           export DT_AGENT_OPTIONS=tenant=$TENANT_ID,tenantToken=$TENANT_TOKEN,server="$TENANT_URL:443" && \
           "./$EXECUTABLE"
