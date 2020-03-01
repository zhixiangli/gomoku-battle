package com.zhixiangli.gomoku.core.analysis;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;

import java.awt.Point;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;

/**
 * @author zhixiangli
 */
public class GlobalAnalyser {

    private GlobalAnalyser() {
    }

    /**
     * find point can be put chess.
     *
     * @param chessboard Chessboard.
     * @return point list.
     */
    public static Point[] getEmptyPointsAround(final Chessboard chessboard, final int range) {
        final boolean[][] isEmpty = new boolean[GomokuConst.CHESSBOARD_SIZE][GomokuConst.CHESSBOARD_SIZE];
        int size = 0;
        for (int row = 0; row < GomokuConst.CHESSBOARD_SIZE; ++row) {
            for (int column = 0; column < GomokuConst.CHESSBOARD_SIZE; ++column) {
                if (ChessType.EMPTY == chessboard.getChess(row, column)) {
                    continue;
                }
                final int a = Math.max(0, row - range);
                final int b = Math.min(GomokuConst.CHESSBOARD_SIZE - 1, row + range);
                final int c = Math.max(0, column - range);
                final int d = Math.min(GomokuConst.CHESSBOARD_SIZE - 1, column + range);
                for (int i = a; i <= b; ++i) {
                    for (int j = c; j <= d; ++j) {
                        if (!isEmpty[i][j] && (ChessType.EMPTY == chessboard.getChess(i, j))) {
                            isEmpty[i][j] = true;
                            ++size;
                        }
                    }
                }
            }
        }
        final Point[] emptyPoints = new Point[size];
        int index = 0;
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                if (isEmpty[i][j]) {
                    emptyPoints[index] = new Point(i, j);
                    index++;
                }
            }
        }
        Preconditions.checkState(index == size);
        return emptyPoints;
    }

    /**
     * get continuous number of same color.
     *
     * @param chessboard Chessboard.
     * @param point      the position was put just now.
     * @param direction  direction.
     * @return Pair<Integer, Integer>. The first number is continuous number, and
     * the second number is blank number.
     */
    static int getConsecutiveCount(final Chessboard chessboard, final Point point, final Point direction) {
        Preconditions.checkArgument(GameReferee.isInChessboard(point), "point out of chessboard.");

        final ChessType chessType = chessboard.getChess(point);
        Preconditions.checkArgument(ChessType.EMPTY != chessType, "the position is empty.");
        int count = 0;
        for (int x = point.x, y = point.y;
             GameReferee.isInChessboard(x, y) && (chessboard.getChess(x, y) == chessType);
             x -= direction.x, y -= direction.y) {
            ++count;
        }
        for (int x = point.x + direction.x, y = point.y + direction.y;
             GameReferee.isInChessboard(x, y) && (chessboard.getChess(x, y) == chessType);
             x += direction.x, y += direction.y) {
            ++count;
        }
        return count;
    }

    /**
     * if the ChessType is to be put in Point, compute the pattern statistics of the
     * Point.
     *
     * @param chessboard current chessboard info.
     * @param point      the position to be computed.
     * @param chessType  chess type to be computed.
     * @return
     */
    public static Map<PatternType, Integer> getPatternStatistics(final Chessboard chessboard, final Point point,
                                                                 final ChessType chessType) {
        Preconditions.checkArgument(chessType != ChessType.EMPTY);
        final ChessType lastChessType = chessboard.getChess(point);
        chessboard.setChess(point, chessType);
        final Map<PatternType, Integer> counter = new EnumMap<>(PatternType.class);
        for (final Point direction : GomokuConst.DIRECTIONS) {
            final PatternType patternType = getChessPatternType(chessboard, point, direction);
            counter.compute(patternType, (key, value) -> (null == value) ? 1 : (1 + value));
        }
        chessboard.setChess(point, lastChessType);
        return counter;
    }

    private static PatternType getChessPatternType(final Chessboard chessboard, final Point point, final Point direction) {
        final ChessType[] pattern = getChessTypeRange(chessboard, point, direction, GomokuConst.CONSECUTIVE_NUM);
        return PatternRecognizer.getBestPatternType(pattern, chessboard.getChess(point));
    }

    private static ChessType[] getChessTypeRange(final Chessboard chessboard, final Point point, final Point direction, final int range) {
        int x = point.x - (direction.x * range);
        int y = point.y - (direction.y * range);
        int length = (range * 2) + 1;
        final ChessType[] pattern = new ChessType[length];
        int size = 0;
        for (; length > 0; x += direction.x, y += direction.y, length--) {
            if (GameReferee.isInChessboard(x, y)) {
                pattern[size] = chessboard.getChess(x, y);
                size++;
            }
        }
        return Arrays.copyOf(pattern, size);
    }
}
