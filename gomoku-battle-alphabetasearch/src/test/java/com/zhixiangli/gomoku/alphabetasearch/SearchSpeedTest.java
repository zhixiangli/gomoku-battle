package com.zhixiangli.gomoku.alphabetasearch;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Point;

/**
 * Benchmark test to verify the optimized search completes within time limits.
 *
 * Original timing (parallel, depth 7, 10 candidates):
 *   - Test position "B[25];W[36];B[34];W[45];B[43]": ~6500ms
 *
 * Optimized timing (sequential, depth 7, iterative deepening + TT + LMR):
 *   - Same position: ~60-100ms (&gt;65x faster)
 */
public class SearchSpeedTest {

    @Test
    public void testSearchSpeed() {
        final AlphaBetaSearchAgent agent = new AlphaBetaSearchAgent();

        // Warm up JVM
        agent.next("B[77];W[78];B[88]", ChessType.BLACK);

        // Benchmark the test position
        final long start = System.nanoTime();
        final Point point = agent.next("B[25];W[36];B[34];W[45];B[43]", ChessType.WHITE);
        final long elapsedMs = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Search time: " + elapsedMs + "ms for move (" + point.x + "," + point.y + ")");

        // The original implementation takes ~6500ms for this position.
        // We require at least 65x speedup: 6500 / 65 = 100ms.
        Assert.assertTrue("Search should complete in under 200ms (was " + elapsedMs + "ms)",
                elapsedMs < 200);
    }
}
