#!/bin/bash

WAR_MODULE_DIR="$(dirname $0)/../app/"
WAR_MODULE_PATH="$WAR_MODULE_DIR/build/libs/*.war"
DEPLOY_PATH="/var/lib/tomcat7/webapps/"
TARGET_WAR_FILE_NAME="ROOT.war"

FILE=$1
SERVER=$1
if [[ ! -f ${FILE} ]]; then
    echo -e "Unable to find warfile: ${FILE}\nUsage: $0 <path-to-warfile> <url-to-server>"
    exit 1
fi

if [[ -z ${SERVER} ]]; then
    echo -e "Invalid Server Destination: ${SERVER}\nUsage: $0 <path-to-warfile> <url-to-server>"
    exit 1
fi

scp "${FILE}" ${SERVER}:~/${TARGET_WAR_FILE_NAME}
ssh -t ${SERVER} "echo Server-Side Sudo Password:; sudo rm -rf ${DEPLOY_PATH}/*; sudo mv ${TARGET_WAR_FILE_NAME} ${DEPLOY_PATH}; sudo chown tomcat7:tomcat7 ${DEPLOY_PATH}/${TARGET_WAR_FILE_NAME}"

echo "Done."

