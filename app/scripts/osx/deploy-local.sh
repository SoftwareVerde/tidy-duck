#!/bin/bash

# DESTINATION='../../tomcat'
TOMCAT_VERSION="`ls /opt/homebrew/Cellar/tomcat/ | sort -n | tail -n 1`"
DESTINATION="/opt/homebrew/Cellar/tomcat/${TOMCAT_VERSION}/libexec/webapps"

date +"[%Y-%m-%d %H:%M:%S]"
rm ${DESTINATION}/ROOT* 2>/dev/null
cd "$(dirname $0)/../../"
./gradlew war && cp $(ls -tr build/libs/*.war | tail -1) ${DESTINATION}/ROOT.war && chmod 770 ${DESTINATION}/ROOT.war
cd -

