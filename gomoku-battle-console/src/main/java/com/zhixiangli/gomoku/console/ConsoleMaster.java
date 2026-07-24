package com.zhixiangli.gomoku.console;

import com.google.gson.Gson;
import com.zhixiangli.gomoku.console.common.ConsoleCommand;
import com.zhixiangli.gomoku.console.common.ConsoleProcess;
import com.zhixiangli.gomoku.console.common.ConsoleProtocol;
import com.zhixiangli.gomoku.console.common.ConsoleRequest;
import com.zhixiangli.gomoku.console.common.ConsoleResponse;
import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.core.chessboard.ChessState;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.common.GomokuFormatter;
import com.zhixiangli.gomoku.core.service.ChessboardService;
import com.zhixiangli.gomoku.core.service.ChessboardService.GameSnapshot;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

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

    private final ExecutorService actionExecutor;

    private volatile boolean closed;

    private volatile long activeGameId = -1;

    public ConsoleMaster(final String playProperties) throws IOException {
        this(playProperties, ChessboardService.getInstance(), ConsoleProcess::new);
    }

    ConsoleMaster(final String playProperties, final ChessboardService chessboardService,
                  final ConsoleProcessFactory processFactory) throws IOException {
        PlayerProperties.parse(playProperties);
        this.chessboardService = chessboardService;
        this.processFactory = processFactory;
        actionExecutor = Executors.newSingleThreadExecutor(runnable ->
                new Thread(runnable, "gomoku-agent-actions"));
        // when chess type changed, notify the process to make a next move.
        currentChessTypeListener = (observable, oldValue, newValue) -> scheduleAction();
        chessboardService.addCurrentChessTypeChangeListener(currentChessTypeListener);
    }

    private void scheduleAction() {
        if (closed) {
            return;
        }
        final GameSnapshot snapshot = chessboardService.snapshot();
        final ChessType chessType = snapshot.currentChessType();
        if ((snapshot.chessState() != ChessState.GAME_ON)
                || ((chessType != ChessType.BLACK) && (chessType != ChessType.WHITE))) {
            return;
        }
        if (activeGameId != snapshot.gameId()) {
            activeGameId = snapshot.gameId();
            closeProcesses();
        }
        try {
            actionExecutor.execute(() -> callForAction(snapshot));
        } catch (final RejectedExecutionException e) {
            if (!closed) {
                throw e;
            }
        }
    }

    private void callForAction(final GameSnapshot snapshot) {
        final ChessType chessType = snapshot.currentChessType();
        try {
            switch (chessType) {
                case BLACK:
                    sendActionCommand(snapshot, ConsoleCommand.NEXT_BLACK);
                    break;
                case WHITE:
                    sendActionCommand(snapshot, ConsoleCommand.NEXT_WHITE);
                    break;
                case EMPTY:
                default:
            }
        } catch (final IOException | RuntimeException e) {
            if (!closed && chessboardService.failGameIfCurrent(snapshot.gameId(), chessType)) {
                LOGGER.error("Agent {} failed in game {}", chessType, snapshot.gameId(), e);
            } else {
                LOGGER.debug("Ignoring an agent failure from an inactive game: {}", e.getMessage());
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

    private void sendActionCommand(final GameSnapshot snapshot, final ConsoleCommand next) throws IOException {
        final ConsoleProcess process = getProcess(snapshot.currentChessType());
        if (null == process) {
            return;
        }
        final ConsoleRequest req = new ConsoleRequest(next, GomokuConst.CHESSBOARD_SIZE, GomokuConst.CHESSBOARD_SIZE,
                GomokuFormatter.toSGF(snapshot.history()));
        process.send(GSON.toJson(req) + StringUtils.LF);

        final String received = process.receive();
        final ConsoleResponse resp = ConsoleProtocol.parseResponse(received);
        final boolean applied = chessboardService.takeMoveIfCurrent(snapshot.gameId(), snapshot.currentChessType(),
                new Point(resp.getRowIndex(), resp.getColumnIndex()));
        if (!applied && chessboardService.failGameIfCurrent(snapshot.gameId(), snapshot.currentChessType())) {
            LOGGER.error("Agent {} returned an illegal move in game {}", snapshot.currentChessType(), snapshot.gameId());
        }
    }

    @Override
    public void close() {
        synchronized (processLock) {
            if (closed) {
                return;
            }
            closed = true;
            chessboardService.removeCurrentChessTypeChangeListener(currentChessTypeListener);
        }
        actionExecutor.shutdownNow();
        closeProcesses();
        try {
            if (!actionExecutor.awaitTermination(2, TimeUnit.SECONDS)) {
                LOGGER.warn("Agent action executor did not terminate promptly");
            }
        } catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void closeProcesses() {
        final List<ConsoleProcess> processes;
        synchronized (processLock) {
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
