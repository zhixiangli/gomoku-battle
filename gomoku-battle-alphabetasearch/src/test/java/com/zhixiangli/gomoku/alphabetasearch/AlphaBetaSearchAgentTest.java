/**
 * 
 */
package com.zhixiangli.gomoku.alphabetasearch;

import java.awt.Point;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zhixiangli.gomoku.alphabetasearch.AlphaBetaSearchAgent;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchAgentTest {

    private AlphaBetaSearchAgent agent;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.agent = new AlphaBetaSearchAgent();
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
        Chessboard chessboard = new Chessboard();
        chessboard.setChess(2, 5, ChessType.BLACK);
        chessboard.setChess(3, 4, ChessType.BLACK);
        chessboard.setChess(3, 6, ChessType.WHITE);
        chessboard.setChess(4, 3, ChessType.BLACK);
        chessboard.setChess(4, 5, ChessType.WHITE);

        Point point = this.agent.next(chessboard, ChessType.WHITE);
        Assert.assertTrue(new Point(5, 2).equals(point) || new Point(1, 6).equals(point));
    }

}
