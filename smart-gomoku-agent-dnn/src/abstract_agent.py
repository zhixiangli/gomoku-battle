#!/usr/bin/python3
# -*- coding: utf-8 -*-

import sys

from toolkit import Command, Toolkit


class AbstractAgent:
    def __init__(self):
        self._row = int(Toolkit.get_config("chess", "row"))
        self._column = int(Toolkit.get_config("chess", "column"))

    def __del__(self):
        pass

    def start(self):
        while True:
            line = sys.stdin.readline().strip()
            if not line:
                break
            cmd, x, y = Toolkit.parse_command(line)
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
                x, y = self.next_black()
                print(Toolkit.format_command(Command.PUT.name, x, y))
            elif cmd == Command.NEXT_WHITE:
                x, y = self.next_white()
                print(Toolkit.format_command(Command.PUT.name, x, y))
            elif cmd == Command.CLEAR:
                self.clear()
