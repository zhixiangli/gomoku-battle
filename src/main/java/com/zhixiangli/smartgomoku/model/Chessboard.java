/**
 * 
 */
package com.zhixiangli.smartgomoku.model;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zhixiangli.smartgomoku.common.GomokuReferee;

/**
 * the data structure of chessboard.
 * 
 * @author lizhixiang
 *
 */
public class Chessboard implements Cloneable {

	/**
	 * default chessboard size.
	 */
	public static final int DEFAULT_SIZE = 15;

	/**
	 * chessboard.
	 */
	private ChessType[][] chessboard;

	/**
	 * the count of each chess type.
	 */
	private Map<ChessType, Integer> chessTypeCount;

	/**
	 * init an empty chessboard.
	 */
	public Chessboard() {
		this.chessboard = new ChessType[DEFAULT_SIZE][DEFAULT_SIZE];
		this.chessTypeCount = new HashMap<>();

		this.clear();
	}

	/**
	 * 
	 * clear the chessboard.
	 */
	public void clear() {
		for (int row = 0; row < DEFAULT_SIZE; ++row) {
			for (int column = 0; column < DEFAULT_SIZE; ++column) {
				this.chessboard[row][column] = ChessType.EMPTY;
			}
		}
		this.chessTypeCount.clear();
	}

	/**
	 * 
	 * get chess type.
	 * 
	 * @param row
	 *            row index.
	 * @param column
	 *            column index.
	 * @return chess type.
	 */
	public ChessType getChess(int row, int column) {
		return this.chessboard[row][column];
	}

	public boolean setChess(int row, int column, ChessType chessType) {
		this.chessboard[row][column] = chessType;
		this.chessTypeCount.compute(chessType, (k, v) -> null == v ? 1 : v + 1);
		return chessType == ChessType.EMPTY ? false : GomokuReferee.isWin(this, row, column);
	}

	public boolean setChess(Point point, ChessType chessType) {
		return this.setChess(point.x, point.y, chessType);
	}

	/**
	 * 
	 * get the number of a chess type.
	 * 
	 * @param chessType
	 *            chess type.
	 * @return the number.
	 */
	public int getChessTypeCount(ChessType chessType) {
		Integer count = this.chessTypeCount.get(chessType);
		return null == count ? 0 : count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Chessboard clone() {
		Chessboard clonedChessboard = new Chessboard();
		for (int row = 0; row < DEFAULT_SIZE; ++row) {
			for (int column = 0; column < DEFAULT_SIZE; ++column) {
				clonedChessboard.setChess(row, column, this.chessboard[row][column]);
			}
		}
		return clonedChessboard;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		List<String> chessboardString = new ArrayList<>();
		chessboardString.add("------------------");
		for (int i = 0; i < DEFAULT_SIZE; ++i) {
			String tmp = "";
			for (int j = 0; j < DEFAULT_SIZE; ++j) {
				tmp += this.getChess(i, j).ordinal();
			}
			chessboardString.add(tmp);
		}
		chessboardString.add("------------------");
		return String.join("\n", chessboardString);
	}

}
