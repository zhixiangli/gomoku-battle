/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch;

import java.awt.Point;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.common.GomokuPatternStatistics;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchProphet {

    public static final double estimateChessboardValue(Chessboard chessboard, ChessType selfChessType) {
        Preconditions.checkArgument(selfChessType != ChessType.EMPTY);
        double selfValue = estimateChessboardValueByChessType(chessboard, selfChessType);
        double opponentValue = estimateChessboardValueByChessType(chessboard, GameReferee.nextChessType(selfChessType));
        if (Double.compare(selfValue, opponentValue) >= 0) {
            return selfValue;
        } else {
            return -opponentValue;
        }
    }

    private static final double estimateChessboardValueByChessType(Chessboard chessboard, ChessType chessType) {
        double value = 0;
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                if (chessboard.getChess(i, j) == chessType) {
                    value += estimatePointValueByChessType(chessboard, new Point(i, j), chessType);
                }
            }
        }
        return value;
    }

    public static final double estimatePointValue(Chessboard chessboard, Point point) {
        return estimatePointValueByChessType(chessboard, point, ChessType.BLACK)
                + estimatePointValueByChessType(chessboard, point, ChessType.WHITE);
    }

    private static final double estimatePointValueByChessType(Chessboard chessboard, Point point, ChessType chessType) {
        GomokuPatternStatistics statistics = GlobalAnalyser.getPatternStatistics(chessboard, point, chessType);
        double value = 0;
        if (statistics.getFive() > 0) {
            value += statistics.getFive() * AlphaBetaSearchConst.Estimate.FIVE;
        }
        if (statistics.getOpenFour() > 0 || statistics.getHalfOpenFour() > 1 || (statistics.getHalfOpenFour() > 0
                && (statistics.getOpenThree() + statistics.getSpacedOpenThree() > 0))) {
            value += AlphaBetaSearchConst.Estimate.OPEN_FOUR;
        }
        if (statistics.getOpenThree() + statistics.getSpacedOpenThree() > 1) {
            value += AlphaBetaSearchConst.Estimate.DOUBLE_OPEN_THREE;
        }
        if (statistics.getHalfOpenThree() > 0 && (statistics.getSpacedOpenThree() + statistics.getOpenThree() > 0)) {
            value += AlphaBetaSearchConst.Estimate.OPEN_AND_HALF_OPEN_THREE;
        }
        if (statistics.getHalfOpenFour() > 0) {
            value += AlphaBetaSearchConst.Estimate.HALF_OPEN_FOUR;
        }
        if (statistics.getOpenThree() > 0) {
            value += AlphaBetaSearchConst.Estimate.OPEN_THREE;
        }
        if (statistics.getSpacedOpenThree() > 0) {
            value += AlphaBetaSearchConst.Estimate.SPACED_OPEN_THREE;
        }
        if (statistics.getOpenTwo() + statistics.getOneSpacedOpenTwo() + statistics.getTwoSpacedOpenTwo() > 1) {
            value += AlphaBetaSearchConst.Estimate.DOUBLE_OPEN_TWO;
        }
        if (statistics.getHalfOpenThree() > 0) {
            value += AlphaBetaSearchConst.Estimate.HALF_OPEN_THREE;
        }
        if (statistics.getOpenTwo() > 0) {
            value += AlphaBetaSearchConst.Estimate.OPEN_TWO;
        }
        if (statistics.getOneSpacedOpenTwo() > 0) {
            value += AlphaBetaSearchConst.Estimate.ONE_SPACED_OPEN_TWO;
        }
        if (statistics.getTwoSpacedOpenTwo() > 0) {
            value += AlphaBetaSearchConst.Estimate.TWO_SPACED_OPEN_TWO;
        }
        if (statistics.getHalfOpenTwo() > 0) {
            value += AlphaBetaSearchConst.Estimate.HALF_OPEN_TWO;
        }
        return value;
    }

}
