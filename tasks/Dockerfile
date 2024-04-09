FROM ubuntu:jammy
LABEL org.opencontainers.image.authors="dec21.eu"

ENV EXECUTABLE=tasks

RUN mkdir -p /opt/agent
COPY build/native/graalnative/ /opt/agent/
ENV DT_GRAALNATIVE_AGENT_DIR=/opt/agent

RUN mkdir -p /opt/app
WORKDIR /opt/app
COPY build/native/nativeCompile/ .
RUN chmod +x "$EXECUTABLE"

RUN apt-get update && \
    apt-get install -y curl sed && \
    rm -rf /var/lib/apt/lists/*

ENTRYPOINT if [ $TENANT_LAYER = "dev" ] || [ $TENANT_LAYER = "sprint" ]; then \
                export DT_SRV=dynatracelabs.com:443; \
           else \
                export DT_SRV=dynatrace.com:443; \
           fi && \
           export TENANT_URL=$(echo "$TENANT_URL" | sed -E 's/[ '\$'/'']+\$//') && \
           export TENANT_TOKEN=$(curl -X GET "$TENANT_URL"/api/v1/deployment/installer/agent/connectioninfo \
            -H "accept: application/json" \
            -H "Authorization: Api-Token $OA_TOKEN" | \
           grep tenantToken | \
           sed s/\ \ \"tenantToken\"\ :\ \"// | sed s/\",//) && \
           export DT_AGENT_OPTIONS=tenant=$TENANT_ID,tenantToken=$TENANT_TOKEN,server="https://$TENANT_ID.$TENANT_LAYER.$DT_SRV" && \
           echo $DT_AGENT_OPTIONS && \
           "./$EXECUTABLE"
