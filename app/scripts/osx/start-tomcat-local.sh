#!/bin/bash

TOMCAT_VERSION="`ls /usr/local/Cellar/tomcat/ | sort -n | tail -n 1`"
/usr/local/Cellar/tomcat/${TOMCAT_VERSION}/bin/catalina run

