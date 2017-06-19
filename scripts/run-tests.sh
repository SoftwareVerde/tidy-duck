#!/bin/bash

cd app
./gradlew test
cd -

cd most-adapter
./gradlew test
cd -

