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
	public static final boolean isDraw(Chessboard chessboard, Point point) {
		int size = Chessboard.DEFAULT_SIZE;
		for (int i = 0; i < size; ++i) {
			for (int j = 0; j < size; ++j) {
				if (chessboard.getChess(i, j) == ChessType.EMPTY) {
					return false;
				}
			}
		}
		return !isWin(chessboard, point);
	}

	public static final boolean isDraw(Chessboard chessboard, int row, int column) {
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
	public static final boolean isWin(Chessboard chessboard, Point point) {
		return Arrays.stream(GomokuConstant.DIRECTIONS).anyMatch(
				direction -> getContinuousCount(chessboard, point, direction) >= GomokuConstant.CONTINUOUS_NUMBER);
	}

	public static final boolean isWin(Chessboard chessboard, int row, int column) {
		return isWin(chessboard, new Point(row, column));
	}

	/**
	 * have enough space to win?
	 * 
	 * @param chessboard
	 * @param point
	 * @param delta
	 * @return
	 */
	public static final boolean isPossibleWin(Chessboard chessboard, Point point, Point delta) {
		int possible = 0;
		int size = Chessboard.DEFAULT_SIZE;
		ChessType chessType = chessboard.getChess(point.x, point.y);
		for (int i = point.x, j = point.y; i >= 0 && i < size && j >= 0 && j < size
				&& possible < GomokuConstant.CONTINUOUS_NUMBER; i += delta.x, j += delta.y) {
			ChessType currentChessType = chessboard.getChess(i, j);
			if (currentChessType == chessType) {
				++possible;
			} else {
				if (currentChessType == ChessType.EMPTY) {
					++possible;
				} else {
					break;
				}
			}
		}
		for (int i = point.x - delta.x, j = point.y - delta.y; i >= 0 && i < size && j >= 0 && j < size
				&& possible < GomokuConstant.CONTINUOUS_NUMBER; i -= delta.x, j -= delta.y) {
			ChessType currentChessType = chessboard.getChess(i, j);
			if (currentChessType == chessType) {
				++possible;
			} else {
				if (currentChessType == ChessType.EMPTY) {
					++possible;
				} else {
					break;
				}
			}
		}
		return possible >= GomokuConstant.CONTINUOUS_NUMBER;
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
	public static final Pair<Integer, Integer> analyseContinuousCount(Chessboard chessboard, Point point, Point delta) {
		ChessType chessType = chessboard.getChess(point.x, point.y);

		if (ChessType.EMPTY == chessType) {
			return new Pair<Integer, Integer>(0, 0);
		}
		int size = Chessboard.DEFAULT_SIZE;

		int serial0 = 0;
		int blank0 = 0;
		int other0 = 0;
		for (int i = point.x, j = point.y; i >= 0 && i < size && j >= 0 && j < size; i += delta.x, j += delta.y) {
			ChessType currentChessType = chessboard.getChess(i, j);
			if (currentChessType == chessType) {
				if (blank0 == 0) {
					++serial0;
				} else {
					++other0;
				}
			} else if (ChessType.EMPTY == currentChessType) {
				++blank0;
				if (blank0 > 1) {
					break;
				}
			} else {
				break;
			}
		}

		int serial1 = 0;
		int blank1 = 0;
		int other1 = 0;
		for (int i = point.x - delta.x, j = point.y - delta.y; i >= 0 && i < size && j >= 0
				&& j < size; i -= delta.x, j -= delta.y) {
			ChessType currentChessType = chessboard.getChess(i, j);
			if (currentChessType == chessType) {
				if (blank1 == 0) {
					++serial1;
				} else {
					++other1;
				}
			} else if (ChessType.EMPTY == currentChessType) {
				++blank1;
				if (blank1 > 1) {
					break;
				}
			} else {
				break;
			}
		}

		int serial = serial0 + serial1 + (Math.max(other1, other0) - 1);
		int blank = Math.min(1, blank1) + Math.min(1, blank0);
		if (!isPossibleWin(chessboard, point, delta)) {
			--serial;
			blank = 0;
		}
		return new Pair<Integer, Integer>(serial, blank);
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
	public static final int getContinuousCount(Chessboard chessboard, Point point, Point delta) {
		ChessType chessType = chessboard.getChess(point.x, point.y);
		if (ChessType.EMPTY == chessType) {
			return 0;
		}
		int size = Chessboard.DEFAULT_SIZE;

		int cnt = 0;
		for (int i = point.x, j = point.y; i >= 0 && i < size && j >= 0 && j < size; i += delta.x, j += delta.y) {
			if (chessboard.getChess(i, j) == chessType) {
				++cnt;
			} else {
				break;
			}
		}

		for (int i = point.x - delta.x, j = point.y - delta.y; i >= 0 && i < size && j >= 0
				&& j < size; i -= delta.x, j -= delta.y) {
			if (chessboard.getChess(i, j) == chessType) {
				++cnt;
			} else {
				break;
			}
		}
		return cnt;
	}

	/**
	 * 
	 * get next chess type. if current is black, and next is white.
	 * 
	 * @param chessType
	 *            chess type.
	 * @return next chess type.
	 */
	public static final ChessType nextChessType(ChessType chessType) {
		return ChessType.BLACK == chessType ? ChessType.WHITE : ChessType.BLACK;
	}

}
