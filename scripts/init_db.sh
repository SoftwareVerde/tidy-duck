#!/bin/bash
psql -d tidy_duck -f "$(dirname $0)/../sql/init.sql"

