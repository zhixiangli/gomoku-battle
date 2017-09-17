#!/usr/bin/python3
#  -*- coding: utf-8 -*-

import sys

import numpy
from keras.layers import Conv2D, MaxPooling2D, Input, Dense, Flatten
from keras.layers.merge import concatenate
from keras.models import Model
from utilities import Utilities

# for reproducibility
numpy.random.seed(1337)


class DeepNeuralNetwork:
    def __init__(self, row=15, column=15):
        self.__row = row
        self.__column = column
        self.__build_model()
        self.__load_weights()

    def __del__(self):
        pass

    def train(self, board_train, artificial_tain, proba_train):
        board_train = Utilities.format_board(board_train, self.__row, self.__column)
        artificial_tain = Utilities.format_artificial(artificial_tain)
        proba_train = Utilities.format_proba(proba_train)
        self.model.fit([board_train, artificial_tain], proba_train, epochs=100, batch_size=32, verbose=1, shuffle=True)

    def predict(self, board_test, artificial_test):
        board_test = Utilities.format_board(board_test, self.__row, self.__column)
        artificial_test = Utilities.format_artificial(artificial_test)
        proba_test = self.model.predict([board_test, artificial_test])
        return proba_test

    def __load_weights(self):
        # self.model.load_weights()
        pass

    def __build_model(self):
        board_input = Input(shape=(self.__row, self.__column, 1))
        board_out = Conv2D(32, (6, 6), activation="relu")(board_input)
        board_out = Conv2D(64, (3, 3), activation="relu")(board_out)
        board_out = MaxPooling2D(pool_size=(2, 2))(board_out)
        board_out = Flatten()(board_out)
        board_model = Model(board_input, board_out)

        artificial_input = Input(shape=(1,))
        artificial_out = Dense(1, activation="relu", kernel_initializer="normal")(artificial_input)
        artificial_model = Model(artificial_input, artificial_out)

        concatenated_out = concatenate([board_model(board_input), artificial_model(artificial_input)])
        concatenated_out = Dense(64, activation="relu", kernel_initializer="normal")(concatenated_out)
        concatenated_out = Dense(1, activation="relu", kernel_initializer="normal")(concatenated_out)
        concatenated_model = Model([board_input, artificial_input], concatenated_out)
        concatenated_model.compile(loss="mean_squared_error", optimizer="adam")
        self.model = concatenated_model


if __name__ == "__main__":
    e = DeepNeuralNetwork()
    board_train, artificial_tain, proba_train = [], [], []
    for line in sys.stdin:
        board, chess_type, proba = Utilities.parse_sample(line)
        board_train.append(board)
        artificial_tain.append(chess_type)
        proba_train.append(proba)

    e.train(board_train, artificial_tain, proba_train)
