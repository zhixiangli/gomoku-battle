#!/usr/bin/python3
# -*- coding: utf-8 -*-

import sys

import toolkit
from toolkit import Command


class AbstractAgent:
    def __init__(self):
        pass

    def __del__(self):
        pass

    def start(self):
        while True:
            line = sys.stdin.readline().strip()
            if not line:
                break
            cmd, x, y = toolkit.parse_command(line)
            if cmd == Command.RESET:
                _, _, board_str = x, y, ""
                for i in range(x):
                    board_str += sys.stdin.readline().strip()
                self.reset(board_str)
            elif cmd == Command.PLAY_BLACK:
                self.play_black(x, y)
            elif cmd == Command.PLAY_WHITE:
                self.play_white(x, y)
            elif cmd == Command.NEXT_BLACK:
                x, y = self.next_black()
                print(toolkit.format_command(Command.PUT.name, x, y))
            elif cmd == Command.NEXT_WHITE:
                x, y = self.next_white()
                print(toolkit.format_command(Command.PUT.name, x, y))
            elif cmd == Command.CLEAR:
                self.clear()
