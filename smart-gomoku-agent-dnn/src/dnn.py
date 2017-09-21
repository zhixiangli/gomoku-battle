#!/usr/bin/python3
#  -*- coding: utf-8 -*-

import os

import numpy
from keras.callbacks import ModelCheckpoint
from keras.layers import Conv2D, MaxPooling2D, Input, Dense, Flatten
from keras.layers.merge import concatenate
from keras.models import Model
from toolkit import Toolkit

# for reproducibility
numpy.random.seed(1337)


class DeepNeuralNetwork:
    def __init__(self):
        self.__row = int(Toolkit.get_config("chess", "row"))
        self.__column = int(Toolkit.get_config("chess", "column"))
        self.__weights_filepath = Toolkit.get_config("dnn", "weights_filepath")
        self.__build_model()
        self.__load_weights()

    def __del__(self):
        pass

    def train(self, board_train_array, artificial_train_array, proba_train_array):
        board_train = Toolkit.format_board(board_train_array, self.__row, self.__column)
        artificial_tain = Toolkit.format_artificial(artificial_train_array)
        proba_train = Toolkit.format_proba(proba_train_array)

        checkpointer = ModelCheckpoint(filepath=self.__weights_filepath, verbose=1, save_best_only=True)
        self.model.fit([board_train, artificial_tain], proba_train, epochs=10000, batch_size=32, verbose=1,
                       shuffle=True, callbacks=[checkpointer], validation_split=0.3)

    def predict(self, board_test_array, artificial_test_array):
        board_test = Toolkit.format_board(board_test_array, self.__row, self.__column)
        artificial_test = Toolkit.format_artificial(artificial_test_array)
        proba_test = self.__model.predict([board_test, artificial_test])
        return proba_test

    def __load_weights(self):
        if os.path.exists(self.__weights_filepath):
            self.__model.load_weights(self.__weights_filepath)

    def __build_model(self):
        board_input = Input(shape=(self.__row, self.__column, 1))
        board_out = Conv2D(32, (3, 3), activation="tanh")(board_input)
        board_out = Conv2D(32, (3, 3), activation="tanh")(board_out)
        board_out = MaxPooling2D(pool_size=(2, 2), padding='same')(board_out)
        board_out = Flatten()(board_out)
        board_model = Model(board_input, board_out)

        artificial_input = Input(shape=(1,))
        artificial_out = Dense(1, activation="tanh", kernel_initializer="normal")(artificial_input)
        artificial_model = Model(artificial_input, artificial_out)

        concatenated_out = concatenate([board_model(board_input), artificial_model(artificial_input)])
        concatenated_out = Dense(128, activation="tanh", kernel_initializer="normal")(concatenated_out)
        concatenated_out = Dense(128, activation="tanh", kernel_initializer="normal")(concatenated_out)
        concatenated_out = Dense(1, activation="tanh", kernel_initializer="normal")(concatenated_out)
        concatenated_model = Model([board_input, artificial_input], concatenated_out)
        concatenated_model.compile(loss="mean_squared_error", optimizer="adam")
        self.__model = concatenated_model


if __name__ == "__main__":
    dnn = DeepNeuralNetwork()
    board_train_array, artificial_tain_array, proba_train_array = [], [], []
    for line in open(Toolkit.get_config("dnn", "samples_filepath")):
        board, chess_type, proba = Toolkit.parse_sample(line)
        board_train_array.append(board)
        artificial_tain_array.append(chess_type)
        proba_train_array.append(proba)

    dnn.train(board_train_array, artificial_tain_array, proba_train_array)
