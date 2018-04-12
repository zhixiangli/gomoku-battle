/**
 * 
 */
package com.zhixiangli.gomoku.console;

import java.awt.Point;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.zhixiangli.gomoku.console.common.ConsoleCommand;
import com.zhixiangli.gomoku.console.common.ConsoleProcess;
import com.zhixiangli.gomoku.console.common.ConsoleRequest;
import com.zhixiangli.gomoku.console.common.ConsoleResponse;
import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.common.GomokuFormatter;
import com.zhixiangli.gomoku.core.service.ChessboardService;

/**
 * 
 * @author zhixiangli
 *
 */
public class ConsoleMaster {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleMaster.class);

    private ChessboardService chessboardService = ChessboardService.getInstance();

    private ConsoleProcess blackPlayerProcess;

    private ConsoleProcess whitePlayerProcess;

    public ConsoleMaster(String playProperties) throws FileNotFoundException, IOException {
        PlayerProperties.parse(playProperties);
        if (StringUtils.isNotBlank(PlayerProperties.playerBlackCommand)) {
            LOGGER.info("fork black player process: {}", PlayerProperties.playerBlackCommand);
            blackPlayerProcess = new ConsoleProcess(PlayerProperties.playerBlackCommand);
        }
        if (StringUtils.isNotBlank(PlayerProperties.playerWhiteCommand)) {
            LOGGER.info("fork white player process: {}", PlayerProperties.playerWhiteCommand);
            whitePlayerProcess = new ConsoleProcess(PlayerProperties.playerWhiteCommand);
        }
        // when chess type changed, notify the process to make a next move.
        chessboardService.addCurrentChessTypeChangeListener(
                (observable, oldValue, newValue) -> new Thread(() -> callForAction(newValue)).start());
    }

    private void callForAction(ChessType chessType) {
        try {
            switch (chessType) {
            case BLACK:
                sendActionCommand(blackPlayerProcess, ConsoleCommand.NEXT_BLACK);
                break;
            case WHITE:
                sendActionCommand(whitePlayerProcess, ConsoleCommand.NEXT_WHITE);
                break;
            case EMPTY:
            default:
            }
        } catch (IOException e) {
            LOGGER.error("call for action error. {}", e);
        }
    }

    private void sendActionCommand(ConsoleProcess process, ConsoleCommand next) throws IOException {
        if (null == process) {
            return;
        }
        ConsoleRequest req = new ConsoleRequest(next, GomokuConst.CHESSBOARD_SIZE, GomokuConst.CHESSBOARD_SIZE,
                GomokuFormatter.toSGF(this.chessboardService.getHistory()));
        process.send(new Gson().toJson(req) + StringUtils.LF);

        String received = process.receive();
        ConsoleResponse resp = new Gson().fromJson(received, ConsoleResponse.class);
        chessboardService.takeMove(new Point(resp.getRowIndex(), resp.getColumnIndex()));
    }

}
