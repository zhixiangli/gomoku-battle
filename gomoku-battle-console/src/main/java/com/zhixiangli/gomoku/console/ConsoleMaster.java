package com.zhixiangli.gomoku.console;

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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhixiangli
 */
public class ConsoleMaster {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleMaster.class);

    private final ChessboardService chessboardService = ChessboardService.getInstance();

    private final Map<String, ConsoleProcess> commandProcessMap = new ConcurrentHashMap<>();

    public ConsoleMaster(final String playProperties) throws IOException {
        PlayerProperties.parse(playProperties);
        // when chess type changed, notify the process to make a next move.
        chessboardService.addCurrentChessTypeChangeListener(
                (observable, oldValue, newValue) -> new Thread(() -> callForAction(newValue)).start());
    }

    private void callForAction(final ChessType chessType) {
        try {
            switch (chessType) {
                case BLACK:
                    sendActionCommand(getProcess(chessType), ConsoleCommand.NEXT_BLACK);
                    break;
                case WHITE:
                    sendActionCommand(getProcess(chessType), ConsoleCommand.NEXT_WHITE);
                    break;
                case EMPTY:
                default:
            }
        } catch (final IOException e) {
            LOGGER.error("call for action error.", e);
        }
    }

    private ConsoleProcess getProcess(final ChessType chessType) throws IOException {
        final String command = PlayerProperties.getPlayerCommand(chessType);
        if (StringUtils.isBlank(command)) {
            return null;
        }
        try {
            return commandProcessMap.computeIfAbsent(command, key -> {
                try {
                    LOGGER.info("fork player process: {}", key);
                    return new ConsoleProcess(key);
                } catch (final IOException e) {
                    throw new IllegalStateException("fork player process error", e);
                }
            });
        } catch (final IllegalStateException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw e;
        }
    }

    private void sendActionCommand(final ConsoleProcess process, final ConsoleCommand next) throws IOException {
        if (null == process) {
            return;
        }
        final ConsoleRequest req = new ConsoleRequest(next, GomokuConst.CHESSBOARD_SIZE, GomokuConst.CHESSBOARD_SIZE,
                GomokuFormatter.toSGF(chessboardService.getHistory()));
        process.send(new Gson().toJson(req) + StringUtils.LF);

        final String received = process.receive();
        final ConsoleResponse resp = new Gson().fromJson(received, ConsoleResponse.class);
        chessboardService.takeMove(new Point(resp.getRowIndex(), resp.getColumnIndex()));
    }

}
