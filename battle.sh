#!/bin/bash

set -e
set -o pipefail

BASE_DIR=$(dirname "$0")
BIN_DIR=${BASE_DIR}/bin
LOG_DIR=${BASE_DIR}/log

function usage {
    cat <<EOM
Usage: $(basename "$0") [OPTION]...
  -c VALUE    specify the configuration file
  -d          run without UI
  -h          display help
EOM
    exit 2
}

function main {
    local jar_filename="gomoku-battle-dashboard-*-jar-with-dependencies.jar"
    while getopts ":c:d" opt; do
        case $opt in
            c)
                local player_config=$OPTARG
                ;;
            d)
                jar_filename="gomoku-battle-console-*-jar-with-dependencies.jar"
                ;;
            h|*)
                usage
                ;;
        esac
    done
    if [ -z "$player_config" ]; then
        usage
    fi

    mkdir -p ${LOG_DIR}
    java -jar ${BIN_DIR}/${jar_filename} -player ${player_config}
}

main $@
