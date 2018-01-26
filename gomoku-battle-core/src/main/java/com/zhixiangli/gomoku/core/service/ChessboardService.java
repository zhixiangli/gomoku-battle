/**
 * 
 */
package com.zhixiangli.gomoku.core.service;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.chessboard.ChessState;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;

/**
 * gomoku backend service.
 * 
 * @author lizhixiang
 */
public class ChessboardService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChessboardService.class);

    private static final ChessboardService CHESSBOARD_SERVICE = new ChessboardService();

    public static final ChessboardService getInstance() {
        return CHESSBOARD_SERVICE;
    }

    /**
     * chessboard property, if changed the UI will changed as well.
     */
    private SimpleObjectProperty<ChessType>[][] chessboardProperty;

    /**
     * current play's chess type, white or black when games on, otherwise empty.
     */
    private SimpleObjectProperty<ChessType> currentChessType;

    private SimpleObjectProperty<Point> lastMovePoint;

    private List<Pair<ChessType, Point>> history;

    /**
     * chessboard state property, game on, draw, black win, white win.
     */
    private SimpleObjectProperty<ChessState> chessStateProperty;

    @SuppressWarnings("unchecked")
    private ChessboardService() {
        this.chessboardProperty = new SimpleObjectProperty[GomokuConst.CHESSBOARD_SIZE][GomokuConst.CHESSBOARD_SIZE];
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                this.chessboardProperty[i][j] = new SimpleObjectProperty<ChessType>(ChessType.EMPTY);
            }
        }
        this.currentChessType = new SimpleObjectProperty<>(ChessType.EMPTY);
        this.chessStateProperty = new SimpleObjectProperty<>(ChessState.GAME_READY);
        this.lastMovePoint = new SimpleObjectProperty<>();
        this.history = new ArrayList<>();
    }

    public void restart() {
        LOGGER.info("start a new game.");
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                this.chessboardProperty[i][j].set(ChessType.EMPTY);
            }
        }
        this.chessStateProperty.set(ChessState.GAME_ON);
        // change current chess type to fire action.
        this.currentChessType.set(ChessType.EMPTY);
        this.currentChessType.set(ChessType.BLACK);
        this.lastMovePoint.set(null);
        this.history.clear();
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
    public void takeMove(Point point) {
        LOGGER.info("start moving: {} {}", point, this.currentChessType);
        Preconditions.checkArgument(GameReferee.isInChessboard(point), "the position is out of range.");
        Preconditions.checkArgument(this.chessStateProperty.get() == ChessState.GAME_ON, "the chess game isn't on.");
        Preconditions.checkArgument(this.getChessboard(point) == ChessType.EMPTY, "the position is not empty.");

        // make move.
        this.chessboardProperty[point.x][point.y].set(currentChessType.get());
        Chessboard chessboard = this.getChessboard();
        if (GameReferee.isWin(chessboard, point)) { // if win.
            ChessState winner = ChessType.BLACK == currentChessType.get() ? ChessState.BLACK_WIN : ChessState.WHITE_WIN;
            this.chessStateProperty.set(winner);
        } else if (GameReferee.isDraw(chessboard, point)) { // if draw.
            this.chessStateProperty.set(ChessState.GAME_DRAW);
        } else {
            this.lastMovePoint.set(new Point(point));
            this.history.add(Pair.of(this.currentChessType.get(), new Point(point)));
            // finish this move, and change the current chess type and current
            // player.
            this.currentChessType.set(GameReferee.nextChessType(this.currentChessType.get()));
        }
        LOGGER.info("finish moving: {} {}", point, this.chessStateProperty.get());
    }

    public void addChessStateChangeListener(ChangeListener<ChessState> listener) {
        this.chessStateProperty.addListener(listener);
    }

    public void addChessboardChangeListener(Point point, ChangeListener<ChessType> listener) {
        this.chessboardProperty[point.x][point.y].addListener(listener);
    }

    public void addCurrentChessTypeChangeListener(ChangeListener<ChessType> listener) {
        this.currentChessType.addListener(listener);
    }

    public void addLastMovePointChangeListener(ChangeListener<Point> listener) {
        this.lastMovePoint.addListener(listener);
    }

    public ChessType getChessboard(Point point) {
        return this.chessboardProperty[point.x][point.y].get();
    }

    public Chessboard getChessboard() {
        Chessboard chessboard = new Chessboard();
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
        return this.currentChessType.get();
    }

    /**
     * @return the history
     */
    public List<Pair<ChessType, Point>> getHistory() {
        return history;
    }

}
