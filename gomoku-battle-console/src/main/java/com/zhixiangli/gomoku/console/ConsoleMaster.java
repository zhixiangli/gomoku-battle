package com.zhixiangli.gomoku.console;

import com.google.gson.Gson;
import com.zhixiangli.gomoku.console.common.ConsoleCommand;
import com.zhixiangli.gomoku.console.common.ConsoleProcess;
import com.zhixiangli.gomoku.console.common.ConsoleProtocol;
import com.zhixiangli.gomoku.console.common.ConsoleRequest;
import com.zhixiangli.gomoku.console.common.ConsoleResponse;
import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.common.GomokuFormatter;
import com.zhixiangli.gomoku.core.service.ChessboardService;
import javafx.beans.value.ChangeListener;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhixiangli
 */
public class ConsoleMaster implements AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleMaster.class);

    private static final Gson GSON = new Gson();

    private final ChessboardService chessboardService;

    private final Map<String, ConsoleProcess> commandProcessMap = new HashMap<>();

    private final Object processLock = new Object();

    private final ConsoleProcessFactory processFactory;

    private final ChangeListener<ChessType> currentChessTypeListener;

    private volatile boolean closed;

    public ConsoleMaster(final String playProperties) throws IOException {
        this(playProperties, ChessboardService.getInstance(), ConsoleProcess::new);
    }

    ConsoleMaster(final String playProperties, final ChessboardService chessboardService,
                  final ConsoleProcessFactory processFactory) throws IOException {
        PlayerProperties.parse(playProperties);
        this.chessboardService = chessboardService;
        this.processFactory = processFactory;
        // when chess type changed, notify the process to make a next move.
        currentChessTypeListener = (observable, oldValue, newValue) -> {
            if (!closed) {
                final Thread actionThread = new Thread(() -> callForAction(newValue), "gomoku-agent-action");
                actionThread.setDaemon(true);
                actionThread.start();
            }
        };
        chessboardService.addCurrentChessTypeChangeListener(currentChessTypeListener);
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
            if (!closed) {
                LOGGER.error("call for action error.", e);
            }
        }
    }

    private ConsoleProcess getProcess(final ChessType chessType) throws IOException {
        final String command = PlayerProperties.getPlayerCommand(chessType);
        if (StringUtils.isBlank(command)) {
            return null;
        }
        synchronized (processLock) {
            if (closed) {
                throw new IOException("Console master is closed");
            }
            final ConsoleProcess existingProcess = commandProcessMap.get(command);
            if (existingProcess != null) {
                return existingProcess;
            }
            LOGGER.info("fork player process: {}", command);
            final ConsoleProcess process = processFactory.create(command);
            commandProcessMap.put(command, process);
            return process;
        }
    }

    private void sendActionCommand(final ConsoleProcess process, final ConsoleCommand next) throws IOException {
        if (null == process) {
            return;
        }
        final ConsoleRequest req = new ConsoleRequest(next, GomokuConst.CHESSBOARD_SIZE, GomokuConst.CHESSBOARD_SIZE,
                GomokuFormatter.toSGF(chessboardService.getHistory()));
        process.send(GSON.toJson(req) + StringUtils.LF);

        final String received = process.receive();
        final ConsoleResponse resp = ConsoleProtocol.parseResponse(received);
        chessboardService.takeMove(new Point(resp.getRowIndex(), resp.getColumnIndex()));
    }

    @Override
    public void close() {
        final List<ConsoleProcess> processes;
        synchronized (processLock) {
            if (closed) {
                return;
            }
            closed = true;
            chessboardService.removeCurrentChessTypeChangeListener(currentChessTypeListener);
            processes = new ArrayList<>(commandProcessMap.values());
            commandProcessMap.clear();
        }
        processes.forEach(ConsoleProcess::close);
    }

    @FunctionalInterface
    interface ConsoleProcessFactory {

        ConsoleProcess create(String command) throws IOException;

    }

}
