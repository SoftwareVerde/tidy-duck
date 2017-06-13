#!/bin/bash

# DESTINATION='../../tomcat'
DESTINATION='/usr/local/Cellar/tomcat/8.5.15/libexec/webapps'

rm ${DESTINATION}/*.war 2>/dev/null
./gradlew war && cp $(ls -tr build/libs/*.war | tail -1) ${DESTINATION}/ROOT.war && chmod 770 ${DESTINATION}/ROOT.war

