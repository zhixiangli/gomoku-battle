#!/usr/bin/python2.6
# -*- coding: utf-8 -*-

from enum import Enum, unique

import numpy


@unique
class Command(Enum):
    CLEAR = 1
    NEXT_BLACK = 2
    NEXT_WHITE = 3
    RESET = 4
    PLAY_BLACK = 5
    PLAY_WHITE = 6
    PUT = 7


@unique
class ChessChar(Enum):
    BLACK = ('B', 1)
    WHITE = ('W', 2)
    EMPTY = ('.', 3)

    @staticmethod
    def get_char(chess_enum):
        return chess_enum.value[0]

    @staticmethod
    def get_value(chess_enum):
        return chess_enum.value[1]

    @staticmethod
    def get_index(chess_char):
        for chess_enum in ChessChar:
            if ChessChar.get_char(chess_enum) == chess_char:
                return ChessChar.get_value(chess_enum)
        return None


class Utilities:
    @staticmethod
    def parse_sample(sample):
        board_str, chess_type, proba = sample.strip().split('\t')
        board = Utilities.parse_board(board_str)
        return board, [int(chess_type)], [float(proba)]

    @staticmethod
    def parse_board(board_str):
        return list(map(lambda ch: ChessChar.get_index(ch), list(board_str)))

    @staticmethod
    def parse_command(line):
        tokens = line.split()
        cmd = tokens[0]
        if cmd == Command.PLAY_WHITE.name or cmd == Command.PLAY_BLACK.name or cmd == Command.RESET.name or cmd == Command.PUT.name:
            return Command[cmd], int(tokens[1]), int(tokens[2])
        elif cmd == Command.NEXT_WHITE.name or cmd == Command.NEXT_BLACK.name or cmd == Command.CLEAR.name:
            return Command[cmd], None, None
        return None, None, None

    @staticmethod
    def format_command(cmd, pos):
        return "%s %d %d\n" % (cmd, pos[0], pos[1]) if pos[0] and pos[1] else "%s\n" % cmd

    @staticmethod
    def format_board(board, row=15, column=15):
        board = numpy.array(board, dtype="float")
        board = board.reshape(board.shape[0], row, column, 1) / 3
        return board

    @staticmethod
    def format_artificial(artificial):
        return numpy.array(artificial, dtype="float") / 2

    @staticmethod
    def format_proba(proba):
        return numpy.array(proba, dtype="float")
