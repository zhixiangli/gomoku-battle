/**
 * 
 */
package com.zhixiangli.gomoku.common;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.zhixiangli.gomoku.chessboard.ChessType;
import com.zhixiangli.gomoku.chessboard.Chessboard;

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

    public static final boolean isFull(Chessboard chessboard) {
        int size = chessboard.getLength();
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (chessboard.getChess(i, j) == ChessType.EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public static final boolean isDraw(Chessboard chessboard, int row, int column) {
        return isFull(chessboard) && !isWin(chessboard, row, column);
    }

    /**
     * 
     * the game is win?
     * 
     * @param chessboard Chessboard.
     * @param point the position was put just now.
     * @return true if is win, otherwise false.
     */
    public static final boolean isWin(Chessboard chessboard, Point point) {
        return isWin(chessboard, point.x, point.y);
    }

    public static final boolean isWin(Chessboard chessboard, int row, int column) {
        return Arrays.stream(GomokuConstant.DIRECTIONS).anyMatch(direction -> getContinuousCount(chessboard, row,
                column, direction) >= GomokuConstant.CONTINUOUS_NUMBER);
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
        int size = chessboard.getLength();
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
     * @param chessboard Chessboard.
     * @param point the position was put just now.
     * @param delta direction.
     * @return Pair<Integer, Integer>. The first number is continuous number, and the second number
     *         is blank number.
     */
    public static final Pair<Integer, Integer> analyseContinuousCount(Chessboard chessboard, Point point, Point delta) {
        ChessType chessType = chessboard.getChess(point.x, point.y);

        if (ChessType.EMPTY == chessType) {
            return MutablePair.of(0, 0);
        }
        int size = chessboard.getLength();

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
        for (int i = point.x - delta.x, j = point.y - delta.y; i >= 0 && i < size && j >= 0 && j < size; i -=
                delta.x, j -= delta.y) {
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
        return MutablePair.of(serial, blank);
    }

    /**
     * 
     * get continuous number of same color.
     * 
     * @param chessboard Chessboard.
     * @param point the position was put just now.
     * @param d direction.
     * @return Pair<Integer, Integer>. The first number is continuous number, and the second number
     *         is blank number.
     */
    public static final int getContinuousCount(Chessboard chessboard, Point point, Point d) {
        return getContinuousCount(chessboard, point.x, point.y, d);
    }

    /**
     * 
     * get continuous number of same color.
     * 
     * @param chessboard Chessboard.
     * @param point the position was put just now.
     * @param d direction.
     * @return Pair<Integer, Integer>. The first number is continuous number, and the second number
     *         is blank number.
     */
    public static final int getContinuousCount(Chessboard chessboard, int x, int y, Point d) {
        ChessType chessType = chessboard.getChess(x, y);
        if (ChessType.EMPTY == chessType) {
            return 0;
        }
        int size = chessboard.getLength();

        int cnt = 0;
        for (int i = x, j = y; i >= 0 && i < size && j >= 0 && j < size; i += d.x, j += d.y) {
            if (chessboard.getChess(i, j) == chessType) {
                ++cnt;
            } else {
                break;
            }
        }

        for (int i = x - d.x, j = y - d.y; i >= 0 && i < size && j >= 0 && j < size; i -= d.x, j -= d.y) {
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
     * @param chessType chess type.
     * @return next chess type.
     */
    public static final ChessType nextChessType(ChessType chessType) {
        return ChessType.BLACK == chessType ? ChessType.WHITE : ChessType.BLACK;
    }

}
