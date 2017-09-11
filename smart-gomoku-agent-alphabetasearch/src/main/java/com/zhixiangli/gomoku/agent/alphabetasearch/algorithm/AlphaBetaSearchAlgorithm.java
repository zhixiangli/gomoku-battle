/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch.algorithm;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zhixiangli.gomoku.agent.alphabetasearch.common.CacheConst;
import com.zhixiangli.gomoku.agent.alphabetasearch.common.ProphetConst;
import com.zhixiangli.gomoku.agent.alphabetasearch.common.SearchUtils;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.analysis.PatternType;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchAlgorithm {

    private Cache<String, Double> seachCache;

    private boolean isEnableCache;

    public AlphaBetaSearchAlgorithm() {
        this(true);
    }

    public AlphaBetaSearchAlgorithm(boolean isEnableCache) {
        this.isEnableCache = isEnableCache;
        this.seachCache = CacheBuilder.newBuilder().maximumSize(CacheConst.MAXIMUM_SIZE)
                .expireAfterAccess(CacheConst.DURATION, TimeUnit.MINUTES).build();
    }

    public final double clearCacheAndSearch(int depth, double alpha, double beta, Chessboard chessboard, Point point,
            ChessType currentChessType, ChessType rootChessType, String path) throws Exception {
        this.clearCache();
        return search(depth, alpha, beta, chessboard, point, currentChessType, rootChessType, path);
    }

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
     *            the point has been put.
     * @param currentChessType
     *            the chess type has been put.
     * @return
     * @throws Exception
     */
    public final double search(int depth, double alpha, double beta, Chessboard chessboard, Point point,
            ChessType currentChessType, ChessType rootChessType, String path) throws Exception {
        Preconditions.checkArgument(GameReferee.isInChessboard(point));
        Preconditions.checkArgument(chessboard.getChess(point) != ChessType.EMPTY);
        Preconditions.checkArgument(currentChessType != ChessType.EMPTY);

        String currentPath = path + SearchUtils.encodePoint(point);
        Callable<Double> callable = () -> {
            double result = 0;
            if (GameReferee.isWin(chessboard, point)) {
                double maxValue = ProphetConst.EVALUATION.get(PatternType.FIVE);
                result = rootChessType == currentChessType ? maxValue : -maxValue;
            } else if (depth <= 0) {
                result = AlphaBetaSearchProphet.evaluateChessboardValue(chessboard, rootChessType);
            } else {
                ChessType nextChessType = GameReferee.nextChessType(currentChessType);
                List<Point> candidateMoves = nextMoves(chessboard, nextChessType);
                double newAlpha = alpha, newBeta = beta;
                for (Point nextPoint : candidateMoves) {
                    // set chessboard.
                    chessboard.setChess(nextPoint, nextChessType);
                    double searchValue = search(depth - 1, newAlpha, newBeta, chessboard, nextPoint, nextChessType,
                            rootChessType, currentPath);
                    // unset chessboard.
                    chessboard.setChess(nextPoint, ChessType.EMPTY);
                    if (rootChessType == currentChessType) {
                        result = newBeta = Math.min(newBeta, searchValue);
                    } else {
                        result = newAlpha = Math.max(newAlpha, searchValue);
                    }
                    if (newBeta <= newAlpha) {
                        break;
                    }
                }
            }
            return result * SearchUtils.DECAY_FACTOR;
        };
        if (this.isEnableCache) {
            return seachCache.get(currentPath, callable);
        } else {
            return callable.call();
        }
    }

    public List<Point> nextMoves(Chessboard chessboard, ChessType chessType) {
        List<Point> emptyPointList = new ArrayList<>(
                GlobalAnalyser.getEmptyPointsAround(chessboard, SearchUtils.AROUND_CANDIDATE_RANGE));
        return emptyPointList.stream().map(point -> ImmutablePair.of(point, evaluateValue(chessboard, point)))
                .sorted((a, b) -> b.getValue() - a.getValue()).limit(SearchUtils.MAX_CANDIDATE_NUM)
                .map(pair -> pair.getKey()).collect(Collectors.toList());
    }

    public int evaluateValue(Chessboard chessboard, Point point) {
        return AlphaBetaSearchProphet.evaluatePointValue(chessboard, point);
    }

    public void clearCache() {
        this.seachCache.invalidateAll();
        Preconditions.checkState(this.seachCache.size() == 0);
    }

}
