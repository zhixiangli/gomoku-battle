#!/bin/bash

set -x

BASE_DIR=$(dirname "$0")
BIN_DIR=${BASE_DIR}/bin/
LOG_DIR=${BASE_DIR}/log/
CONF_DIR=${BASE_DIR}/conf/

ALPHA_ZERO_DIR=${BASE_DIR}/alpha-gomoku-agent/alpha-gomoku-zero/
ALPHA_ZERO_BIN=${ALPHA_ZERO_DIR}/alpha_gomoku_zero
ALPHA_ZERO_CONF=${ALPHA_ZERO_DIR}/conf/alpha-gomoku-zero.json

DASHBOARD_JAR=${BASE_DIR}/alpha-gomoku-dashboard/target/alpha-gomoku-dashboard-*-jar-with-dependencies.jar
CONSOLE_JAR=${BASE_DIR}/alpha-gomoku-console/target/alpha-gomoku-console-*-jar-with-dependencies.jar
ALPHA_BETA_SEARCH_JAR=${BASE_DIR}/alpha-gomoku-agent/alpha-beta-search/target/alpha-beta-search-*-jar-with-dependencies.jar
POM_FILE=${BASE_DIR}/pom.xml

function build_platform {
    # build console & alpha-beta-search
    mvn clean -f ${POM_FILE}
    mvn package -f ${POM_FILE}

    # build alpha-gomoku-zero
    make clean -C ${ALPHA_ZERO_DIR}
    make -C ${ALPHA_ZERO_DIR}
}

function deploy_bin {
    mkdir -p ${BIN_DIR} ${LOG_DIR} ${CONF_DIR}

    cp -f ${ALPHA_ZERO_BIN} ${BIN_DIR}
    cp -f ${ALPHA_ZERO_CONF} ${CONF_DIR}

    cp -f ${DASHBOARD_JAR} ${BIN_DIR}
    cp -f ${CONSOLE_JAR} ${BIN_DIR}
    cp -f ${ALPHA_BETA_SEARCH_JAR} ${BIN_DIR}
}

function main {
    build_platform
    deploy_bin
}

main $@

