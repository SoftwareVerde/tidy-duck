#!/bin/bash
mysql tidy_duck -u root -p < "$(dirname $0)/../sql/init.sql"

