#!/bin/bash

date

rm -rf out/bin 2>/dev/null
mkdir -p out/bin/lib/

./gradlew copyDependencies makeJar || exit 1

./scripts/package.sh

