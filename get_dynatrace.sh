#!/bin/bash

PLATFORM=$(uname -p);
export PLATFORM;
if [ "$PLATFORM" = "arm" ] || [ "$PLATFORM" = "arm64" ] || [ "$PLATFORM" = "aarch64" ]; then
  export PLATFORM="arm";
elif [ $PLATFORM = "x86" ] || [ $PLATFORM = "x86_64" ] || [ $PLATFORM = "x64" ]; then
  export PLATFORM="x86";
else
  export PLATFORM="x86";
fi

wget -O linux-agent.zip http://graalnative.eastus.cloudapp.azure.com:8080/linux-$PLATFORM.zip
wget -O buildtools.zip http://graalnative.eastus.cloudapp.azure.com:8080/buildtools.zip
mkdir plugin && mv buildtools.zip ./plugin/ && cd ./plugin && unzip ./buildtools.zip
