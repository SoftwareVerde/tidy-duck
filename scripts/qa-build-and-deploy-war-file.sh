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
./scripts/make-qa.sh
cd -

file=$(ls -tr ${WAR_MODULE_PATH})
echo ${file}

if [[ $? == 0 ]]; then
    if [[ ! -f $file ]]; then
        echo "$WAR_MODULE_PATH does not exist."
        exit 1
    fi
    scp $file $SERVER:~/$TARGET_WAR_FILE_NAME
    ssh -t $SERVER "sudo rm -rf $DEPLOY_PATH/*; sudo mv $TARGET_WAR_FILE_NAME $DEPLOY_PATH; sudo chown tomcat7:tomcat7 $DEPLOY_PATH/$TARGET_WAR_FILE_NAME"
fi
