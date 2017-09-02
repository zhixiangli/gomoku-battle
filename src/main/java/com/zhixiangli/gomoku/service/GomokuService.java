/**
 * 
 */
package com.zhixiangli.gomoku.service;

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
import com.zhixiangli.gomoku.chessboard.ChessState;
import com.zhixiangli.gomoku.common.GomokuConstant;
import com.zhixiangli.gomoku.common.GomokuReferee;

/**
 * adapter between gomoku AI and UI.
 * 
 * @author lizhixiang
 * @date 2015年6月5日
 */
public class GomokuService {

	/**
	 * chessboard property, if changed the UI will changed as well.
	 */
	private SimpleIntegerProperty[][] chessboardProperty = new SimpleIntegerProperty[GomokuConstant.CHESSBOARD_GRID_NUM][GomokuConstant.CHESSBOARD_GRID_NUM];

	/**
	 * current play's chess type, white or black.
	 */
	private ChessType currentChessType = ChessType.BLACK;

	/**
	 * current play property, if changed will let another play to move.
	 */
	private SimpleObjectProperty<Class<? extends GomokuAgent>> currentAgentProperty = new SimpleObjectProperty<Class<? extends GomokuAgent>>();

	/**
	 * chessboard state property, game on, draw, black win, white win.
	 */
	private SimpleObjectProperty<ChessState> chessStateProperty = new SimpleObjectProperty<ChessState>(
			ChessState.GAME_ON);

	/**
	 * black player strategy class. GomokuAI's subclass if is computer, otherwise
	 * null.
	 */
	private Class<? extends GomokuAgent> blackPlayerStrategyClass;

	/**
	 * white player strategy class. GomokuAI's subclass if is computer, otherwise
	 * null.
	 */
	private Class<? extends GomokuAgent> whitePlayerStrategyClass;

	private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor(runnable -> {
		Thread thread = new Thread(runnable);
		thread.setDaemon(true);
		return thread;
	});

	public GomokuService() {
		for (int row = 0; row < GomokuConstant.CHESSBOARD_GRID_NUM; ++row) {
			for (int column = 0; column < GomokuConstant.CHESSBOARD_GRID_NUM; ++column) {
				this.chessboardProperty[row][column] = new SimpleIntegerProperty(ChessType.EMPTY.ordinal());
			}
		}
		currentAgentProperty.addListener(event -> this.makeStrategyMove());
	}

	/**
	 * 
	 * make move.
	 * 
	 * @param row
	 *            row index.
	 * @param column
	 *            column index.
	 */
	public void moveOnMouseClicked(int row, int column) {
		// make move.
		this.setChessboardProperty(row, column, this.currentChessType);
		if (GomokuReferee.isWin(adaptChessboard(), row, column)) { // if win.
			this.chessStateProperty
					.set(ChessType.BLACK == this.currentChessType ? ChessState.BLACK_WIN : ChessState.WHITE_WIN);
		} else if (GomokuReferee.isDraw(adaptChessboard(), row, column)) { // if draw.
			this.chessStateProperty.set(ChessState.GAME_DRAW);
		} else {
			// finish this move, and change the current chess type and current
			// player.
			this.currentChessType = GomokuReferee.nextChessType(this.currentChessType);

			// to make current player property change.
			this.currentAgentProperty.set(null);
			this.currentAgentProperty.set(ChessType.BLACK == currentChessType ? this.blackPlayerStrategyClass
					: this.whitePlayerStrategyClass);
		}
	}

	public void makeStrategyMove() {
		if (null == currentAgentProperty) {
			return;
		}
		Task<Point> strategyMoveTask = new Task<Point>() {
			@Override
			protected Point call() throws Exception {
				return SmartGomoku.next(currentAgentProperty.get(), adaptChessboard(), currentChessType);
			}
		};
		strategyMoveTask.setOnSucceeded(event -> {
			Point point = strategyMoveTask.getValue();
			this.moveOnMouseClicked(point.x, point.y);
		});
		this.singleThreadExecutor.submit(strategyMoveTask);
	}

	private Chessboard adaptChessboard() {
		Chessboard chessboard = new Chessboard();
		for (int i = 0; i < GomokuConstant.CHESSBOARD_GRID_NUM; ++i) {
			for (int j = 0; j < GomokuConstant.CHESSBOARD_GRID_NUM; ++j) {
				chessboard.setChess(i, j, ChessType.values()[chessboardProperty[i][j].get()]);
			}
		}
		return chessboard;
	}

	/**
	 * getter method for property chessStateProperty
	 * 
	 * @return the chessboardStateProperty
	 */
	public SimpleObjectProperty<ChessState> getChessStateProperty() {
		return chessStateProperty;
	}

	/**
	 * setter method for property blackPlayerStrategyClass
	 * 
	 * @param blackPlayerStrategyClass
	 *            the blackPlayerStrategyClass to set
	 */
	public void setBlackPlayerStrategyClass(Class<? extends GomokuAgent> blackPlayerStrategyClass) {
		this.blackPlayerStrategyClass = blackPlayerStrategyClass;
	}

	/**
	 * setter method for property whitePlayerStrategyClass
	 * 
	 * @param whitePlayerStrategyClass
	 *            the whitePlayerStrategyClass to set
	 */
	public void setWhitePlayerStrategyClass(Class<? extends GomokuAgent> whitePlayerStrategyClass) {
		this.whitePlayerStrategyClass = whitePlayerStrategyClass;
	}

	public SimpleIntegerProperty getChessboardProperty(int row, int column) {
		return this.chessboardProperty[row][column];
	}

	public void setChessboardProperty(int row, int column, ChessType chessType) {
		this.chessboardProperty[row][column].set(chessType.ordinal());
	}

	/**
	 * @return the currentAgentProperty
	 */
	public SimpleObjectProperty<Class<? extends GomokuAgent>> getCurrentAgentProperty() {
		return currentAgentProperty;
	}
}
