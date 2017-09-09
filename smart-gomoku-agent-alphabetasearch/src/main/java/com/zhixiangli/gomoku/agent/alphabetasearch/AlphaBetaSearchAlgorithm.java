/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchAlgorithm {

    private static final Cache<TableNode, Double> SEARCH_CACHE = CacheBuilder.newBuilder()
            .maximumSize(AlphaBetaSearchConst.Cache.MAXIMUM_SIZE)
            .expireAfterAccess(AlphaBetaSearchConst.Cache.DURATION, TimeUnit.MINUTES).build();

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
    public final double search(int depth, double alpha, double beta, Chessboard chessboard, Point point,
            ChessType chessType) throws ExecutionException {
        Preconditions.checkArgument(GameReferee.isInChessboard(point));
        Preconditions.checkArgument(chessboard.getChess(point) == ChessType.EMPTY);
        Preconditions.checkArgument(chessType != ChessType.EMPTY);

        chessboard.setChess(point, chessType);

        TableNode key = new TableNode(depth, chessboard);
        Double value = SEARCH_CACHE.get(key, () -> {
            double result = 0;
            if (GameReferee.isWin(chessboard, point)) {
                if ((depth & 1) == 0) {
                    result = AlphaBetaSearchConst.Estimate.WIN;
                } else {
                    result = -AlphaBetaSearchConst.Estimate.WIN;
                }
            } else if (depth >= AlphaBetaSearchConst.Search.MAX_DEPTH) {
                result = AlphaBetaSearchProphet.estimateChessboardValue(chessboard,
                        (depth & 1) == 0 ? chessType : GameReferee.nextChessType(chessType));
            } else {
                ChessType nextChessType = GameReferee.nextChessType(chessType);
                List<Point> candidateMoves = this.nextMoves(chessboard, nextChessType);
                double newAlpha = alpha;
                double newBeta = beta;
                for (Point nextPoint : candidateMoves) {
                    double searchValue = search(depth + 1, newAlpha, newBeta, chessboard, nextPoint, nextChessType);
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
            return result * AlphaBetaSearchConst.Search.DECAY_FACTOR;
        });
        chessboard.setChess(point, ChessType.EMPTY);
        return value;
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

class TableNode {
    int searchDepth;
    Pair<Long, Long> tableHashCode;

    public TableNode(int searchDepth, Chessboard chessboard) {
        this.searchDepth = searchDepth;
        this.tableHashCode = hashChessboard(chessboard);
    }

    private Pair<Long, Long> hashChessboard(Chessboard chessboard) {
        long result1 = 1;
        long result2 = 1;
        int prime1 = 13;
        int prime2 = 11;
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                int type = chessboard.getChess(i, j).ordinal();
                result1 = result1 * prime1 + type;
                result2 = result2 * prime2 + type;
            }
        }
        return ImmutablePair.of(result1, result2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + searchDepth;
        result = prime * result + ((tableHashCode == null) ? 0 : tableHashCode.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof TableNode)) {
            return false;
        }
        TableNode other = (TableNode) obj;
        if (searchDepth != other.searchDepth) {
            return false;
        }
        if (tableHashCode == null) {
            if (other.tableHashCode != null) {
                return false;
            }
        } else if (!tableHashCode.equals(other.tableHashCode)) {
            return false;
        }
        return true;
    }

}
