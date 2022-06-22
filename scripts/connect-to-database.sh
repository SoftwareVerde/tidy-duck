#!/bin/bash

USER='root'
PASSWORD='ROOT_PASSWORD'
DATABASE='tidy_duck'
PORT='3306'
HOST='127.0.0.1'

mysql -u ${USER} -h ${HOST} -P${PORT} -p${PASSWORD} ${DATABASE}

