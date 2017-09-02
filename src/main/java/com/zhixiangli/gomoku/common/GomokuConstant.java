/**
 * 
 */
package com.zhixiangli.gomoku.common;

import java.awt.Point;
import java.util.Calendar;

/**
 * constant.
 * 
 * @author lizhixiang
 * @date 2015年6月6日
 */
public class GomokuConstant {

	/**
	 * four directions.
	 */
	public static final Point[] DIRECTIONS = { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(-1, 1) };

	/**
	 * continuous number to win the game.
	 */
	public static final int CONTINUOUS_NUMBER = 5;

	/**
	 * the number of grid in the chessboard.
	 */
	public static final int CHESSBOARD_GRID_NUM = 15;

	/**
	 * title.
	 */
	public static final String TITLE = "SMART GOMOKU";

	/**
	 * copyright.
	 */
	public static final String COPYRIGHT = String.format("©%d LI ZHIXIANG", Calendar.getInstance().get(Calendar.YEAR));

	/**
	 * length of each cell.
	 */
	public static final double GRID_SIZE = 800.0 / CHESSBOARD_GRID_NUM;
}
