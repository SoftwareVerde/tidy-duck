#!/bin/bash

WAR_MODULE_DIR="$(dirname $0)/../app/"
WAR_MODULE_PATH="$WAR_MODULE_DIR/build/libs/*.war"
SERVER="dev.tidy-duck.sv.net"
DEPLOY_PATH="/var/lib/tomcat7/webapps/"
TARGET_WAR_FILE_NAME="ROOT.war"

# build war file
cd $WAR_MODULE_DIR
./gradlew war
cd -
if [[ $? == 0 ]]; then
    if [[ ! -f $WAR_MODULE_PATH ]]; then
        echo "$WAR_MODULE_PATH does not exist."
        exit 1
    fi
    scp $WAR_MODULE_PATH $SERVER:~/$TARGET_WAR_FILE_NAME
    ssh -t $SERVER "sudo mv $TARGET_WAR_FILE_NAME $DEPLOY_PATH; sudo chown tomcat7:tomcat7 $DEPLOY_PATH/$TARGET_WAR_FILE_NAME"
fi
