#!/bin/bash

TOMCAT_VERSION="`ls /opt/homebrew/Cellar/tomcat/ | sort -n | tail -n 1`"
/opt/homebrew/Cellar/tomcat/${TOMCAT_VERSION}/bin/catalina run

