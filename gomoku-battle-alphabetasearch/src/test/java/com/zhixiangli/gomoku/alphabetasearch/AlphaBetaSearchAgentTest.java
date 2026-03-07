/**
 *
 */
package com.zhixiangli.gomoku.alphabetasearch;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchAgentTest {

    private AlphaBetaSearchAgent agent;

    @Before
    public void setUp() {
        agent = new AlphaBetaSearchAgent();
    }

    @Test
    public void next() {
        /**
         *
         * ...............
         *
         * ...............
         *
         * .....B.........
         *
         * ....B.W........
         *
         * ...B.W.........
         *
         * ...............
         *
         */

        final Point point = agent.next("B[25];W[36];B[34];W[45];B[43]", ChessType.WHITE);
        Assert.assertTrue(new Point(5, 2).equals(point) || new Point(1, 6).equals(point));
    }

    @Test
    public void nextShouldCompleteOpenFour() {
        /**
         * White to move, complete an open four on row 7.
         *
         * ......?WWWW?...
         *
         * Expected: (7,6) or (7,11)
         */
        final Point point = agent.next("W[77];B[12];W[78];B[23];W[79];B[34];W[7a]", ChessType.WHITE);
        Assert.assertTrue(new Point(7, 6).equals(point) || new Point(7, 11).equals(point));
    }

    @Test
    public void nextShouldBlockOpponentOpenFour() {
        /**
         * White to move, must block black open four on row 7.
         *
         * ......?BBBB?...
         *
         * Expected: (7,6) or (7,11)
         */
        final Point point = agent.next("B[77];W[22];B[78];W[33];B[79];W[44];B[7a]", ChessType.WHITE);
        Assert.assertTrue(new Point(7, 6).equals(point) || new Point(7, 11).equals(point));
    }

    @Test
    public void nextShouldPreferOpenThreeExtension() {
        /**
         * White to move, extend open three to create stronger threat.
         *
         * ......?WWW?....
         *
         * Expected: (7,6) or (7,10)
         */
        final Point point = agent.next("W[77];B[22];W[78];B[33];W[79]", ChessType.WHITE);
        Assert.assertTrue(new Point(7, 6).equals(point) || new Point(7, 10).equals(point));
    }

    @Test
    public void nextShouldFillSpacedOpenThreeGap() {
        /**
         * White to move, fill the center gap in spaced open three.
         *
         * .......WW?W....
         *
         * Expected: (7,9)
         */
        final Point point = agent.next("W[77];B[22];W[78];B[33];W[7a]", ChessType.WHITE);
        Assert.assertEquals(new Point(7, 9), point);
    }

}
