/**
 * 
 */
package com.zhixiangli.gomoku.core.console;

import java.awt.Point;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                case NEXT_WHITE:
                    StopWatch stopWatch = new StopWatch();
                    stopWatch.start();
                    Point point = this.next(
                            commandPair.getKey() == ConsoleCommand.NEXT_BLACK ? ChessType.BLACK : ChessType.WHITE);
                    stopWatch.stop();
                    LOGGER.info("finish next point computing, cost {}", stopWatch.getTime(TimeUnit.MILLISECONDS));
                    System.out.print(ConsoleCommand.format(ConsoleCommand.PUT, point));
                    break;
                case PLAY_BLACK:
                    this.play(ChessType.BLACK, commandPair.getValue());
                    break;
                case PLAY_WHITE:
                    this.play(ChessType.WHITE, commandPair.getValue());
                    break;
                case RESET:
                    Chessboard chessboard = new Chessboard();
                    for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
                        String row = reader.nextLine();
                        LOGGER.info("received command {}", row);
                        for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                            ChessType chessType = null;
                            switch (row.charAt(j)) {
                            case GomokuConst.CHESS_CHAR_BLACK:
                                chessType = ChessType.BLACK;
                                break;
                            case GomokuConst.CHESS_CHAR_WHITE:
                                chessType = ChessType.WHITE;
                                break;
                            case GomokuConst.CHESS_CHAR_EMPTY:
                                chessType = ChessType.EMPTY;
                                break;
                            default:
                                break;
                            }
                            chessboard.setChess(i, j, chessType);
                        }
                    }
                    this.show(chessboard);
                    break;
                default:
                    break;
                }
            }
        }
    }

    protected abstract void clear();

    protected abstract Point next(ChessType chessType);

    protected abstract void show(Chessboard chessboard);

    protected abstract void play(ChessType chessType, Point point);

}
