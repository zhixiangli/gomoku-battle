/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.base.Stopwatch;
import com.zhixiangli.gomoku.agent.alphabetasearch.algorithm.AlphaBetaSearchAlgorithm;
import com.zhixiangli.gomoku.agent.alphabetasearch.common.SearchConst;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.console.ConsoleAgent;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchAgent extends ConsoleAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlphaBetaSearchAgent.class);

    private Chessboard chessboard;

    private AlphaBetaSearchAlgorithm alphaBetaAlgorithm;

    public AlphaBetaSearchAgent() {
        this.alphaBetaAlgorithm = new AlphaBetaSearchAlgorithm();
    }

    @Override
    protected void clear() {
        this.chessboard = null;
    }

    @Override
    protected Point next(ChessType chessType) {
        Point[] candidates = alphaBetaAlgorithm.nextMoves(chessboard, chessType);

        if (ArrayUtils.getLength(candidates) == 0) {
            int x = (int) (GomokuConst.RANDOM.nextGaussian() + GomokuConst.CHESSBOARD_SIZE * 0.5);
            int y = (int) (GomokuConst.RANDOM.nextGaussian() + GomokuConst.CHESSBOARD_SIZE * 0.5);
            return new Point(Math.min(Math.max(x, 0), GomokuConst.CHESSBOARD_SIZE - 1),
                    Math.min(Math.max(y, 0), GomokuConst.CHESSBOARD_SIZE - 1));
        } else {
            Point point = null;
            Stopwatch watch = Stopwatch.createStarted();
            point = this.searchBestPoint(candidates, chessType, SearchConst.MAX_DEPTH);
            LOGGER.info("alpha beta search cost: {}", watch.elapsed(TimeUnit.SECONDS));
            return point;
        }

    }

    private Point searchBestPoint(Point[] candidates, ChessType chessType, int depth) {
        this.alphaBetaAlgorithm.clearCache();
        List<Pair<Point, Double>> pairs = Stream.of(candidates).parallel().map(point -> {
            Chessboard newChessboard = chessboard.clone();
            // set chessboard.
            newChessboard.setChess(point, chessType);
            double value = -Double.MAX_VALUE;
            try {
                value = alphaBetaAlgorithm.search(depth, -Double.MAX_VALUE, Double.MAX_VALUE, newChessboard, point,
                        chessType, chessType, StringUtils.EMPTY);
            } catch (Exception e) {
                LOGGER.error("alpha beta search error: {}", e);
            }
            // unset chessboard.
            newChessboard.setChess(point, ChessType.EMPTY);
            Preconditions.checkState(newChessboard.equals(chessboard));
            return ImmutablePair.of(point, value);
        }).collect(Collectors.toList());

        double bestValue = pairs.stream().map(pair -> pair.getValue()).max((a, b) -> Double.compare(a, b)).get();
        List<Pair<Point, Double>> resultPoints = pairs.stream()
                .filter(pair -> Double.compare(bestValue, pair.getValue()) == 0).collect(Collectors.toList());
        return resultPoints.get(RandomUtils.nextInt(0, resultPoints.size())).getKey();
    }

    @Override
    protected void reset(Chessboard chessboard) {
        this.chessboard = chessboard;
    }

    @Override
    protected void play(ChessType chessType, Point point) {
    }

    public static void main(String[] args) {
        ConsoleAgent agent = new AlphaBetaSearchAgent();
        agent.start();
    }

}
