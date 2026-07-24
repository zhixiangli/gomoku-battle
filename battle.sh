#!/bin/bash

set -euo pipefail

BASE_DIR=$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)
BIN_DIR="${BASE_DIR}/bin"
LOG_DIR="${BASE_DIR}/log"

function usage {
    cat <<EOM
Usage: $(basename "$0") [OPTION]...
  -c VALUE    specify the configuration file
  -d          run without UI
  -p          validate the AlphaZero setup and exit
  -h          display help
EOM
}

function fail {
    echo "Error: $*" >&2
    return 1
}

function alphazero_preflight {
    local player_config=$1
    local alpha_command
    alpha_command=$(sed -n 's/^[[:space:]]*agent\.alphazero\.cmd[[:space:]]*=[[:space:]]*//p' "$player_config" | tail -n 1)
    if [[ -z "$alpha_command" ]]; then
        fail "agent.alphazero.cmd is missing from ${player_config}"
    fi

    (
        cd "$BASE_DIR"
        sh -c "${alpha_command} --preflight"
    )
}

function main {
    local jar_filename="gomoku-battle-dashboard-*-jar-with-dependencies.jar"
    local player_config=""
    local preflight_only=false
    while getopts ":c:dhp" opt; do
        case $opt in
            c)
                player_config=$OPTARG
                ;;
            d)
                jar_filename="gomoku-battle-console-*-jar-with-dependencies.jar"
                ;;
            p)
                preflight_only=true
                ;;
            h)
                usage
                return 0
                ;;
            :)
                fail "Option -${OPTARG} requires a value"
                ;;
            \?)
                fail "Unknown option: -${OPTARG}"
                ;;
        esac
    done
    if [[ -z "$player_config" ]]; then
        usage
        return 2
    fi
    if [[ ! -f "$player_config" ]]; then
        fail "Player configuration does not exist: ${player_config}"
    fi
    if ! command -v java >/dev/null 2>&1; then
        fail "Java is not installed or not on PATH"
    fi

    local -a jars=()
    mapfile -t jars < <(compgen -G "${BIN_DIR}/${jar_filename}" || true)
    if (( ${#jars[@]} == 0 )); then
        fail "No launch JAR found in ${BIN_DIR}; run ./build.sh first"
    fi
    if (( ${#jars[@]} > 1 )); then
        fail "Multiple launch JARs found in ${BIN_DIR}; clean and rebuild"
    fi

    if [[ "$preflight_only" == true ]]; then
        alphazero_preflight "$player_config"
        echo "Battle preflight passed"
        return 0
    fi

    mkdir -p "$LOG_DIR"
    java -jar "${jars[0]}" -player "$player_config"
}

main "$@"
