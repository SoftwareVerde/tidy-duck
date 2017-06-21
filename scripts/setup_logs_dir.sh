#!/bin/bash
LOGS_DIR="/var/log/tidy-duck/"

if [[ $# -ne 1 ]];then
    echo "Usage: $0 user:group"
    exit 1
fi

# determine user/group
ownership="$1"

# create directory
sudo mkdir -p $LOGS_DIR
# update permissions
sudo chown $ownership $LOGS_DIR

