#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""AlphaZero adapter for gomoku-battle stdio protocol.

Bridges the Java gomoku-battle framework with AlphaZero's MCTS engine.
Reads JSON requests from stdin, runs MCTS, and writes JSON responses to stdout.

Protocol
--------
Request  (stdin):  {"command":"NEXT_WHITE","rows":15,"columns":15,"chessboard":"B[77];W[78]"}
Response (stdout): {"rowIndex":7,"columnIndex":8}
"""

import glob
import argparse
import json
import logging
import os
import sys

import numpy

from alphazero.mcts import MCTS
from alphazero.nnet import AlphaZeroNNet
from gomoku_15_15.config import GomokuConfig
from gomoku_15_15.game import GomokuGame

# Directory containing this adapter script.
_ADAPTER_DIR = os.path.dirname(os.path.abspath(__file__))

# Root of the alphazero-board-games submodule (sibling of this adapter dir).
_SUBMODULE_DIR = os.path.join(os.path.dirname(_ADAPTER_DIR), "alphazero-board-games")

# Log file lives in the gomoku-battle/log directory; mode='w' clears previous logs each run.
_LOG_DIR = os.path.join(os.path.dirname(_ADAPTER_DIR), "log")
_LOG_FILE = os.path.join(_LOG_DIR, "alphazero.log")


def _parse_args():
    """Parse CLI arguments for adapter runtime settings."""
    parser = argparse.ArgumentParser(description="Run AlphaZero gomoku adapter")
    parser.add_argument(
        "--simulation-num",
        type=int,
        default=5000,
        help="Number of MCTS simulations per move (default: 5000)",
    )
    return parser.parse_args()


def _pick_ai_action(mcts, board, player):
    """Pick the best action using MCTS (same logic as alphazero.stdio_play)."""
    actions, counts = mcts.simulate(board, player)
    if len(actions) == 0:
        return None
    best = numpy.max(counts)
    best_actions = actions[counts == best]
    return int(numpy.random.choice(best_actions))


def _command_to_player(command: str) -> str:
    """Map ConsoleCommand to AlphaZero player string."""
    if command == "NEXT_BLACK":
        return "B"
    elif command == "NEXT_WHITE":
        return "W"
    raise ValueError(f"Unknown command: {command}")


def main():
    args = _parse_args()

    # Log to a file (cleared each run) so stdout stays clean for JSON protocol.
    os.makedirs(_LOG_DIR, exist_ok=True)
    handler = logging.FileHandler(_LOG_FILE, mode="w")
    handler.setFormatter(logging.Formatter("%(asctime)s %(levelname)s %(name)s: %(message)s"))
    logging.basicConfig(level=logging.INFO, handlers=[handler])
    logger = logging.getLogger(__name__)

    logger.info("Initializing AlphaZero adapter...")

    config = GomokuConfig()
    config.simulation_num = args.simulation_num
    game = GomokuGame(config)
    nnet = AlphaZeroNNet(game, config)

    logger.info("MCTS simulation_num=%d", config.simulation_num)

    # Resolve checkpoint path relative to the submodule directory so it works
    # regardless of the process's current working directory.
    checkpoint_path = os.path.join(_SUBMODULE_DIR, config.save_checkpoint_path)
    logger.info("Resolved checkpoint path: %s", checkpoint_path)

    checkpoint_files = glob.glob(checkpoint_path + "*.pt")
    if not checkpoint_files:
        raise RuntimeError(
            f"No checkpoint files found matching '{checkpoint_path}*.pt'. "
            f"Cannot run AlphaZero without a trained model."
        )

    nnet.load_checkpoint(checkpoint_path)
    mcts = MCTS(nnet, game, config)

    logger.info("AlphaZero adapter ready, waiting for commands.")

    for line in sys.stdin:
        line = line.strip()
        if not line:
            continue

        try:
            request = json.loads(line)
        except json.JSONDecodeError:
            logger.error("Failed to parse JSON: %s", line)
            continue

        command = request.get("command", "")
        sgf_board = request.get("chessboard", "")

        logger.info("Received command=%s, board=%s", command, sgf_board)

        player = _command_to_player(command)
        action = _pick_ai_action(mcts, sgf_board, player)

        if action is None:
            logger.warning("No legal moves available.")
            continue

        row, col = divmod(action, config.columns)
        response = {"rowIndex": row, "columnIndex": col}

        logger.info("Responding: %s (action=%d)", response, action)

        sys.stdout.write(json.dumps(response) + "\n")
        sys.stdout.flush()


if __name__ == "__main__":
    main()
