/**
 * 
 */
package com.zhixiangli.gomoku.core.console;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.ChessboardService;
import com.zhixiangli.gomoku.core.common.GomokuConst;

/**
 * 
 * From Dashboard to AI: CLEAR; NEXT_WHITE; NEXT_BLACK; SHOW CHESSBOARD_SIZE;
 * 
 * From AI to Dashboard: PUT ROW COLUMN;
 * 
 * @author zhixiangli
 *
 */
public class ConsoleMaster implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleMaster.class);

    private ChessboardService chessboardService = ChessboardService.getInstance();

    private ConsoleProcess blackPlayerProcess;

    private ConsoleProcess whitePlayerProcess;

    public ConsoleMaster() throws FileNotFoundException, IOException {
        if (StringUtils.isNotBlank(GomokuConst.Player.playerBlackCommand)) {
            blackPlayerProcess = new ConsoleProcess(GomokuConst.Player.playerBlackCommand);
        }
        if (StringUtils.isNotBlank(GomokuConst.Player.playerWhiteCommand)) {
            whitePlayerProcess = new ConsoleProcess(GomokuConst.Player.playerWhiteCommand);
        }

        chessboardService.addCurrentChessTypeChangeListener((observable, oldValue, newValue) -> {
            Thread t = new Thread(() -> this.callForAction(newValue));
            t.start();
        });
    }

    private void callForAction(ChessType chessType) {
        try {
            switch (chessType) {
            case BLACK:
                this.sendActionCommand(this.blackPlayerProcess, ConsoleCommand.PLAY_WHITE, ConsoleCommand.NEXT_BLACK);
                break;
            case WHITE:
                this.sendActionCommand(this.whitePlayerProcess, ConsoleCommand.PLAY_BLACK, ConsoleCommand.NEXT_WHITE);
                break;
            case EMPTY:
            default: // game is over and clean the chessboard.
                this.sendClearCommand(blackPlayerProcess);
                this.sendClearCommand(whitePlayerProcess);
            }
        } catch (IOException e) {
            LOGGER.error("call for action error. {}", e);
        }
    }

    private void sendActionCommand(ConsoleProcess process, ConsoleCommand play, ConsoleCommand next)
            throws IOException {
        if (null == process) {
            return;
        }
        Point lastMovePoint = chessboardService.getLastMovePoint();
        if (null != lastMovePoint) {
            process.send(ConsoleCommand.format(play, lastMovePoint));
        }
        process.send(this.showChessboard());
        process.send(ConsoleCommand.format(next));
        Pair<ConsoleCommand, Point> commandPair = ConsoleCommand.parse(process.receive());
        this.chessboardService.takeMove(commandPair.getValue());

    }

    private void sendClearCommand(ConsoleProcess process) throws IOException {
        if (null == process) {
            return;
        }
        process.send(ConsoleCommand.format(ConsoleCommand.CLEAR));
    }

    private String showChessboard() {
        StringBuilder sb = new StringBuilder(ConsoleCommand.format(ConsoleCommand.SHOW,
                new Point(GomokuConst.CHESSBOARD_SIZE, GomokuConst.CHESSBOARD_SIZE)));
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                ChessType chessType = this.chessboardService.getChessboard(new Point(i, j));
                if (chessType == ChessType.EMPTY) {
                    sb.append(GomokuConst.ChessChar.EMPTY);
                } else if (chessType == ChessType.BLACK) {
                    sb.append(GomokuConst.ChessChar.BLACK);
                } else if (chessType == ChessType.WHITE) {
                    sb.append(GomokuConst.ChessChar.WHITE);
                }
            }
            sb.append(StringUtils.LF);
        }
        return sb.toString();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
            }
            LOGGER.info("dashboard console thread is alive.");
        }
    }

}
