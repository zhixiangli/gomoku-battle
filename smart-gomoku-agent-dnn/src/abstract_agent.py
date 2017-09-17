#!/usr/bin/python2.6
# -*- coding: utf-8 -*-

import sys

from utilities import Command, Utilities


class AbstractAgent:
    def __init__(self):
        self._row = 15
        self._column = 15

    def __del__(self):
        pass

    def start(self):
        while True:
            line = sys.stdin.readline().strip()
            if not line:
                break
            cmd, x, y = Utilities.parse_command(line)
            if cmd == Command.RESET:
                self._row, self._column, board_str = x, y, ""
                for i in range(x):
                    board_str += sys.stdin.readline().strip()
                self.reset(board_str)
            elif cmd == Command.PLAY_BLACK:
                self.play_black(x, y)
            elif cmd == Command.PLAY_WHITE:
                self.play_white(x, y)
            elif cmd == Command.NEXT_BLACK:
                print(Utilities.format_command(Command.PUT.name, self.next_black()))
            elif cmd == Command.NEXT_WHITE:
                print(Utilities.format_command(Command.PUT.name, self.next_white()))
            elif cmd == Command.CLEAR:
                self.clear()
