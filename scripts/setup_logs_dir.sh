#!/bin/bash
LOGS_DIR="/var/log/tidy-duck/"

# determine user/group
group=""
if [ $(getent group tomcat7) ]; then
    group="tomcat7"
elif [ $(getent group tomcat8) ]; then
    group="tomcat8"
else
    echo "Unable to determine tomcat user/group."
    exit 1
fi
# create directory
sudo mkdir -p $LOGS_DIR
# update permissions
sudo chown $group:$group $LOGS_DIR

