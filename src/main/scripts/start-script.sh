#!/bin/bash

SCRIPT_DIR=$(dirname "$(readlink -f -- ${BASH_SOURCE[0]})")

MAIN_CLASS=Runner

CLASS_PATH="${CLASS_PATH_OVERRIDE:-"$SCRIPT_DIR/../lib/*"}"
JAVA_OPTS=${JAVA_OPTS:-"-Xmx256m -Xms256m"}

java $JAVA_OPTS -classpath "$CLASS_PATH" -Dlogback.configurationFile="$SCRIPT_DIR/../conf/logback.xml" -DapplicationConf="$SCRIPT_DIR/../conf" -DapplicationInput="$SCRIPT_DIR/../input" "$MAIN_CLASS" "$@"

