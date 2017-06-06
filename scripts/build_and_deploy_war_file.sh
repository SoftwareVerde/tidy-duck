#!/bin/bash

VERSION="1.0-SNAPSHOT"
WAR_MODULE_DIR="$(dirname $0)/../tidy-duck/"
WAR_MODULE_PATH="$WAR_MODULE_DIR/build/libs/tidy-duck-$VERSION.war"
SERVER="dev.tidy-duck.sv.net"
DEPLOY_PATH="/var/lib/tomcat7/webapps/"
TARGET_WAR_FILE_NAME="tidy-duck.war"

# build war file
cd $WAR_MODULE_DIR
gradle war
if [[ $? == 0 ]]; then
    scp $WAR_MODULE_PATH $SERVER:~/$TARGET_WAR_FILE_NAME
    ssh -t $SERVER "sudo mv $TARGET_WAR_FILE_NAME $DEPLOY_PATH; sudo chown tomcat7:tomcat7 $DEPLOY_PATH/$TARGET_WAR_FILE_NAME"
fi
