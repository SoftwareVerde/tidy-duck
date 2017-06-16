#!/bin/bash

cd /usr/local/Cellar/tomcat/8.5.15/libexec/work/Catalina/
find . -name 'SESSIONS.ser' | xargs -I {} rm {}

