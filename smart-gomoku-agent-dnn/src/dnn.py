#!/usr/bin/python3
#  -*- coding: utf-8 -*-

import os

import numpy
import toolkit
from keras.callbacks import ModelCheckpoint
from keras.layers import Conv2D, MaxPooling2D, Input, Dense, Flatten, ZeroPadding2D
from keras.layers.merge import concatenate
from keras.models import Model

# for reproducibility
numpy.random.seed(1337)


class DeepNeuralNetwork:
    def __init__(self):
        self.__weights_filepath = toolkit.get_config("dnn", "weights_filepath")
        self.__build_model()
        self.__load_weights()

    def __del__(self):
        pass

    def train(self, board_train_array, artificial_train_array, proba_train_array):
        board_train = toolkit.numpy_board(board_train_array)
        artificial_train = toolkit.numpy_artificial(artificial_train_array)
        proba_train = toolkit.numpy_proba(proba_train_array)

        checkpointer = ModelCheckpoint(filepath=self.__weights_filepath, verbose=1, save_best_only=True)
        self.__model.fit([board_train, artificial_train], proba_train, epochs=1000, batch_size=32, verbose=1,
                         shuffle=True, callbacks=[checkpointer], validation_split=0.2)

    def predict(self, board_test_array, artificial_test_array):
        board_test = toolkit.numpy_board(board_test_array)
        artificial_test = toolkit.numpy_artificial(artificial_test_array)
        proba_test = self.__model.predict([board_test, artificial_test])
        return proba_test

    def __load_weights(self):
        if os.path.exists(self.__weights_filepath):
            self.__model.load_weights(self.__weights_filepath)

    def __build_model(self):
        board_input = Input(shape=(toolkit.chess_row, toolkit.chess_column, 1))
        board_output = ZeroPadding2D((1, 1))(board_input)
        board_output = Conv2D(32, (3, 3), activation="tanh")(board_output)
        board_output = ZeroPadding2D((1, 1))(board_output)
        board_output = Conv2D(32, (3, 3), activation="tanh")(board_output)
        board_output = MaxPooling2D(pool_size=(2, 2), padding='same')(board_output)

        board_output = ZeroPadding2D((1, 1))(board_output)
        board_output = Conv2D(64, (3, 3), activation="tanh")(board_output)
        board_output = ZeroPadding2D((1, 1))(board_output)
        board_output = Conv2D(64, (3, 3), activation="tanh")(board_output)
        board_output = MaxPooling2D(pool_size=(2, 2), padding='same')(board_output)

        board_output = Flatten()(board_output)
        board_model = Model(board_input, board_output)

        artificial_input = Input(shape=(1,))
        artificial_output = Dense(1, activation="tanh")(artificial_input)
        artificial_model = Model(artificial_input, artificial_output)

        concatenated_output = concatenate([board_model(board_input), artificial_model(artificial_input)])
        concatenated_output = Dense(256, activation="tanh")(concatenated_output)
        concatenated_output = Dense(256, activation="tanh")(concatenated_output)
        concatenated_output = Dense(1, activation="sigmoid")(concatenated_output)
        concatenated_model = Model([board_input, artificial_input], concatenated_output)
        concatenated_model.compile(loss="mean_squared_error", optimizer="adadelta")
        self.__model = concatenated_model


if __name__ == "__main__":
    dnn = DeepNeuralNetwork()
    board_train_list, artificial_train_list, proba_train_list = [], [], []
    for line in open(toolkit.get_config("dnn", "samples_filepath")):
        board, chess_type, proba = toolkit.numpy_sample(line)
        board_train_list.append(board)
        artificial_train_list.append(chess_type)
        proba_train_list.append(proba)
    board_train_list_augmentation, artificial_train_list_augmentation, proba_train_list_augmentation = \
        toolkit.data_augmentation(board_train_list, artificial_train_list, proba_train_list)
    dnn.train(board_train_list_augmentation, artificial_train_list_augmentation, proba_train_list_augmentation)
