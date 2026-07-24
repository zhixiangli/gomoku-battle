import importlib.util
import logging
import tempfile
import unittest
from pathlib import Path
from types import SimpleNamespace


_ADAPTER_PATH = Path(__file__).resolve().parents[1] / "alphazero_adapter.py"
_SPEC = importlib.util.spec_from_file_location("alphazero_adapter_module", _ADAPTER_PATH)
alphazero_adapter = importlib.util.module_from_spec(_SPEC)
assert _SPEC.loader is not None
_SPEC.loader.exec_module(alphazero_adapter)


class AdapterIntegrationTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        alphazero_adapter._ensure_submodule_on_syspath()
        logger = logging.getLogger("alphazero-adapter-integration")
        logger.handlers = [logging.NullHandler()]
        cls.runtime = alphazero_adapter._build_runtime(simulation_num=32, logger=logger)

    def _assert_response_in_expected(self, request, expected_positions):
        request = {"rows": 15, "columns": 15, **request}
        response = alphazero_adapter._process_request(self.runtime, request)
        self.assertIsNotNone(response)
        actual = (response["rowIndex"], response["columnIndex"])
        self.assertIn(actual, expected_positions)

    def test_basic_game_patterns(self):
        patterns = [
            {
                "name": "opening_prefers_center",
                # empty board
                "request": {"command": "NEXT_BLACK", "chessboard": ""},
                "expected_positions": {(7, 7)},
            },
            {
                "name": "early_cross_shape_extension",
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # .......BW......
                # .......BW......
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                "request": {
                    "command": "NEXT_BLACK",
                    "chessboard": "B[77];W[78];B[87];W[88]",
                },
                "expected_positions": {(9, 8)},
            },
            {
                "name": "black_finishes_horizontal_five",
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ......WWW......
                # .......BBBB....
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                "request": {
                    "command": "NEXT_BLACK",
                    "chessboard": "B[77];W[66];B[78];W[67];B[79];W[68];B[7a]",
                },
                "expected_positions": {(7, 6), (7, 11)},
            },
            {
                "name": "black_finishes_diagonal_five",
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ......WWW......
                # .......B.......
                # ........B......
                # .........B.....
                # ..........B....
                # ...............
                # ...............
                # ...............
                # ...............
                "request": {
                    "command": "NEXT_BLACK",
                    "chessboard": "B[77];W[66];B[88];W[67];B[99];W[68];B[aa]",
                },
                "expected_positions": {(6, 6), (11, 11)},
            },
            {
                "name": "white_blocks_black_open_four",
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # BBBB...........
                # WWW............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                # ...............
                "request": {
                    "command": "NEXT_WHITE",
                    "chessboard": "B[70];W[80];B[71];W[81];B[72];W[82];B[73]",
                },
                "expected_positions": {(7, 4)},
            },
        ]

        for pattern in patterns:
            with self.subTest(pattern=pattern["name"]):
                self._assert_response_in_expected(
                    request=pattern["request"],
                    expected_positions=pattern["expected_positions"],
                )


class AdapterProtocolTests(unittest.TestCase):
    def test_process_request_converts_protocol_sgf_to_engine_format(self):
        class RecordingMcts:
            def __init__(self):
                self.calls = []

            def simulate(self, board, player):
                self.calls.append((board, player))
                return [42], [1]

        mcts = RecordingMcts()
        runtime = alphazero_adapter.AdapterRuntime(mcts=mcts, columns=15)

        response = alphazero_adapter._process_request(
            runtime,
            {
                "command": "NEXT_BLACK",
                "rows": 15,
                "columns": 15,
                "chessboard": "B[77];W[ae]",
            },
        )

        self.assertEqual([("B[7,7];W[a,e]", "B")], mcts.calls)
        self.assertEqual({"rowIndex": 2, "columnIndex": 12}, response)

    def test_request_requires_object_known_command_and_fixed_dimensions(self):
        for request in [
            [],
            {"command": "NEXT_GREEN", "rows": 15, "columns": 15, "chessboard": ""},
            {"command": "NEXT_BLACK", "rows": 14, "columns": 15, "chessboard": ""},
            {"command": "NEXT_BLACK", "rows": 15, "columns": 15, "chessboard": "B[7f]"},
        ]:
            with self.subTest(request=request):
                with self.assertRaises(ValueError):
                    alphazero_adapter._validate_request(request)


class AdapterPreflightTests(unittest.TestCase):
    def test_preflight_reports_an_uninitialized_submodule(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            original_submodule_dir = alphazero_adapter._SUBMODULE_DIR
            alphazero_adapter._SUBMODULE_DIR = temp_dir
            try:
                with self.assertRaisesRegex(RuntimeError, "submodule update --init"):
                    alphazero_adapter._preflight()
            finally:
                alphazero_adapter._SUBMODULE_DIR = original_submodule_dir

    def test_checkpoint_prefix_must_match_at_least_one_model(self):
        with tempfile.TemporaryDirectory() as temp_dir:
            original_submodule_dir = alphazero_adapter._SUBMODULE_DIR
            alphazero_adapter._SUBMODULE_DIR = temp_dir
            config = SimpleNamespace(save_checkpoint_path="checkpoints/model")
            try:
                with self.assertRaisesRegex(RuntimeError, "No checkpoint files found"):
                    alphazero_adapter._find_checkpoint_path(config)

                checkpoint = Path(temp_dir) / "checkpoints" / "model-42.pt"
                checkpoint.parent.mkdir(parents=True)
                checkpoint.touch()

                self.assertEqual(
                    str(Path(temp_dir) / "checkpoints" / "model"),
                    alphazero_adapter._find_checkpoint_path(config),
                )
            finally:
                alphazero_adapter._SUBMODULE_DIR = original_submodule_dir

if __name__ == "__main__":
    unittest.main()
