/**
 * 
 */
package com.zhixiangli.gomoku.core.analysis;

import java.awt.Point;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuPatternStatistics;

/**
 * @author zhixiangli
 *
 */
public class GlobalAnalyserTest {

    private Chessboard chessboard;

    @Before
    public void setUp() throws Exception {
        this.chessboard = new Chessboard();
    }

    @Test
    public void getEmptyPointsAround() {
        // ...
        // .o.
        // oo.
        // ...
        this.chessboard.setChess(9, 0, ChessType.WHITE);
        this.chessboard.setChess(9, 1, ChessType.WHITE);
        this.chessboard.setChess(8, 1, ChessType.WHITE);
        Set<Point> actual = GlobalAnalyser.getEmptyPointsAround(chessboard, 1);
        Set<Point> expected = new HashSet<>(
                Arrays.asList(new Point(7, 0), new Point(7, 1), new Point(7, 2), new Point(8, 0), new Point(8, 2),
                        new Point(9, 2), new Point(10, 0), new Point(10, 1), new Point(10, 2)));
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getPatternStatistics() {
        // .......B...
        // ......B....
        // .B.W.B.....
        // ..BBB......
        // ..B.BB.....
        // ...BB......
        // .....W..W..
        // ...........
        this.chessboard.setChess(0, 7, ChessType.BLACK);
        this.chessboard.setChess(1, 6, ChessType.BLACK);
        this.chessboard.setChess(2, 1, ChessType.BLACK);
        this.chessboard.setChess(2, 3, ChessType.WHITE);
        this.chessboard.setChess(2, 5, ChessType.BLACK);
        this.chessboard.setChess(3, 2, ChessType.BLACK);
        this.chessboard.setChess(3, 3, ChessType.BLACK);
        this.chessboard.setChess(3, 4, ChessType.BLACK);
        this.chessboard.setChess(4, 2, ChessType.BLACK);
        this.chessboard.setChess(4, 4, ChessType.BLACK);
        this.chessboard.setChess(4, 5, ChessType.BLACK);
        this.chessboard.setChess(5, 3, ChessType.BLACK);
        this.chessboard.setChess(5, 4, ChessType.BLACK);
        this.chessboard.setChess(6, 5, ChessType.WHITE);
        this.chessboard.setChess(6, 8, ChessType.WHITE);
        Point blackPoint = new Point(4, 3);
        GomokuPatternStatistics blackStatistics = GlobalAnalyser.getPatternStatistics(chessboard, blackPoint,
                ChessType.BLACK);
        Assert.assertEquals(ChessType.EMPTY, chessboard.getChess(blackPoint));
        Assert.assertEquals(blackStatistics.getFive(), 1);
        Assert.assertEquals(blackStatistics.getOpenFour(), 1);
        Assert.assertEquals(blackStatistics.getHalfOpenFour(), 1);
        Assert.assertEquals(blackStatistics.getOpenThree(), 0);
        Assert.assertEquals(blackStatistics.getSpacedOpenThree(), 0);
        Assert.assertEquals(blackStatistics.getHalfOpenThree(), 1);
        Assert.assertEquals(blackStatistics.getOpenTwo(), 0);
        Assert.assertEquals(blackStatistics.getOneSpacedOpenTwo(), 0);
        Assert.assertEquals(blackStatistics.getTwoSpacedOpenTwo(), 0);
        Assert.assertEquals(blackStatistics.getHalfOpenTwo(), 0);
    }

}
