/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.agent.alphabetasearch.algorithm.AlphaBetaSearchAlgorithm;
import com.zhixiangli.gomoku.agent.alphabetasearch.common.ProphetConst;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.chessboard.PatternType;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.console.ConsoleAgent;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchAgent extends ConsoleAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlphaBetaSearchAgentTest.class);

    private Chessboard chessboard;

    private AlphaBetaSearchAlgorithm alphaBetaAlgorithm;

    public AlphaBetaSearchAgent() {
        super();
        this.alphaBetaAlgorithm = new AlphaBetaSearchAlgorithm();
    }

    @Override
    protected void clear() {
        this.chessboard = null;
    }

    @Override
    protected Point next(ChessType chessType) {
        LOGGER.info("start the next turn. current chess type {}\n{}", chessType, chessboard);
        List<Point> candidatePoints = alphaBetaAlgorithm.nextMoves(chessboard, chessType);

        if (candidatePoints.isEmpty()) {
            return new Point(GomokuConst.CHESSBOARD_SIZE / 2, GomokuConst.CHESSBOARD_SIZE / 2);
        }
        List<Pair<Point, Double>> bestPoints = candidatePoints.parallelStream().map(point -> {
            Chessboard newChessboard = chessboard.clone();
            double maxValue = ProphetConst.EVALUATION.get(PatternType.FIVE);
            double value = -maxValue;
            try {
                value = alphaBetaAlgorithm.search(0, -maxValue, maxValue, newChessboard, point,
                        chessType);
            } catch (ExecutionException e) {
                LOGGER.error("alpha beta search error {}", e);
            }
            Preconditions.checkState(newChessboard.equals(chessboard));
            return ImmutablePair.of(point, value);
        }).collect(Collectors.toList());

        double bestValue = bestPoints.stream().map(pair -> pair.getValue()).max((a, b) -> Double.compare(a, b)).get();
        List<Pair<Point, Double>> resultPoints = bestPoints.stream()
                .filter(pair -> Double.compare(bestValue, pair.getValue()) == 0).collect(Collectors.toList());
        return resultPoints.get(RandomUtils.nextInt(0, resultPoints.size())).getKey();
    }

    @Override
    protected void show(Chessboard chessboard) {
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
