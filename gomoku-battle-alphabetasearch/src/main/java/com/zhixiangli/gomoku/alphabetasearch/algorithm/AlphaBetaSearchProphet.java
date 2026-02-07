package com.zhixiangli.gomoku.alphabetasearch.algorithm;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.alphabetasearch.common.ProphetConst;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.analysis.PatternType;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;

import java.awt.Point;
import java.util.Map;

/**
 * @author zhixiangli
 */
public class AlphaBetaSearchProphet {

    private AlphaBetaSearchProphet() {
    }

    public static double evaluateChessboardValue(final Chessboard chessboard, final ChessType selfChessType) {
        Preconditions.checkArgument(selfChessType != ChessType.EMPTY);
        final double selfValue = evaluateChessboardValueByChessType(chessboard, selfChessType);
        final double opponentValue = evaluateChessboardValueByChessType(chessboard, GameReferee.nextChessType(selfChessType));
        if (selfValue > opponentValue) {
            return selfValue;
        } else {
            return -opponentValue;
        }
    }

    public static double evaluateChessboardValueByChessType(final Chessboard chessboard, final ChessType chessType) {
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

    public static double evaluatePointValue(final Chessboard chessboard, final Point point) {
        return evaluatePointValueByChessType(chessboard, point, ChessType.BLACK)
                + evaluatePointValueByChessType(chessboard, point, ChessType.WHITE);
    }

    public static double evaluatePointValueByChessType(final Chessboard chessboard, final Point point, final ChessType chessType) {
        final Map<PatternType, Integer> counter = GlobalAnalyser.getPatternStatistics(chessboard, point, chessType);
        return evaluateChessPatternType(counter);
    }

    public static double evaluateChessPatternType(final Map<PatternType, Integer> counter) {
        double value = 0;
        // single pattern.
        for (final Map.Entry<PatternType, Integer> entry : counter.entrySet()) {
            value += ProphetConst.EVALUATION.get(entry.getKey()) * entry.getValue();
        }

        // special combination.
        final int halfOpenFour = counter.getOrDefault(PatternType.HALF_OPEN_FOUR, 0);
        // half open four + half open four
        if (halfOpenFour > 1) {
            value += ProphetConst.EVALUATION.get(PatternType.OPEN_FOUR) * halfOpenFour;
        }
        // half open four + open three
        final int openThree = counter.getOrDefault(PatternType.OPEN_THREE, 0)
                + counter.getOrDefault(PatternType.SPACED_OPEN_THREE, 0);
        if ((halfOpenFour > 0) && (openThree > 0)) {
            value += ProphetConst.EVALUATION.get(PatternType.OPEN_FOUR) * (halfOpenFour + openThree);
        }
        // open three + open three
        if (openThree > 1) {
            value += ProphetConst.EVALUATION.get(PatternType.HALF_OPEN_FOUR) * 2 * openThree * openThree;
        }
        // open three + half open three
        final int halfOpenThree = counter.getOrDefault(PatternType.HALF_OPEN_THREE, 0);
        if ((openThree > 0) && (halfOpenThree > 0)) {
            value += ProphetConst.EVALUATION.get(PatternType.HALF_OPEN_FOUR) * 3 * (openThree + halfOpenThree);
        }
        // half open four + half open three
        if ((halfOpenFour > 0) && (halfOpenThree > 0)) {
            value += ProphetConst.EVALUATION.get(PatternType.HALF_OPEN_FOUR) * 3 * (halfOpenFour + halfOpenThree);
        }
        // half open three + half open three
        if (halfOpenThree > 1) {
            value += ProphetConst.EVALUATION.get(PatternType.HALF_OPEN_THREE) * 3 * halfOpenThree;
        }
        final int openTwo = counter.getOrDefault(PatternType.OPEN_TWO, 0)
                + counter.getOrDefault(PatternType.ONE_SPACED_OPEN_TWO, 0)
                + counter.getOrDefault(PatternType.TWO_SPACED_OPEN_TWO, 0);
        // open two + open three
        if ((openTwo > 0) && (openThree > 0)) {
            value += ProphetConst.EVALUATION.get(PatternType.HALF_OPEN_FOUR);
        }
        // open two + half open three
        if ((openTwo > 0) && (halfOpenThree > 0)) {
            value += ProphetConst.EVALUATION.get(PatternType.HALF_OPEN_THREE) * (openTwo + halfOpenThree);
        }
        // open two + open two
        if (openTwo > 1) {
            value += ProphetConst.EVALUATION.get(PatternType.HALF_OPEN_THREE) * openTwo * openTwo;
        }
        Preconditions.checkState(value >= 0);
        return value;
    }

}
