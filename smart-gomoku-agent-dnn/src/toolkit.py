#!/usr/bin/python3
# -*- coding: utf-8 -*-

import configparser
from enum import Enum, unique

import numpy


def get_config(section, key):
    config = configparser.ConfigParser()
    config.read("../conf/dnn.conf")
    return config.get(section, key)


directions = [[1, 1], [1, -1], [0, 1], [1, 0]]
consecutive_num = int(get_config('chess', 'consecutive_num'))
chess_row = int(get_config('chess', 'row'))
chess_column = int(get_config('chess', 'column'))


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
    def get_enum_by_char(chess_char):
        for chess_enum in ChessChar:
            if ChessChar.get_char(chess_enum) == chess_char:
                return chess_enum
        return None


def numpy_sample(sample):
    board_str, chess_ch, proba = sample.strip().split('\t')
    board = restore_board(board_str)
    return board, [ChessChar.get_value(ChessChar.get_enum_by_char(chess_ch))], [float(proba)]


def restore_board(board_str):
    board_list = list(map(lambda ch: ChessChar.get_value(ChessChar.get_enum_by_char(ch)), list(board_str)))
    return [board_list[i:i + chess_column] for i in range(0, chess_row * chess_column, chess_column)]


def numpy_board(board_array):
    board = numpy.array(board_array, dtype="float")
    board = board.reshape(board.shape[0], chess_row, chess_column, 1)
    return board / numpy.amax(board)


def numpy_artificial(artificial_array):
    artificial = numpy.array(artificial_array, dtype="float")
    return artificial / numpy.amax(artificial)


def numpy_proba(proba_array):
    return numpy.array(proba_array, dtype="float")


def is_win(chessboard, x, y):
    for dx, dy in directions:
        if is_win_in_direction(chessboard, x, y, dx, dy):
            return True
    return False


def is_win_in_direction(chessboard, x, y, dx, dy):
    i, j, num = x, y, 0
    while 0 <= i < len(chessboard) and 0 <= j < len(chessboard[x]) and chessboard[i][j] == chessboard[x][y]:
        num += 1
        i += dx
        j += dy

    i, j = x - dx, y - dy
    while 0 <= i < len(chessboard) and 0 <= j < len(chessboard[x]) and chessboard[i][j] == chessboard[x][y]:
        num += 1
        i -= dx
        j -= dy
    return num >= consecutive_num


def next_type(chess_enum):
    if chess_enum == ChessChar.BLACK:
        return ChessChar.WHITE
    elif chess_enum == ChessChar.WHITE:
        return ChessChar.BLACK
    return None


def data_augmentation(board_train_list, artificial_train_list, proba_train_list):
    def append(value, i):
        board_train_list_augmentation.append(value.tolist())
        artificial_train_list_augmentation.append(artificial_train_list[i])
        proba_train_list_augmentation.append(proba_train_list[i])

    board_train_list_augmentation, artificial_train_list_augmentation, proba_train_list_augmentation = [], [], []
    for i in range(len(board_train_list)):
        curr = numpy.array(board_train_list[i]).reshape(chess_row, chess_column)
        for j in range(4):
            curr = numpy.rot90(curr)
            append(curr, i)
            append(numpy.fliplr(curr), i)
            append(numpy.flipud(curr), i)
    return board_train_list_augmentation, artificial_train_list_augmentation, proba_train_list_augmentation


def parse_command(line):
    tokens = line.split()
    cmd = tokens[0]
    if cmd == Command.PLAY_WHITE.name or cmd == Command.PLAY_BLACK.name or cmd == Command.RESET.name \
            or cmd == Command.PUT.name:
        return Command[cmd], int(tokens[1]), int(tokens[2])
    elif cmd == Command.NEXT_WHITE.name or cmd == Command.NEXT_BLACK.name or cmd == Command.CLEAR.name:
        return Command[cmd], None, None
    return None, None, None


def format_command(cmd, x, y):
    return "%s %d %d\n" % (cmd, x, y) if x and y else "%s\n" % cmd
