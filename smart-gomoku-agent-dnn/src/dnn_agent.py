#!/usr/bin/python3
# -*- coding: utf-8 -*-

import dnn
from abstract_agent import AbstractAgent
from toolkit import Toolkit, ChessChar


class DNNAgent(AbstractAgent):
    def __init__(self):
        self.__board = None
        self.__dnn = dnn.DeepNeuralNetwork()

    def __del__(self):
        pass

    def next_black(self):
        proba, x, y = self.__next(self.__board, ChessChar.get_char(ChessChar.BLACK))
        return x, y

    def next_white(self):
        proba, x, y = self.__next(self.__board, ChessChar.get_char(ChessChar.WHITE))
        return x, y

    def __next(self, board_str, chess_char):
        best_proba = -1
        index = -1
        for i in range(len(board_str)):
            if board_str[i] != ChessChar.get_char(ChessChar.EMPTY):
                continue
            new_board = board_str[:i] + chess_char + board_str[i + 1:]

            proba = self.__dnn.predict([Toolkit.parse_board(new_board)],
                                       [ChessChar.get_value(ChessChar.get_enum(chess_char))])
            if proba > best_proba:
                best_proba, index = proba, i
        return best_proba, index / self._row, index % self._row

    def reset(self, board):
        self.__board = board

    def play_black(self, x, y):
        pass

    def play_white(self, x, y):
        pass

    def clear(self):
        pass


if __name__ == "__main__":
    agent = DNNAgent()
    agent.start()
