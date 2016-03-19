/**
 * 
 */
package com.zhixiangli.smartgomoku.common;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

/**
 * chessboard referee.
 * 
 * @author lizhixiang
 *
 */
public class GomokuReferee {

	/**
	 * random generator.
	 */
	public static final Random RANDOM = new SecureRandom();

	/**
	 * 
	 * the game is draw?
	 * 
	 * @param chessboard
	 *            Chessboard.
	 * @param point
	 *            the position was put just now.
	 * @return true if is draw, otherwise false.
	 */
	public static boolean isDraw(Chessboard chessboard, Point point) {
		int size = chessboard.getSize();
		int blackCount = chessboard.getChessTypeCount(ChessType.BLACK);
		int whiteCount = chessboard.getChessTypeCount(ChessType.WHITE);
		return size * size == blackCount + whiteCount && !isWin(chessboard, point);
	}

	public static boolean isDraw(Chessboard chessboard, int row, int column) {
		return isDraw(chessboard, new Point(row, column));
	}

	/**
	 * 
	 * the game is win?
	 * 
	 * @param chessboard
	 *            Chessboard.
	 * @param point
	 *            the position was put just now.
	 * @return true if is win, otherwise false.
	 */
	public static boolean isWin(Chessboard chessboard, Point point) {
		return Arrays.stream(GomokuConstant.DIRECTIONS)
				.anyMatch(direction -> getContinuousCount(chessboard, point, direction)
						.getFirst() >= GomokuConstant.CONTINUOUS_NUMBER);
	}

	public static boolean isWin(Chessboard chessboard, int row, int column) {
		return isWin(chessboard, new Point(row, column));
	}

	/**
	 * 
	 * get continuous number of same color.
	 * 
	 * @param chessboard
	 *            Chessboard.
	 * @param point
	 *            the position was put just now.
	 * @param delta
	 *            direction.
	 * @return Pair<Integer, Integer>. The first number is continuous number,
	 *         and the second number is blank number.
	 */
	public static Pair<Integer, Integer> getContinuousCount(Chessboard chessboard, Point point, Point delta) {
		if (ChessType.EMPTY == chessboard.getChess(point.x, point.y)) {
			return new Pair<Integer, Integer>(0, 0);
		}
		int cnt = 0;
		int blank = 0;
		int size = chessboard.getSize();
		for (int i = point.x, j = point.y; i >= 0 && i < size && j >= 0 && j < size; i += delta.x, j += delta.y) {
			if (chessboard.getChess(i, j) == chessboard.getChess(point.x, point.y)) {
				++cnt;
			} else {
				if (ChessType.EMPTY == chessboard.getChess(i, j)) {
					++blank;
				}
				break;
			}
		}
		for (int i = point.x, j = point.y; i >= 0 && i < size && j >= 0 && j < size; i -= delta.x, j -= delta.y) {
			if (chessboard.getChess(i, j) == chessboard.getChess(point.x, point.y)) {
				++cnt;
			} else {
				if (ChessType.EMPTY == chessboard.getChess(i, j)) {
					++blank;
				}
				break;
			}
		}
		return new Pair<Integer, Integer>(--cnt, blank);
	}

	/**
	 * 
	 * get next chess type. if current is black, and next is white.
	 * 
	 * @param chessType
	 *            chess type.
	 * @return next chess type.
	 */
	public static ChessType nextChessType(ChessType chessType) {
		return ChessType.BLACK == chessType ? ChessType.WHITE : ChessType.BLACK;
	}
}
