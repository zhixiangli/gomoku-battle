package com.zhixiangli.gomoku.alphabetasearch;

import com.google.common.base.Stopwatch;
import com.zhixiangli.gomoku.alphabetasearch.algorithm.AlphaBetaSearchAlgorithm;
import com.zhixiangli.gomoku.alphabetasearch.common.SearchConst;
import com.zhixiangli.gomoku.console.ConsoleAgent;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.common.GomokuFormatter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * @author zhixiangli
 */
public class AlphaBetaSearchAgent extends ConsoleAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlphaBetaSearchAgent.class);

    private final AlphaBetaSearchAlgorithm alphaBetaAlgorithm;

    public AlphaBetaSearchAgent() {
        alphaBetaAlgorithm = new AlphaBetaSearchAlgorithm();
    }

    @Override
    protected Point next(final String sgf, final ChessType chessType) {
        final Chessboard chessboard = GomokuFormatter.toChessboard(sgf);
        final Point[] candidates = alphaBetaAlgorithm.nextMoves(chessboard);

        if (ArrayUtils.getLength(candidates) == 0) {
            final int x = (int) (GomokuConst.RANDOM.nextGaussian() + (GomokuConst.CHESSBOARD_SIZE * 0.5));
            final int y = (int) (GomokuConst.RANDOM.nextGaussian() + (GomokuConst.CHESSBOARD_SIZE * 0.5));
            return new Point(Math.min(Math.max(x, 0), GomokuConst.CHESSBOARD_SIZE - 1),
                    Math.min(Math.max(y, 0), GomokuConst.CHESSBOARD_SIZE - 1));
        } else {
            final Stopwatch watch = Stopwatch.createStarted();
            final Point point = searchBestPoint(candidates, chessboard, chessType);
            LOGGER.info("alpha beta search cost: {}", watch.elapsed(TimeUnit.SECONDS));
            return point;
        }

    }

    /**
     * Use iterative deepening: search from depth 1 up to MAX_DEPTH.
     * Each iteration reorders root candidates by scores from the previous iteration,
     * and the transposition table carries forward entries for better move ordering
     * at deeper levels.
     */
    private Point searchBestPoint(final Point[] initialCandidates, final Chessboard chessboard,
                                  final ChessType chessType) {
        alphaBetaAlgorithm.clearCache();

        Point[] candidates = initialCandidates;
        List<Pair<Point, Double>> pairs = null;

        for (int depth = 1; depth <= SearchConst.MAX_DEPTH; depth++) {
            // Reorder candidates based on previous iteration's scores (best first)
            if (pairs != null) {
                pairs.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
                candidates = pairs.stream().map(Pair::getKey).toArray(Point[]::new);
            }

            pairs = new ArrayList<>();
            for (final Point point : candidates) {
                chessboard.setChess(point, chessType);
                double value = -Double.MAX_VALUE;
                try {
                    value = alphaBetaAlgorithm.search(depth, -Double.MAX_VALUE, Double.MAX_VALUE,
                            chessboard, point, chessType, chessType, StringUtils.EMPTY);
                } catch (final Exception e) {
                    LOGGER.error("alpha beta search error", e);
                }
                chessboard.setChess(point, ChessType.EMPTY);
                pairs.add(ImmutablePair.of(point, value));
            }
        }

        final double bestValue = pairs.stream().map(Pair::getValue).max(Double::compare).get();
        final List<Pair<Point, Double>> resultPoints = new ArrayList<>();
        for (final Pair<Point, Double> pair : pairs) {
            if (Double.compare(bestValue, pair.getValue()) == 0) {
                resultPoints.add(pair);
            }
        }
        return resultPoints.get(ThreadLocalRandom.current().nextInt(resultPoints.size())).getKey();
    }

    public static void main(final String[] args) {
        final ConsoleAgent agent = new AlphaBetaSearchAgent();
        agent.start();
    }

}
