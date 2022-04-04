#!/bin/bash

echo ""
echo "Your current Java version is"
echo "-------------------------------------------------"

java -version

echo ""

./gradlew -q clean

./gradlew -q runScript -PmainClass=working

./gradlew -q runScript -PmainClass=example.notworking

./gradlew -q runScript -PmainClass=example.more.run