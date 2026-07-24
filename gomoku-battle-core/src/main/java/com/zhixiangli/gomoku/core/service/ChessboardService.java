/**
 *
 */
package com.zhixiangli.gomoku.core.service;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.chessboard.ChessState;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * gomoku backend service.
 *
 * @author lizhixiang
 */
public class ChessboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChessboardService.class);

    private static final ChessboardService CHESSBOARD_SERVICE = new ChessboardService();

    public static ChessboardService getInstance() {
        return CHESSBOARD_SERVICE;
    }

    /**
     * chessboard property, if changed the UI will changed as well.
     */
    private final SimpleObjectProperty<ChessType>[][] chessboardProperty;

    /**
     * current play's chess type, white or black when games on, otherwise empty.
     */
    private final SimpleObjectProperty<ChessType> currentChessType;

    private final SimpleObjectProperty<Point> lastMovePoint;

    private final List<Pair<ChessType, Point>> history;

    private long gameId;

    /**
     * chessboard state property, game on, draw, black win, white win.
     */
    private final SimpleObjectProperty<ChessState> chessStateProperty;

    @SuppressWarnings("unchecked")
    private ChessboardService() {
        chessboardProperty = new SimpleObjectProperty[GomokuConst.CHESSBOARD_SIZE][GomokuConst.CHESSBOARD_SIZE];
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                chessboardProperty[i][j] = new SimpleObjectProperty<>(ChessType.EMPTY);
            }
        }
        currentChessType = new SimpleObjectProperty<>(ChessType.EMPTY);
        chessStateProperty = new SimpleObjectProperty<>(ChessState.GAME_READY);
        lastMovePoint = new SimpleObjectProperty<>();
        history = new ArrayList<>();
    }

    public synchronized void restart() {
        LOGGER.info("start a new game.");
        gameId++;
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                chessboardProperty[i][j].set(ChessType.EMPTY);
            }
        }
        history.clear();
        lastMovePoint.set(null);
        chessStateProperty.set(ChessState.GAME_ON);
        // change current chess type to fire action.
        currentChessType.set(ChessType.EMPTY);
        currentChessType.set(ChessType.BLACK);
    }

    /**
     *
     * make a move.
     *
     * @param point position to occupy.
     */
    public synchronized void takeMove(final Point point) {
        LOGGER.info("start moving: {} {}", point, currentChessType);
        Preconditions.checkArgument(GameReferee.isInChessboard(point), "the position is out of range.");
        Preconditions.checkArgument(chessStateProperty.get() == ChessState.GAME_ON, "the chess game isn't on.");
        Preconditions.checkArgument(getChessboard(point) == ChessType.EMPTY, "the position is not empty.");

        takeMoveInternal(point);
    }

    /**
     * Applies an agent move only when it belongs to the active game and player.
     *
     * @return true when the move was applied; false when it was stale or invalid.
     */
    public synchronized boolean takeMoveIfCurrent(final long expectedGameId, final ChessType expectedChessType,
                                                  final Point point) {
        if ((gameId != expectedGameId) || (currentChessType.get() != expectedChessType)
                || (chessStateProperty.get() != ChessState.GAME_ON) || !GameReferee.isInChessboard(point)
                || (getChessboard(point) != ChessType.EMPTY)) {
            return false;
        }

        takeMoveInternal(point);
        return true;
    }

    /**
     * Marks a game as failed only when the reported failure belongs to its active generation.
     */
    public synchronized boolean failGameIfCurrent(final long expectedGameId, final ChessType expectedChessType) {
        if ((gameId != expectedGameId) || (currentChessType.get() != expectedChessType)
                || (chessStateProperty.get() != ChessState.GAME_ON)) {
            return false;
        }
        currentChessType.set(ChessType.EMPTY);
        chessStateProperty.set(ChessState.AGENT_FAILURE);
        return true;
    }

    public synchronized GameSnapshot snapshot() {
        return new GameSnapshot(gameId, currentChessType.get(), chessStateProperty.get(), history);
    }

    private void takeMoveInternal(final Point point) {

        // make move.
        chessboardProperty[point.x][point.y].set(currentChessType.get());
        lastMovePoint.set(new Point(point));
        history.add(Pair.of(currentChessType.get(), new Point(point)));

        final Chessboard chessboard = getChessboard();
        if (GameReferee.isWin(chessboard, point)) { // if win.
            final ChessState winner = (ChessType.BLACK == currentChessType.get()) ? ChessState.BLACK_WIN : ChessState.WHITE_WIN;
            LOGGER.info("game over, winner: {}", winner);
            currentChessType.set(ChessType.EMPTY);
            chessStateProperty.set(winner);
        } else if (GameReferee.isDraw(chessboard, point)) { // if draw.
            currentChessType.set(ChessType.EMPTY);
            chessStateProperty.set(ChessState.GAME_DRAW);
            LOGGER.info("game over, draw");
        } else {
            // finish this move, and change the current chess type and current
            // player.
            currentChessType.set(GameReferee.nextChessType(currentChessType.get()));
        }
        LOGGER.info("finish moving: {}", point);
    }

    public void addChessStateChangeListener(final ChangeListener<ChessState> listener) {
        chessStateProperty.addListener(listener);
    }

    public void removeChessStateChangeListener(final ChangeListener<ChessState> listener) {
        chessStateProperty.removeListener(listener);
    }

    public void addChessboardChangeListener(final Point point, final ChangeListener<ChessType> listener) {
        chessboardProperty[point.x][point.y].addListener(listener);
    }

    public void addCurrentChessTypeChangeListener(final ChangeListener<ChessType> listener) {
        currentChessType.addListener(listener);
    }

    public void removeCurrentChessTypeChangeListener(final ChangeListener<ChessType> listener) {
        currentChessType.removeListener(listener);
    }

    public void addLastMovePointChangeListener(final ChangeListener<Point> listener) {
        lastMovePoint.addListener(listener);
    }

    public synchronized ChessType getChessboard(final Point point) {
        return chessboardProperty[point.x][point.y].get();
    }

    public synchronized Chessboard getChessboard() {
        final Chessboard chessboard = new Chessboard();
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                chessboard.setChess(i, j, chessboardProperty[i][j].get());
            }
        }
        return chessboard;
    }

    public synchronized Point getLastMovePoint() {
        final Point point = lastMovePoint.get();
        return point == null ? null : new Point(point);
    }

    public synchronized ChessType getCurrentChessType() {
        return currentChessType.get();
    }

    public synchronized ChessState getChessState() {
        return chessStateProperty.get();
    }

    /**
     * @return the history
     */
    public synchronized List<Pair<ChessType, Point>> getHistory() {
        return copyHistory(history);
    }

    private static List<Pair<ChessType, Point>> copyHistory(final List<Pair<ChessType, Point>> source) {
        return List.copyOf(source.stream()
                .map(move -> Pair.of(move.getLeft(), new Point(move.getRight())))
                .toList());
    }

    public record GameSnapshot(long gameId, ChessType currentChessType, ChessState chessState,
                               List<Pair<ChessType, Point>> history) {

        public GameSnapshot {
            history = copyHistory(history);
        }
    }

}
