#!/bin/bash

BASE_DIR=$(dirname "$0")
BIN_DIR=${BASE_DIR}/bin
LOG_DIR=${BASE_DIR}/log

function battle {
    mkdir -p ${LOG_DIR}
    java -jar ${BIN_DIR}/alpha-gomoku-dashboard-*-jar-with-dependencies.jar -player $1
}

function main {
    battle $1
}

main $@

