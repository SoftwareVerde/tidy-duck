#!/bin/bash

rm -rf .gradle/
./gradlew clean test war -Pconfiguration=production

