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

}
