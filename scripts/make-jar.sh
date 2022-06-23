#!/bin/bash

date

rm -rf out/bin 2>/dev/null
mkdir -p out/bin/lib/

cd app
./gradlew copyDependencies makeJar || exit 1

cd ..
./scripts/package.sh

