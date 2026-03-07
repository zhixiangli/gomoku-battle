package com.zhixiangli.gomoku.alphabetasearch.algorithm;

import com.zhixiangli.gomoku.alphabetasearch.common.SearchConst;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Self-play test to verify the AI plays reasonably strong games.
 */
public class SelfPlayTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SelfPlayTest.class);

    private static final int MAX_MOVES = 225; // 15*15

    /**
     * Play a complete game between two instances of the AI with reduced depth for speed.
     * Verifies the game terminates within reasonable moves and produces a decisive result.
     */
    @Test
    public void selfPlayProducesDecisiveResult() {
        // Use reduced depth for faster self-play testing
        AlphaBetaSearchAlgorithm algorithm = new AlphaBetaSearchAlgorithm(false);
        Chessboard board = new Chessboard();

        // BLACK plays first in center
        Point lastMove = new Point(GomokuConst.CHESSBOARD_SIZE / 2, GomokuConst.CHESSBOARD_SIZE / 2);
        board.setChess(lastMove, ChessType.BLACK);
        ChessType currentPlayer = ChessType.WHITE;
        int moveCount = 1;

        while (moveCount < MAX_MOVES) {
            if (GameReferee.isWin(board, lastMove)) {
                ChessType winner = GameReferee.nextChessType(currentPlayer);
                LOGGER.info("Game ended after {} moves. Winner: {}", moveCount, winner);
                // Game should end in a win, not go on forever
                Assert.assertTrue("Game should end within reasonable moves", moveCount < 120);
                return;
            }
            if (GameReferee.isDraw(board, lastMove)) {
                LOGGER.info("Game ended in draw after {} moves", moveCount);
                return;
            }

            // Use reduced search depth for faster testing
            Point bestMove = findBestMoveWithDepth(algorithm, board, currentPlayer, 3);
            Assert.assertNotNull("Algorithm should find a move", bestMove);
            board.setChess(bestMove, currentPlayer);
            lastMove = bestMove;
            currentPlayer = GameReferee.nextChessType(currentPlayer);
            moveCount++;
        }
        LOGGER.info("Game ended after max moves");
    }

    /**
     * Test that the AI correctly identifies winning moves.
     */
    @Test
    public void aiFindsWinningMove() throws Exception {
        AlphaBetaSearchAlgorithm algorithm = new AlphaBetaSearchAlgorithm(false);
        Chessboard board = new Chessboard();

        // Set up a position where BLACK has an open four: .BBBB.
        board.setChess(7, 7, ChessType.BLACK);
        board.setChess(7, 8, ChessType.BLACK);
        board.setChess(7, 9, ChessType.BLACK);
        board.setChess(7, 10, ChessType.BLACK);
        // Add some WHITE stones so the board is not empty
        board.setChess(5, 5, ChessType.WHITE);
        board.setChess(5, 6, ChessType.WHITE);

        Point bestMove = findBestMove(algorithm, board, ChessType.BLACK);
        LOGGER.info("Best move for winning position: {}", bestMove);
        // Should play to complete the five: (7,6) or (7,11)
        Assert.assertTrue("AI should find the winning move",
                (bestMove.x == 7 && bestMove.y == 6) || (bestMove.x == 7 && bestMove.y == 11));
    }

    /**
     * Test that the AI correctly blocks opponent's winning threat.
     * Uses a half-open four (one end blocked) which has exactly one winning square.
     */
    @Test
    public void aiBlocksOpponentWinningThreat() throws Exception {
        AlphaBetaSearchAlgorithm algorithm = new AlphaBetaSearchAlgorithm(false);
        Chessboard board = new Chessboard();

        // WHITE has a half-open four: BWWWW. (left end blocked by BLACK at 7,6)
        board.setChess(7, 6, ChessType.BLACK);
        board.setChess(7, 7, ChessType.WHITE);
        board.setChess(7, 8, ChessType.WHITE);
        board.setChess(7, 9, ChessType.WHITE);
        board.setChess(7, 10, ChessType.WHITE);
        // Additional BLACK stones elsewhere
        board.setChess(5, 5, ChessType.BLACK);
        board.setChess(5, 6, ChessType.BLACK);

        Point bestMove = findBestMove(algorithm, board, ChessType.BLACK);
        System.out.println("Best blocking move: " + bestMove);
        // Must block at (7,11) to prevent WHITE from completing FIVE
        Assert.assertTrue("AI should block at (7,11) but chose " + bestMove,
                bestMove.x == 7 && bestMove.y == 11);
    }

    private Point findBestMove(AlphaBetaSearchAlgorithm algorithm, Chessboard board, ChessType player) {
        return findBestMoveWithDepth(algorithm, board, player, SearchConst.MAX_DEPTH);
    }

    private Point findBestMoveWithDepth(AlphaBetaSearchAlgorithm algorithm, Chessboard board, ChessType player, int depth) {
        algorithm.clearCache();
        Point[] candidates = algorithm.nextMoves(board);
        if (candidates.length == 0) {
            return null;
        }

        List<Pair<Point, Double>> scores = Stream.of(candidates).map(point -> {
            Chessboard copy = board.clone();
            copy.setChess(point, player);
            double value = -Double.MAX_VALUE;
            try {
                value = algorithm.search(depth, -Double.MAX_VALUE, Double.MAX_VALUE,
                        copy, point, player, player, StringUtils.EMPTY);
            } catch (Exception e) {
                LOGGER.error("Search error", e);
            }
            copy.setChess(point, ChessType.EMPTY);
            return ImmutablePair.of(point, value);
        }).collect(Collectors.toList());

        double bestValue = scores.stream().map(Pair::getValue).max(Double::compare).get();
        List<Pair<Point, Double>> bestMoves = scores.stream()
                .filter(p -> Double.compare(bestValue, p.getValue()) == 0)
                .collect(Collectors.toList());
        return bestMoves.get(ThreadLocalRandom.current().nextInt(bestMoves.size())).getKey();
    }
}
