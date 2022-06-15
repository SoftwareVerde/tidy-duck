#!/bin/bash

date

rm -rf out 2>/dev/null
mkdir -p out/bin/lib


cd app
./gradlew clean copyDependencies makeJar || exit 1

cd ..
./scripts/package.sh

# Create Database Directories
mkdir -p out/data
mkdir -p out/tmp
