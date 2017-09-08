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
                if (chessboard.getChess(i, j) == ChessType.EMPTY) {
                    continue;
                }
                value += estimatePointValueByChessType(chessboard, new Point(i, j), chessType);
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
        value += statistics.getFive() * AlphaBetaSearchConst.Estimate.FIVE;
        if (statistics.getOpenFour() > 0) {
            value += AlphaBetaSearchConst.Estimate.OPEN_FOUR;
        }
        if (statistics.getOpenThree() + statistics.getSpacedOpenThree() + statistics.getHalfOpenFour() > 1) {
            value += AlphaBetaSearchConst.Estimate.OPEN_FOUR;
        }
        value += statistics.getHalfOpenFour() * AlphaBetaSearchConst.Estimate.HALF_OPEN_FOUR;
        value += statistics.getOpenThree() * AlphaBetaSearchConst.Estimate.OPEN_THREE;
        value += statistics.getSpacedOpenThree() * AlphaBetaSearchConst.Estimate.SPACED_OPEN_THREE;
        value += statistics.getHalfOpenThree() * AlphaBetaSearchConst.Estimate.HALF_OPEN_THREE;
        value += statistics.getOpenTwo() * AlphaBetaSearchConst.Estimate.OPEN_TWO;
        value += statistics.getOneSpacedOpenTwo() * AlphaBetaSearchConst.Estimate.ONE_SPACED_OPEN_TWO;
        value += statistics.getTwoSpacedOpenTwo() * AlphaBetaSearchConst.Estimate.TWO_SPACED_OPEN_TWO;
        value += statistics.getHalfOpenTwo() * AlphaBetaSearchConst.Estimate.HALF_OPEN_TWO;
        return value;
    }

}
