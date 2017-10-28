/**
 * 
 */
package com.zhixiangli.gomoku.console;

import java.awt.Point;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhixiangli.gomoku.console.common.ConsoleCommand;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;

/**
 * @author zhixiangli
 *
 */
public abstract class ConsoleAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleAgent.class);

    public void start() {
        try (Scanner reader = new Scanner(System.in)) {
            while (reader.hasNext()) {
                String command = StringUtils.strip(reader.nextLine());
                LOGGER.info("received command {}", command);

                Pair<ConsoleCommand, Point> commandPair = ConsoleCommand.parse(command);
                if (null == commandPair) {
                    LOGGER.error("unknown command: {}", command);
                    continue;
                }

                switch (commandPair.getKey()) {
                case CLEAR:
                    this.clear();
                    break;
                case NEXT_BLACK:
                    System.out.print(ConsoleCommand.format(ConsoleCommand.PUT, this.next(ChessType.BLACK)));
                    break;
                case NEXT_WHITE:
                    System.out.print(ConsoleCommand.format(ConsoleCommand.PUT, this.next(ChessType.WHITE)));
                    break;
                case PLAY_BLACK:
                    this.play(ChessType.BLACK, commandPair.getValue());
                    break;
                case PLAY_WHITE:
                    this.play(ChessType.WHITE, commandPair.getValue());
                    break;
                case RESET:
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
                        sb.append(reader.nextLine() + StringUtils.LF);
                    }
                    LOGGER.info("received chessboard:\n{}", sb);
                    this.reset(new Chessboard(sb.toString()));
                    break;
                default:
                    break;
                }
            }
        }
    }

    protected abstract void clear();

    protected abstract Point next(ChessType chessType);

    protected abstract void reset(Chessboard chessboard);

    protected abstract void play(ChessType chessType, Point point);

}
