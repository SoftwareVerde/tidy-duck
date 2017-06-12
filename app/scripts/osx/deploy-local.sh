#!/bin/bash

DESTINATION='../../tomcat'

rm ${DESTINATION}/*.war 2>/dev/null
./gradlew war && cp $(ls -tr build/libs/*.war | tail -1) ${DESTINATION}/tidy-duck.war && chmod 770 ${DESTINATION}/*.war

