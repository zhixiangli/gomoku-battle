/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchAlgorithm {

    /**
     * 
     * @param depth
     *            the current search depth.
     * @param alpha
     *            the lower bound estimation of root chess type.
     * @param beta
     *            the upper bound estimation of root chess type.
     * @param chessboard
     *            the current chessboard.
     * @param point
     *            the point to be put.
     * @param chessType
     *            the chess type to be put.
     * @return
     */
    public final double search(int depth, double alpha, double beta, Chessboard chessboard, Point point,
            ChessType chessType) {
        Preconditions.checkArgument(GameReferee.isInChessboard(point));
        Preconditions.checkArgument(chessboard.getChess(point) == ChessType.EMPTY);
        Preconditions.checkArgument(chessType != ChessType.EMPTY);

        chessboard.setChess(point, chessType);
        double result = 0;
        if (GameReferee.isWin(chessboard, point)) {
            if ((depth & 1) == 0) {
                result = alpha = AlphaBetaSearchConst.Estimate.WIN;
            } else {
                result = beta = -AlphaBetaSearchConst.Estimate.WIN;
            }
        } else if (depth >= AlphaBetaSearchConst.Search.MAX_DEPTH) {
            result = AlphaBetaSearchProphet.estimateChessboardValue(chessboard,
                    (depth & 1) == 0 ? chessType : GameReferee.nextChessType(chessType));
        } else {
            ChessType nextChessType = GameReferee.nextChessType(chessType);
            List<Point> candidateMoves = this.nextMoves(chessboard, nextChessType);
            for (Point nextPoint : candidateMoves) {
                double searchValue = search(depth + 1, alpha, beta, chessboard, nextPoint, nextChessType);
                if ((depth & 1) == 0) {
                    result = beta = Math.min(beta, searchValue);
                } else {
                    result = alpha = Math.max(alpha, searchValue);
                }
                if (beta <= alpha) {
                    break;
                }
            }
        }
        chessboard.setChess(point, ChessType.EMPTY);
        return result * AlphaBetaSearchConst.Search.DECAY_FACTOR;
    }

    protected List<Point> nextMoves(Chessboard chessboard, ChessType chessType) {
        return GlobalAnalyser.getEmptyPointsAround(chessboard, AlphaBetaSearchConst.Search.AROUND_CANDIDATE_RANGE)
                .stream().map(point -> ImmutablePair.of(point, estimateValue(chessboard, point)))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(AlphaBetaSearchConst.Search.MAX_CANDIDATE_NUM).map(pair -> pair.getKey())
                .collect(Collectors.toList());
    }

    protected double estimateValue(Chessboard chessboard, Point point) {
        return AlphaBetaSearchProphet.estimatePointValue(chessboard, point);
    }

}
