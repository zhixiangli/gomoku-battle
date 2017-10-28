#!/usr/bin/python3
# -*- coding: utf-8 -*-

import dnn
import toolkit
from abstract_agent import AbstractAgent
from toolkit import ChessChar


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
        best_proba, x, y = -1, -1, -1
        chessboard = [list(board_str[i:i + toolkit.chess_column]) for i in
                      range(0, toolkit.chess_column * toolkit.chess_row, toolkit.chess_row)]
        for i in range(toolkit.chess_row):
            for j in range(toolkit.chess_column):
                if chessboard[i][j] != ChessChar.get_char(ChessChar.EMPTY):
                    continue
                chessboard[i][j] = chess_char
                if toolkit.is_win(chessboard, i, j):
                    return 1, i, j
                proba = 1 - self.__dnn.predict([toolkit.restore_board("".join(sum(chessboard, [])))], [
                    ChessChar.get_value(toolkit.next_type(ChessChar.get_enum_by_char(chess_char)))])
                if proba > best_proba:
                    best_proba, x, y = proba, i, j
                chessboard[i][j] = ChessChar.get_char(ChessChar.EMPTY)
        return best_proba, x, y

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
