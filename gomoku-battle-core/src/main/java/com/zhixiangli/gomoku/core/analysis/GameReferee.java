package com.zhixiangli.gomoku.core.analysis;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;

import java.awt.Point;
import java.util.Arrays;

/**
 * chessboard referee.
 *
 * @author lizhixiang
 */
public class GameReferee {

    public static boolean isDraw(final Chessboard chessboard, final Point point) {
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                if (chessboard.getChess(i, j) == ChessType.EMPTY) {
                    return false;
                }
            }
        }
        return !isWin(chessboard, point);
    }

    /**
     * the game is win?
     *
     * @param chessboard Chessboard.
     * @param point      the position was put just now.
     * @return true if is win, otherwise false.
     */
    public static boolean isWin(final Chessboard chessboard, final Point point) {
        Preconditions.checkArgument(isInChessboard(point), "point out of chessboard.");
        Preconditions.checkArgument(ChessType.EMPTY != chessboard.getChess(point), "the position is empty.");
        return Arrays.stream(GomokuConst.DIRECTIONS).anyMatch(direction -> GlobalAnalyser
                .getConsecutiveCount(chessboard, point, direction) >= GomokuConst.CONSECUTIVE_NUM);
    }

    /**
     * get next chess type. if current is black, and next is white.
     *
     * @param chessType chess type.
     * @return next chess type.
     */
    public static ChessType nextChessType(final ChessType chessType) {
        Preconditions.checkArgument(chessType != ChessType.EMPTY, "empty type has no next type.");
        return (ChessType.BLACK == chessType) ? ChessType.WHITE : ChessType.BLACK;
    }

    public static boolean isInChessboard(final Point point) {
        return isInChessboard(point.x, point.y);
    }

    static boolean isInChessboard(final int row, final int column) {
        return (row >= 0) && (column >= 0) && (row < GomokuConst.CHESSBOARD_SIZE) && (column < GomokuConst.CHESSBOARD_SIZE);
    }

}
