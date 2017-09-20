#!/usr/bin/python3
# -*- coding: utf-8 -*-

import configparser
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
    def get_enum(chess_char):
        for chess_enum in ChessChar:
            if ChessChar.get_char(chess_enum) == chess_char:
                return chess_enum
        return None


class Toolkit:
    @staticmethod
    def parse_sample(sample):
        board_str, chess_ch, proba = sample.strip().split('\t')
        board = Toolkit.parse_board(board_str)
        return board, [ChessChar.get_value(ChessChar.get_enum(chess_ch))], [float(proba)]

    @staticmethod
    def parse_board(board_str):
        return list(map(lambda ch: ChessChar.get_value(ChessChar.get_enum(ch)), list(board_str)))

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
    def format_command(cmd, x, y):
        return "%s %d %d\n" % (cmd, x, y) if x and y else "%s\n" % cmd

    @staticmethod
    def format_board(board_array, row, column):
        board = numpy.array(board_array, dtype="float")
        board = board.reshape(board.shape[0], row, column, 1)
        return board / numpy.amax(board)

    @staticmethod
    def format_artificial(artificial_array):
        artificial = numpy.array(artificial_array, dtype="float")
        return artificial / numpy.amax(artificial)

    @staticmethod
    def format_proba(proba_array):
        return numpy.array(proba_array, dtype="float")

    @staticmethod
    def get_config(section, key):
        config = configparser.ConfigParser()
        config.read("../conf/dnn.conf")
        return config.get(section, key)
