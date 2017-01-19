/**
 * 
 */
package com.zhixiangli.gomoku.fx;

import java.awt.Point;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;

import com.zhixiangli.gomoku.SmartGomoku;
import com.zhixiangli.gomoku.agent.GomokuAgent;
import com.zhixiangli.gomoku.chessboard.ChessType;
import com.zhixiangli.gomoku.chessboard.Chessboard;
import com.zhixiangli.gomoku.common.GomokuReferee;

/**
 * adapter between gomoku AI and UI.
 * 
 * @author lizhixiang
 * @date 2015年6月5日
 */
public class GomokuFXManager {

    /**
     * chessboard's data structure.
     */
    private Chessboard chessboard = new Chessboard();

    /**
     * chessboard property, if changed the UI will changed as well.
     */
    private SimpleIntegerProperty[][] chessboardProperty =
            new SimpleIntegerProperty[chessboard.getLength()][chessboard.getLength()];

    /**
     * current play's chess type, white or black.
     */
    private ChessType currentChessType = ChessType.BLACK;

    /**
     * current play property, if changed will let another play to move.
     */
    private SimpleObjectProperty<Class<? extends GomokuAgent>> currentPlayerProperty =
            new SimpleObjectProperty<Class<? extends GomokuAgent>>();

    /**
     * chessboard state property, game on, draw, black win, white win.
     */
    private SimpleObjectProperty<ChessboardState> chessboardStateProperty =
            new SimpleObjectProperty<ChessboardState>(ChessboardState.GAME_ON);

    /**
     * black player strategy class. GomokuAI's subclass if is computer, otherwise null.
     */
    private Class<? extends GomokuAgent> blackPlayerStrategyClass;

    /**
     * white player strategy class. GomokuAI's subclass if is computer, otherwise null.
     */
    private Class<? extends GomokuAgent> whitePlayerStrategyClass;

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable);
        thread.setDaemon(true);
        return thread;
    });

    public GomokuFXManager() {
        for (int row = 0; row < chessboard.getLength(); ++row) {
            for (int column = 0; column < chessboard.getLength(); ++column) {
                this.chessboardProperty[row][column] = new SimpleIntegerProperty(ChessType.EMPTY.ordinal());
            }
        }
    }

    public SimpleIntegerProperty getChessboardProperty(int row, int column) {
        return this.chessboardProperty[row][column];
    }

    public void setChessboardProperty(int row, int column, ChessType chessType) {
        this.chessboardProperty[row][column].set(chessType.ordinal());
        this.chessboard.setChess(row, column, chessType);
    }

    /**
     * 
     * make move.
     * 
     * @param row row index.
     * @param column column index.
     */
    public void makeMove(int row, int column) {
        // make move.
        this.setChessboardProperty(row, column, this.currentChessType);
        if (GomokuReferee.isWin(chessboard, row, column)) { // if win.
            this.chessboardStateProperty.set(
                    ChessType.BLACK == this.currentChessType ? ChessboardState.BLACK_WIN : ChessboardState.WHITE_WIN);
        } else if (GomokuReferee.isDraw(chessboard, row, column)) { // if draw.
            this.chessboardStateProperty.set(ChessboardState.GAME_DRAW);
        } else {
            // finish this move, and change the current chess type and current
            // player.
            this.currentChessType = GomokuReferee.nextChessType(this.currentChessType);

            // to make current player property change.
            this.currentPlayerProperty.set(null);
            this.currentPlayerProperty.set(ChessType.BLACK == currentChessType ? this.blackPlayerStrategyClass
                    : this.whitePlayerStrategyClass);
        }
    }

    public void makeStrategyMove() {
        Task<Point> strategyMoveTask = new Task<Point>() {
            @Override
            protected Point call() throws Exception {
                return SmartGomoku.next(currentPlayerProperty.get(), chessboard, currentChessType);
            }
        };
        strategyMoveTask.setOnSucceeded(event -> {
            Point point = strategyMoveTask.getValue();
            this.makeMove(point.x, point.y);
        });
        this.singleThreadExecutor.submit(strategyMoveTask);
    }

    /**
     * getter method for property chessboardStateProperty
     * 
     * @return the chessboardStateProperty
     */
    public SimpleObjectProperty<ChessboardState> getChessboardStateProperty() {
        return chessboardStateProperty;
    }

    /**
     * getter method for property currentPlayerProperty
     * 
     * @return the currentPlayerProperty
     */
    public SimpleObjectProperty<Class<? extends GomokuAgent>> getCurrentPlayerProperty() {
        return currentPlayerProperty;
    }

    /**
     * setter method for property blackPlayerStrategyClass
     * 
     * @param blackPlayerStrategyClass the blackPlayerStrategyClass to set
     */
    public void setBlackPlayerStrategyClass(Class<? extends GomokuAgent> blackPlayerStrategyClass) {
        this.blackPlayerStrategyClass = blackPlayerStrategyClass;
    }

    /**
     * setter method for property whitePlayerStrategyClass
     * 
     * @param whitePlayerStrategyClass the whitePlayerStrategyClass to set
     */
    public void setWhitePlayerStrategyClass(Class<? extends GomokuAgent> whitePlayerStrategyClass) {
        this.whitePlayerStrategyClass = whitePlayerStrategyClass;
    }

}
