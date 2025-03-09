#!/bin/bash

DT_TENANT_URL=https://<tenant>.dev.dynatracelabs.com
DT_TOKEN=<token>

curl -X GET "$DT_TENANT_URL/api/v1/deployment/installer/agent/unix/paas/latest?flavor=default&arch=x86&bitness=64&include=java-graal-native&skipMetadata=true" -H "accept: application/octet-stream"  -H "Authorization: Api-Token $DT_TOKEN" -o linux-agent.zip

