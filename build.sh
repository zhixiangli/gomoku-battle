#!/bin/bash

set -x
set -e

BASE_DIR=$(dirname "$0")
BIN_DIR=${BASE_DIR}/bin/
LOG_DIR=${BASE_DIR}/log/
CONF_DIR=${BASE_DIR}/conf/

MCTS_DIR=${BASE_DIR}/alpha-gomoku-agent/monte-carlo-tree-search/
MCTS_BIN=${MCTS_DIR}/monte-carlo-tree-search
MCTS_CONF=${MCTS_DIR}/conf/config.json

DASHBOARD_JAR=${BASE_DIR}/alpha-gomoku-dashboard/target/alpha-gomoku-dashboard-*-jar-with-dependencies.jar
CONSOLE_JAR=${BASE_DIR}/alpha-gomoku-console/target/alpha-gomoku-console-*-jar-with-dependencies.jar
ALPHA_BETA_SEARCH_JAR=${BASE_DIR}/alpha-gomoku-agent/alpha-beta-search/target/alpha-beta-search-*-jar-with-dependencies.jar
POM_FILE=${BASE_DIR}/pom.xml

function build_platform {
    # build console & alpha-beta-search
    mvn clean -f ${POM_FILE}
    mvn package -f ${POM_FILE}

    # build monte-carlo-tree-search
    make clean -C ${MCTS_DIR}
    make -C ${MCTS_DIR}
}

function deploy_bin {
    mkdir -p ${BIN_DIR} ${LOG_DIR} ${CONF_DIR}

    cp -f ${MCTS_BIN} ${BIN_DIR}
    cp -f ${MCTS_CONF} ${CONF_DIR}/mcts.json

    cp -f ${DASHBOARD_JAR} ${BIN_DIR}
    cp -f ${CONSOLE_JAR} ${BIN_DIR}
    cp -f ${ALPHA_BETA_SEARCH_JAR} ${BIN_DIR}
}

function main {
    build_platform
    deploy_bin
}

main $@

