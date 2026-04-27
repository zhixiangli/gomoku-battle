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

import argparse
import glob
import importlib
import json
import logging
import os
import random
import sys
from dataclasses import dataclass
from typing import Any, Optional

# Directory containing this adapter script.
_ADAPTER_DIR = os.path.dirname(os.path.abspath(__file__))

# Root of the alphazero-board-games submodule (sibling of this adapter dir).
_SUBMODULE_DIR = os.path.join(os.path.dirname(_ADAPTER_DIR), "alphazero-board-games")

# Log file lives in the gomoku-battle/log directory; mode='w' clears previous logs each run.
_LOG_DIR = os.path.join(os.path.dirname(_ADAPTER_DIR), "log")
_LOG_FILE = os.path.join(_LOG_DIR, "alphazero.log")


@dataclass
class AdapterRuntime:
    """Initialized objects needed to handle requests."""

    mcts: Any
    columns: int


def _ensure_submodule_on_syspath():
    """Ensure alphazero-board-games is importable when running from repo root."""
    if _SUBMODULE_DIR not in sys.path:
        sys.path.insert(0, _SUBMODULE_DIR)


def _parse_args(argv: Optional[list[str]] = None):
    """Parse CLI arguments for adapter runtime settings."""
    parser = argparse.ArgumentParser(description="Run AlphaZero gomoku adapter")
    parser.add_argument(
        "--simulation-num",
        type=int,
        default=5000,
        help="Number of MCTS simulations per move (default: 5000)",
    )
    return parser.parse_args(argv)


def _pick_ai_action(mcts, board, player):
    """Pick the best action using MCTS (same logic as alphazero.stdio_play)."""
    actions, counts = mcts.simulate(board, player)
    if len(actions) == 0:
        return None

    actions_list = list(actions)
    counts_list = list(counts)
    best_count = max(counts_list)
    best_actions = [action for action, count in zip(actions_list, counts_list) if count == best_count]
    return int(random.choice(best_actions))


def _command_to_player(command: str) -> str:
    """Map ConsoleCommand to AlphaZero player string."""
    if command == "NEXT_BLACK":
        return "B"
    if command == "NEXT_WHITE":
        return "W"
    raise ValueError(f"Unknown command: {command}")


def _build_runtime(simulation_num: int, logger: logging.Logger) -> AdapterRuntime:
    """Initialize AlphaZero game objects and verify checkpoints are present."""
    _ensure_submodule_on_syspath()
    mcts_module = importlib.import_module("alphazero.mcts")
    nnet_module = importlib.import_module("alphazero.nnet")
    config_module = importlib.import_module("gomoku_15_15.config")
    game_module = importlib.import_module("gomoku_15_15.game")

    config = config_module.GomokuConfig()
    config.simulation_num = simulation_num
    game = game_module.GomokuGame(config)
    nnet = nnet_module.AlphaZeroNNet(game, config)

    logger.info("MCTS simulation_num=%d", config.simulation_num)

    # Resolve checkpoint path relative to the submodule directory so it works
    # regardless of the process's current working directory.
    checkpoint_path = os.path.join(_SUBMODULE_DIR, config.save_checkpoint_path)
    logger.info("Resolved checkpoint path: %s", checkpoint_path)

    checkpoint_files = glob.glob(checkpoint_path + "*.pt")
    if not checkpoint_files:
        raise RuntimeError(
            f"No checkpoint files found matching '{checkpoint_path}*.pt'. "
            "Cannot run AlphaZero without a trained model."
        )

    nnet.load_checkpoint(checkpoint_path)
    mcts = mcts_module.MCTS(nnet, game, config)

    return AdapterRuntime(mcts=mcts, columns=config.columns)


def _process_request(runtime: AdapterRuntime, request: dict[str, Any]) -> Optional[dict[str, int]]:
    """Process one JSON request and return a response JSON object or None."""
    command = request.get("command", "")
    sgf_board = request.get("chessboard", "")
    player = _command_to_player(command)
    action = _pick_ai_action(runtime.mcts, sgf_board, player)

    if action is None:
        return None

    row, col = divmod(action, runtime.columns)
    return {"rowIndex": row, "columnIndex": col}


def main(argv: Optional[list[str]] = None, stdin=None, stdout=None):
    args = _parse_args(argv)
    stdin = stdin or sys.stdin
    stdout = stdout or sys.stdout

    # Log to a file (cleared each run) so stdout stays clean for JSON protocol.
    os.makedirs(_LOG_DIR, exist_ok=True)
    logger = logging.getLogger(__name__)
    logger.handlers = []
    logger.setLevel(logging.INFO)
    handler = logging.FileHandler(_LOG_FILE, mode="w")
    handler.setFormatter(logging.Formatter("%(asctime)s %(levelname)s %(name)s: %(message)s"))
    logger.addHandler(handler)
    logger.propagate = False

    try:
        logger.info("Initializing AlphaZero adapter...")
        runtime = _build_runtime(args.simulation_num, logger)
        logger.info("AlphaZero adapter ready, waiting for commands.")

        for line in stdin:
            line = line.strip()
            if not line:
                continue

            try:
                request = json.loads(line)
            except json.JSONDecodeError:
                logger.error("Failed to parse JSON: %s", line)
                continue

            logger.info("Received command=%s, board=%s", request.get("command", ""), request.get("chessboard", ""))

            try:
                response = _process_request(runtime, request)
            except ValueError as exc:
                logger.error("Invalid request: %s", exc)
                continue

            if response is None:
                logger.warning("No legal moves available.")
                continue

            logger.info("Responding: %s", response)
            stdout.write(json.dumps(response) + "\n")
            stdout.flush()
    finally:
        handler.close()
        logger.handlers = []


if __name__ == "__main__":
    main()
