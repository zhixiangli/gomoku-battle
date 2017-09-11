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
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.common.PlayerProperties;
import com.zhixiangli.gomoku.core.service.ChessboardService;

/**
 * 
 * From Dashboard to AI:
 * 
 * CLEAR
 * 
 * NEXT_WHITE
 * 
 * NEXT_BLACK
 * 
 * RESET CHESSBOARD_SIZE CHESSBOARD_SIZE
 * 
 * PLAY_BLACK ROW COLUMN
 * 
 * PLAY_WHITE ROW COLUMN
 * 
 * From AI to Dashboard:
 * 
 * PUT ROW COLUMN
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
        if (StringUtils.isNotBlank(PlayerProperties.playerBlackCommand)) {
            blackPlayerProcess = new ConsoleProcess(PlayerProperties.playerBlackCommand);
        }
        if (StringUtils.isNotBlank(PlayerProperties.playerWhiteCommand)) {
            whitePlayerProcess = new ConsoleProcess(PlayerProperties.playerWhiteCommand);
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
        process.send(this.resetChessboard());
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

    private String resetChessboard() {
        return ConsoleCommand.format(ConsoleCommand.RESET,
                new Point(GomokuConst.CHESSBOARD_SIZE, GomokuConst.CHESSBOARD_SIZE))
                + chessboardService.getChessboard().toString();
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
