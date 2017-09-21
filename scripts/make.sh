#!/bin/bash

WAR_MODULE_DIR="$(dirname $0)/../app/"
WAR_MODULE_PATH="$WAR_MODULE_DIR/build/libs/*.war"
SERVER="qa.tidy-duck.sv.net"
DEPLOY_PATH="/var/lib/tomcat7/webapps/"
TARGET_WAR_FILE_NAME="ROOT.war"

echo rm ${WAR_MODULE_PATH} 2>/dev/null
rm ${WAR_MODULE_PATH} 2>/dev/null

# build war file
cd $WAR_MODULE_DIR
rm -rf .gradle/
./gradlew clean test war -Pconfiguration=production
cd -

file=$(ls -tr ${WAR_MODULE_PATH})
echo "$(cd "$(dirname "${file}")"; pwd)/$(basename "${file}")"
echo

