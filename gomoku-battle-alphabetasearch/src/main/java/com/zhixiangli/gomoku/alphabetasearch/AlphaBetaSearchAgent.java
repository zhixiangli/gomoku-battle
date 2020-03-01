package com.zhixiangli.gomoku.alphabetasearch;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.zhixiangli.gomoku.alphabetasearch.algorithm.AlphaBetaSearchAlgorithm;
import com.zhixiangli.gomoku.alphabetasearch.common.SearchConst;
import com.zhixiangli.gomoku.console.ConsoleAgent;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.common.GomokuFormatter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        final Point[] candidates = alphaBetaAlgorithm.nextMoves(chessboard, chessType);

        if (ArrayUtils.getLength(candidates) == 0) {
            final int x = (int) (GomokuConst.RANDOM.nextGaussian() + (GomokuConst.CHESSBOARD_SIZE * 0.5));
            final int y = (int) (GomokuConst.RANDOM.nextGaussian() + (GomokuConst.CHESSBOARD_SIZE * 0.5));
            return new Point(Math.min(Math.max(x, 0), GomokuConst.CHESSBOARD_SIZE - 1),
                    Math.min(Math.max(y, 0), GomokuConst.CHESSBOARD_SIZE - 1));
        } else {
            final Stopwatch watch = Stopwatch.createStarted();
            final Point point = searchBestPoint(candidates, chessboard, chessType, SearchConst.MAX_DEPTH);
            LOGGER.info("alpha beta search cost: {}", watch.elapsed(TimeUnit.SECONDS));
            return point;
        }

    }

    private Point searchBestPoint(final Point[] candidates, final Chessboard chessboard, final ChessType chessType, final int depth) {
        alphaBetaAlgorithm.clearCache();
        final List<Pair<Point, Double>> pairs = Stream.of(candidates).parallel().map(point -> {
            final Chessboard newChessboard = chessboard.clone();
            // set chessboard.
            newChessboard.setChess(point, chessType);
            double value = -Double.MAX_VALUE;
            try {
                value = alphaBetaAlgorithm.search(depth, -Double.MAX_VALUE, Double.MAX_VALUE, newChessboard, point,
                        chessType, chessType, StringUtils.EMPTY);
            } catch (Exception e) {
                LOGGER.error("alpha beta search error", e);
            }
            // unset chessboard.
            newChessboard.setChess(point, ChessType.EMPTY);
            Preconditions.checkState(newChessboard.equals(chessboard));
            return ImmutablePair.of(point, value);
        }).collect(Collectors.toList());

        final double bestValue = pairs.stream().map(Pair::getValue).max(Double::compare).get();
        final List<Pair<Point, Double>> resultPoints = pairs.stream()
                .filter(pair -> Double.compare(bestValue, pair.getValue()) == 0).collect(Collectors.toList());
        return resultPoints.get(RandomUtils.nextInt(0, resultPoints.size())).getKey();
    }

    public static void main(final String[] args) {
        final ConsoleAgent agent = new AlphaBetaSearchAgent();
        agent.start();
    }

}
