/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch;

import java.awt.Point;
import java.util.List;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.analysis.ChessPatternType;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;

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
        List<ChessPatternType> patternTypeList = GlobalAnalyser.getPatternStatistics(chessboard, point, chessType);
        double value = 0;
        for (ChessPatternType patternType : patternTypeList) {
            value += AlphaBetaSearchConst.ESTIMATED_MAP.get(patternType);
        }
        return value;
    }

}
