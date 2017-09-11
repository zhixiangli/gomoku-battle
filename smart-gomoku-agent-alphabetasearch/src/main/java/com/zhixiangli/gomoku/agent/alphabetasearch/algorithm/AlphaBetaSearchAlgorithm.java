/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch.algorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zhixiangli.gomoku.agent.alphabetasearch.common.CacheConst;
import com.zhixiangli.gomoku.agent.alphabetasearch.common.ProphetConst;
import com.zhixiangli.gomoku.agent.alphabetasearch.common.SearchConst;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.chessboard.PatternType;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchAlgorithm {

    private static final Cache<AlphaBetaSearchTable, Integer> SEARCH_CACHE = CacheBuilder.newBuilder()
            .maximumSize(CacheConst.MAXIMUM_SIZE).expireAfterAccess(CacheConst.DURATION, TimeUnit.MINUTES).build();

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
     * @throws ExecutionException
     */
    public final int search(int depth, int alpha, int beta, Chessboard chessboard, Point point, ChessType chessType)
            throws ExecutionException {
        Preconditions.checkArgument(GameReferee.isInChessboard(point));
        Preconditions.checkArgument(chessboard.getChess(point) == ChessType.EMPTY);
        Preconditions.checkArgument(chessType != ChessType.EMPTY);

        chessboard.setChess(point, chessType);

        AlphaBetaSearchTable key = new AlphaBetaSearchTable(depth, chessboard);
        int value = SEARCH_CACHE.get(key, () -> {
            int result = 0;
            if (GameReferee.isWin(chessboard, point)) {
                int searchValue = ProphetConst.EVALUATION.get(PatternType.FIVE);
                result = (depth & 1) == 0 ? searchValue : -searchValue;
            } else if (depth >= SearchConst.MAX_DEPTH) {
                result = AlphaBetaSearchProphet.evaluateChessboardValue(chessboard,
                        (depth & 1) == 0 ? chessType : GameReferee.nextChessType(chessType));
            } else {
                ChessType nextChessType = GameReferee.nextChessType(chessType);
                List<Point> candidateMoves = this.nextMoves(chessboard, nextChessType);
                int newAlpha = alpha;
                int newBeta = beta;
                for (Point nextPoint : candidateMoves) {
                    int searchValue = search(depth + 1, newAlpha, newBeta, chessboard, nextPoint, nextChessType);
                    if ((depth & 1) == 0) {
                        result = newBeta = Math.min(newBeta, searchValue);
                    } else {
                        result = newAlpha = Math.max(newAlpha, searchValue);
                    }
                    if (newBeta <= newAlpha) {
                        break;
                    }
                }
            }
            return (int) (result * SearchConst.DECAY_FACTOR);
        });
        chessboard.setChess(point, ChessType.EMPTY);
        return value;
    }

    public List<Point> nextMoves(Chessboard chessboard, ChessType chessType) {
        List<Point> emptyPointList = new ArrayList<>(
                GlobalAnalyser.getEmptyPointsAround(chessboard, SearchConst.AROUND_CANDIDATE_RANGE));
        Collections.shuffle(emptyPointList);
        return emptyPointList.stream().map(point -> ImmutablePair.of(point, evaluateValue(chessboard, point)))
                .sorted((a, b) -> b.getValue() - a.getValue()).limit(SearchConst.MAX_CANDIDATE_NUM)
                .map(pair -> pair.getKey()).collect(Collectors.toList());
    }

    public int evaluateValue(Chessboard chessboard, Point point) {
        return AlphaBetaSearchProphet.evaluatePointValue(chessboard, point);
    }

}
