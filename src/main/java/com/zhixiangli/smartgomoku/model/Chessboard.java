/**
 * 
 */
package com.zhixiangli.smartgomoku.model;

import java.awt.Point;
import java.util.HashMap;
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
	 * chessboard is size*size.
	 */
	private int size;

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
		this(DEFAULT_SIZE);
	}

	/**
	 * init an empty chessboard.
	 */
	public Chessboard(int size) {
		this.size = size;
		this.chessboard = new ChessType[this.size][this.size];
		this.chessTypeCount = new HashMap<>();

		this.clear();
	}

	/**
	 * 
	 * clear the chessboard.
	 */
	public void clear() {
		for (int row = 0; row < this.size; ++row) {
			for (int column = 0; column < this.size; ++column) {
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

	/**
	 * getter method for property size
	 * 
	 * @return the size
	 */
	public int getSize() {
		return size;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Chessboard clone() {
		Chessboard clonedChessboard = new Chessboard();
		for (int row = 0; row < this.size; ++row) {
			for (int column = 0; column < this.size; ++column) {
				clonedChessboard.setChess(row, column, this.chessboard[row][column]);
			}
		}
		return clonedChessboard;
	}

}
