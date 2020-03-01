package com.zhixiangli.gomoku.alphabetasearch.algorithm;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zhixiangli.gomoku.alphabetasearch.common.CacheConst;
import com.zhixiangli.gomoku.alphabetasearch.common.ProphetConst;
import com.zhixiangli.gomoku.alphabetasearch.common.SearchConst;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.analysis.PatternType;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.common.GomokuFormatter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

/**
 * @author zhixiangli
 */
public class AlphaBetaSearchAlgorithm {

    private final Cache<String, Double> seachCache;

    private final boolean isEnableCache;

    public AlphaBetaSearchAlgorithm() {
        this(true);
    }

    public AlphaBetaSearchAlgorithm(final boolean isEnableCache) {
        this.isEnableCache = isEnableCache;
        seachCache = CacheBuilder.newBuilder().maximumSize(CacheConst.MAXIMUM_SIZE)
                .expireAfterAccess(CacheConst.DURATION_IN_MINUTE, TimeUnit.MINUTES).build();
    }

    double clearCacheAndSearch(
            final int depth, final double alpha, final double beta,
            final Chessboard chessboard, final Point point, final ChessType currentChessType,
            final ChessType rootChessType, final String path) throws Exception {
        clearCache();
        return search(depth, alpha, beta, chessboard, point, currentChessType, rootChessType, path);
    }

    /**
     * @param depth            the current search depth.
     * @param alpha            the lower bound estimation of root chess type.
     * @param beta             the upper bound estimation of root chess type.
     * @param chessboard       the current chessboard.
     * @param point            the point has been put.
     * @param currentChessType the chess type has been put.
     * @return
     * @throws Exception
     */
    public final double search(
            final int depth, final double alpha, final double beta, final Chessboard chessboard, final Point point,
            final ChessType currentChessType, final ChessType rootChessType, final String path) throws Exception {
        Preconditions.checkArgument(GameReferee.isInChessboard(point));
        Preconditions.checkArgument(chessboard.getChess(point) != ChessType.EMPTY);
        Preconditions.checkArgument(currentChessType != ChessType.EMPTY);

        final String currentPath = path + GomokuFormatter.encodePoint(point);
        final Callable<Double> callable = () -> {
            double result = 0;
            if (GameReferee.isWin(chessboard, point)) {
                final double maxValue = ProphetConst.EVALUATION.get(PatternType.FIVE);
                result = (rootChessType == currentChessType) ? maxValue : -maxValue;
            } else if (depth <= 0) {
                result = AlphaBetaSearchProphet.evaluateChessboardValue(chessboard, rootChessType);
            } else {
                final ChessType nextChessType = GameReferee.nextChessType(currentChessType);
                final Point[] candidateMoves = nextMoves(chessboard, nextChessType);
                double newAlpha = alpha, newBeta = beta;
                for (final Point nextPoint : candidateMoves) {
                    // set chessboard.
                    chessboard.setChess(nextPoint, nextChessType);
                    final double searchValue = search(depth - 1, newAlpha, newBeta, chessboard, nextPoint, nextChessType,
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
            return result * SearchConst.DECAY_FACTOR;
        };
        if (isEnableCache) {
            return seachCache.get(currentPath, callable);
        } else {
            return callable.call();
        }
    }

    public Point[] nextMoves(final Chessboard chessboard, final ChessType chessType) {
        final Point[] candidates = GlobalAnalyser.getEmptyPointsAround(chessboard, SearchConst.AROUND_CANDIDATE_RANGE);
        ArrayUtils.shuffle(candidates, GomokuConst.RANDOM);
        final Stream<Point> candidatesStream = Stream.of(candidates)
                .map(point -> ImmutablePair.of(point, AlphaBetaSearchProphet.evaluatePointValue(chessboard, point)))
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue())).limit(SearchConst.MAX_CANDIDATE_NUM)
                .map(Pair::getKey);
        return candidatesStream.toArray(Point[]::new);
    }

    public void clearCache() {
        seachCache.invalidateAll();
        Preconditions.checkState(seachCache.size() == 0);
    }

}
