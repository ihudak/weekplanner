FROM ubuntu:jammy
LABEL org.opencontainers.image.authors="dec21.eu"

ENV EXECUTABLE=tasks

RUN mkdir -p /opt/app
WORKDIR /opt/app
COPY build/native/nativeCompile/ .
RUN chmod +x "$EXECUTABLE"

RUN TENANT_URL=$(echo "$TENANT_URL" | sed -E 's/[ '\$'/'']+\$//') && \
    export TENANT_URL && \
    TENANT_TOKEN=$(curl -X GET "$TENANT_URL"/api/v1/deployment/installer/agent/connectioninfo \
      -H "accept: application/json" \
      -H "Authorization: Api-Token $OA_TOKEN" | \
    grep tenantToken | \
    sed s/\ \ \"tenantToken\"\ :\ \"// | sed s/\",//) && \
    export TENANT_TOKEN && \
    DT_AGENT_OPTIONS=tenant="$TENANT_ID",tenantToken="$TENANT_TOKEN",server="https://$TENANT_ID.$TENANT_LAYER.dynatracelabs.com:443" \
    export DT_AGENT_OPTIONS

ENTRYPOINT "./$EXECUTABLE"
