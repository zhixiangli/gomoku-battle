/**
 * 
 */
package com.zhixiangli.gomoku.alphabetasearch.algorithm;

import java.awt.Point;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zhixiangli.gomoku.alphabetasearch.algorithm.AlphaBetaSearchAlgorithm;
import com.zhixiangli.gomoku.alphabetasearch.common.ProphetConst;
import com.zhixiangli.gomoku.alphabetasearch.common.SearchConst;
import com.zhixiangli.gomoku.core.analysis.PatternType;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchAlgorithmTest {

    private AlphaBetaSearchAlgorithm algorithmWithoutCache;

    private AlphaBetaSearchAlgorithm algorithmWithCache;

    private Chessboard chessboard;

    @Before
    public void setUp() throws Exception {
        algorithmWithoutCache = new AlphaBetaSearchAlgorithm(false);
        algorithmWithCache = new AlphaBetaSearchAlgorithm();

        chessboard = new Chessboard();
        chessboard.setChess(7, 7, ChessType.BLACK);
        chessboard.setChess(8, 8, ChessType.BLACK);
    }

    @Test
    public void testDepth() throws Exception {
        Point point = new Point(5, 5);
        ChessType chessType = ChessType.BLACK;
        chessboard.setChess(point, chessType);
        double expected = 3 * ProphetConst.EVALUATION.get(PatternType.SPACED_OPEN_THREE) * SearchConst.DECAY_FACTOR;
        double actual = algorithmWithCache.clearCacheAndSearch(0, -Double.MAX_VALUE, Double.MAX_VALUE, chessboard,
                point, chessType, chessType, StringUtils.EMPTY);
        Assert.assertEquals(expected, actual, 1e-8);

        expected = PatternType.HALF_OPEN_TWO.getChessNum() * ProphetConst.EVALUATION.get(PatternType.HALF_OPEN_TWO)
                * SearchConst.DECAY_FACTOR * SearchConst.DECAY_FACTOR;
        actual = algorithmWithoutCache.clearCacheAndSearch(1, -Double.MAX_VALUE, Double.MAX_VALUE, chessboard, point,
                chessType, chessType, StringUtils.EMPTY);
        Assert.assertEquals(expected, actual, 1e-8);
    }

    @Test
    public void testCache() throws Exception {
        Chessboard newChessboard = chessboard.clone();
        ChessType chessType = ChessType.BLACK;
        for (int depth = 0; depth <= 3; ++depth) {
            for (Point point : Arrays.asList(new Point(5, 5), new Point(7, 6))) {
                newChessboard.setChess(point, chessType);
                double withCache = algorithmWithCache.clearCacheAndSearch(depth, -Double.MAX_VALUE, Double.MAX_VALUE,
                        newChessboard, point, chessType, chessType, StringUtils.EMPTY);
                newChessboard.setChess(point, ChessType.EMPTY);
                Assert.assertEquals(chessboard, newChessboard);

                newChessboard.setChess(point, chessType);
                double withoutCache = algorithmWithoutCache.clearCacheAndSearch(depth, -Double.MAX_VALUE,
                        Double.MAX_VALUE, newChessboard, point, chessType, chessType, StringUtils.EMPTY);
                newChessboard.setChess(point, ChessType.EMPTY);
                Assert.assertEquals(chessboard, newChessboard);

                System.out.println(String.format("depth: %d, with cache %f, without cache %f, point: " + point, depth,
                        withCache, withoutCache));
                Assert.assertEquals(withCache, withoutCache, 1e-8);
            }
        }
    }

}
