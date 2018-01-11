#!/bin/bash

set -x
set -e

BASE_DIR=$(dirname "$0")
BIN_DIR=${BASE_DIR}/bin/
LOG_DIR=${BASE_DIR}/log/

DASHBOARD_JAR=${BASE_DIR}/gomoku-battle-dashboard/target/gomoku-battle-dashboard-*-jar-with-dependencies.jar
CONSOLE_JAR=${BASE_DIR}/gomoku-battle-console/target/gomoku-battle-console-*-jar-with-dependencies.jar
POM_FILE=${BASE_DIR}/pom.xml

ALPHA_BETA_SEARCH_JAR=${BASE_DIR}/gomoku-battle-alphabetasearch/target/gomoku-battle-alphabetasearch-*-jar-with-dependencies.jar

function build_platform {
    mvn clean -f ${POM_FILE}
    mvn package -f ${POM_FILE}
}

function deploy_bin {
    mkdir -p ${BIN_DIR} ${LOG_DIR}

    cp -f ${DASHBOARD_JAR} ${BIN_DIR}
    cp -f ${CONSOLE_JAR} ${BIN_DIR}
    cp -f ${ALPHA_BETA_SEARCH_JAR} ${BIN_DIR}
}

function main {
    build_platform
    deploy_bin
}

main $@

