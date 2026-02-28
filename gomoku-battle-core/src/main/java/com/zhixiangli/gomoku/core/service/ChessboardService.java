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

    public void restart() {
        LOGGER.info("start a new game.");
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                chessboardProperty[i][j].set(ChessType.EMPTY);
            }
        }
        chessStateProperty.set(ChessState.GAME_ON);
        // change current chess type to fire action.
        currentChessType.set(ChessType.EMPTY);
        currentChessType.set(ChessType.BLACK);
        lastMovePoint.set(null);
        history.clear();
    }

    /**
     *
     * make a move.
     *
     * @param row
     *            row index.
     * @param column
     *            column index.
     */
    public void takeMove(final Point point) {
        LOGGER.info("start moving: {} {}", point, currentChessType);
        Preconditions.checkArgument(GameReferee.isInChessboard(point), "the position is out of range.");
        Preconditions.checkArgument(chessStateProperty.get() == ChessState.GAME_ON, "the chess game isn't on.");
        Preconditions.checkArgument(getChessboard(point) == ChessType.EMPTY, "the position is not empty.");

        // make move.
        chessboardProperty[point.x][point.y].set(currentChessType.get());
        lastMovePoint.set(new Point(point));
        history.add(Pair.of(currentChessType.get(), new Point(point)));

        final Chessboard chessboard = getChessboard();
        if (GameReferee.isWin(chessboard, point)) { // if win.
            final ChessState winner = (ChessType.BLACK == currentChessType.get()) ? ChessState.BLACK_WIN : ChessState.WHITE_WIN;
            LOGGER.info("game over, winner: {}", winner);
            chessStateProperty.set(winner);
        } else if (GameReferee.isDraw(chessboard, point)) { // if draw.
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

    public void addChessboardChangeListener(final Point point, final ChangeListener<ChessType> listener) {
        chessboardProperty[point.x][point.y].addListener(listener);
    }

    public void addCurrentChessTypeChangeListener(final ChangeListener<ChessType> listener) {
        currentChessType.addListener(listener);
    }

    public void addLastMovePointChangeListener(final ChangeListener<Point> listener) {
        lastMovePoint.addListener(listener);
    }

    public ChessType getChessboard(final Point point) {
        return chessboardProperty[point.x][point.y].get();
    }

    public Chessboard getChessboard() {
        final Chessboard chessboard = new Chessboard();
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                chessboard.setChess(i, j, chessboardProperty[i][j].get());
            }
        }
        return chessboard;
    }

    public Point getLastMovePoint() {
        return lastMovePoint.get();
    }

    public ChessType getCurrentChessType() {
        return currentChessType.get();
    }

    /**
     * @return the history
     */
    public List<Pair<ChessType, Point>> getHistory() {
        return history;
    }

}
