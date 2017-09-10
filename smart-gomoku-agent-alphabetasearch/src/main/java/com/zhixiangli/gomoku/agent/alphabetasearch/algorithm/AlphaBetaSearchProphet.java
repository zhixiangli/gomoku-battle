/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch.algorithm;

import java.awt.Point;
import java.util.List;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.agent.alphabetasearch.common.ProphetConst;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.chessboard.PatternType;
import com.zhixiangli.gomoku.core.common.GomokuConst;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchProphet {

    public static final double evaluateChessboardValue(Chessboard chessboard, ChessType selfChessType) {
        Preconditions.checkArgument(selfChessType != ChessType.EMPTY);
        double selfValue = evaluateChessboardValueByChessType(chessboard, selfChessType);
        double opponentValue = evaluateChessboardValueByChessType(chessboard, GameReferee.nextChessType(selfChessType));
        if (Double.compare(selfValue, opponentValue) >= 0) {
            return selfValue;
        } else {
            return -opponentValue;
        }
    }

    public static final double evaluateChessboardValueByChessType(Chessboard chessboard, ChessType chessType) {
        double value = 0;
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                if (chessboard.getChess(i, j) == chessType) {
                    value += evaluatePointValueByChessType(chessboard, new Point(i, j), chessType);
                }
            }
        }
        return value;
    }

    public static final double evaluatePointValue(Chessboard chessboard, Point point) {
        return evaluatePointValueByChessType(chessboard, point, ChessType.BLACK)
                + evaluatePointValueByChessType(chessboard, point, ChessType.WHITE);
    }

    public static final double evaluatePointValueByChessType(Chessboard chessboard, Point point, ChessType chessType) {
        List<PatternType> patternTypeList = GlobalAnalyser.getPatternStatistics(chessboard, point, chessType);
        return evaluateChessPatternType(patternTypeList);
    }

    public static final double evaluateChessPatternType(List<PatternType> patternTypeList) {
        double value = 0;
        for (PatternType patternType : patternTypeList) {
            value += ProphetConst.EVALUATION.get(patternType);
        }
        return value;
    }

}
