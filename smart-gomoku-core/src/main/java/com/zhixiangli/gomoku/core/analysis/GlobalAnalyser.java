/**
 * 
 */
package com.zhixiangli.gomoku.core.analysis;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.chessboard.ChessPatternType;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.common.GomokuPatternStatistics;

/**
 * @author zhixiangli
 *
 */
public class GlobalAnalyser {

    /**
     * 
     * find point can be put chess.
     * 
     * @param chessboard
     *            Chessboard.
     * @return point list.
     */
    public static final Set<Point> getEmptyPointsAround(Chessboard chessboard, int range) {
        Set<Point> pointSet = new HashSet<>();
        int size = GomokuConst.CHESSBOARD_SIZE;
        for (int row = 0; row < size; ++row) {
            for (int column = 0; column < size; ++column) {
                if (ChessType.EMPTY == chessboard.getChess(row, column)) {
                    continue;
                }
                int a = Math.max(0, row - range), b = Math.min(size - 1, row + range);
                int c = Math.max(0, column - range), d = Math.min(size - 1, column + range);
                for (int i = a; i <= b; ++i) {
                    for (int j = c; j <= d; ++j) {
                        if (ChessType.EMPTY == chessboard.getChess(i, j)) {
                            pointSet.add(new Point(i, j));
                        }
                    }
                }
            }
        }
        return pointSet;
    }

    /**
     * 
     * get continuous number of same color.
     * 
     * @param chessboard
     *            Chessboard.
     * @param point
     *            the position was put just now.
     * @param direction
     *            direction.
     * @return Pair<Integer, Integer>. The first number is continuous number, and
     *         the second number is blank number.
     */
    public static final int getConsecutiveCount(Chessboard chessboard, Point point, Point direction) {
        Preconditions.checkArgument(GameReferee.isInChessboard(point), "point out of chessboard.");

        ChessType chessType = chessboard.getChess(point);
        Preconditions.checkArgument(ChessType.EMPTY != chessType, "the position is empty.");
        int count = 0;
        for (int x = point.x, y = point.y; GameReferee.isInChessboard(x, y); x -= direction.x, y -= direction.y) {
            if (chessboard.getChess(x, y) != chessType) {
                break;
            }
            ++count;
        }
        for (int x = point.x + direction.x, y = point.y + direction.y; GameReferee.isInChessboard(x,
                y); x += direction.x, y += direction.y) {
            if (chessboard.getChess(x, y) != chessType) {
                break;
            }
            ++count;
        }
        return count;
    }

    /**
     * 
     * if the ChessType is to be put in Point, compute the pattern statistics of the
     * Point.
     * 
     * @param chessboard
     *            current chessboard info.
     * @param point
     *            the position to be computed.
     * @param chessType
     *            chess type to be computed.
     * @return
     */
    public static final GomokuPatternStatistics getPatternStatistics(Chessboard chessboard, Point point,
            ChessType chessType) {
        Preconditions.checkArgument(chessType != ChessType.EMPTY);

        ChessType oldChessType = chessboard.getChess(point);
        chessboard.setChess(point, chessType);
        GomokuPatternStatistics statistics = new GomokuPatternStatistics();
        for (Point direction : GomokuConst.DIRECTIONS) {
            ChessPatternType patternType = getChessPatternType(chessboard, point, direction);
            statistics.update(patternType);
        }
        chessboard.setChess(point, oldChessType);
        return statistics;
    }

    public static final ChessPatternType getChessPatternType(Chessboard chessboard, Point point, Point direction) {
        List<ChessType> pattern = getChessTypeRange(chessboard, point, direction, GomokuConst.CONSECUTIVE_NUM);
        if (pattern.size() <= GomokuConst.CONSECUTIVE_NUM + 1) {
            return PatternRecognizer.getPatternType(pattern, chessboard.getChess(point));
        }
        ChessPatternType patternType = ChessPatternType.OTHERS;
        for (int i = 0; i + GomokuConst.CONSECUTIVE_NUM + 1 <= pattern.size(); ++i) {
            ChessPatternType newPatternType = PatternRecognizer.getPatternType(
                    pattern.subList(i, i + GomokuConst.CONSECUTIVE_NUM + 1), chessboard.getChess(point));
            if (newPatternType.compareTo(patternType) > 0)
                patternType = newPatternType;
        }
        return patternType;
    }

    private static final List<ChessType> getChessTypeRange(Chessboard chessboard, Point point, Point direction,
            int range) {
        int x = point.x - direction.x * GomokuConst.CONSECUTIVE_NUM;
        int y = point.y - direction.y * GomokuConst.CONSECUTIVE_NUM;
        int length = range * 2 + 1;
        List<ChessType> pattern = new ArrayList<>();
        for (; length-- > 0; x += direction.x, y += direction.y) {
            if (GameReferee.isInChessboard(x, y)) {
                pattern.add(chessboard.getChess(x, y));
            }
        }
        return pattern;
    }
}
