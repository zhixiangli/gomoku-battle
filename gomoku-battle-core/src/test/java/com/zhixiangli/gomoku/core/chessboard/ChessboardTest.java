/**
 *
 */
package com.zhixiangli.gomoku.core.chessboard;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author lizhixiang
 */
public class ChessboardTest {

    private Chessboard chessboard;

    @Before
    public void setUp() {
        chessboard = new Chessboard();
    }

    @Test
    public void test() {
        chessboard.setChess(0, 1, ChessType.BLACK);
        chessboard.setChess(14, 1, ChessType.WHITE);
        chessboard.setChess(3, 1, ChessType.BLACK);
        Assert.assertEquals(ChessType.BLACK, chessboard.getChess(0, 1));
        Assert.assertEquals(ChessType.BLACK, chessboard.getChess(3, 1));
        Assert.assertEquals(ChessType.WHITE, chessboard.getChess(14, 1));
        Assert.assertEquals(ChessType.EMPTY, chessboard.getChess(10, 1));
        Assert.assertEquals(ChessType.EMPTY, chessboard.getChess(0, 11));

        chessboard.clear();
        Assert.assertEquals(ChessType.EMPTY, chessboard.getChess(0, 1));
        Assert.assertEquals(ChessType.EMPTY, chessboard.getChess(3, 1));
        Assert.assertEquals(ChessType.EMPTY, chessboard.getChess(14, 1));
        Assert.assertEquals(ChessType.EMPTY, chessboard.getChess(10, 1));
        Assert.assertEquals(ChessType.EMPTY, chessboard.getChess(0, 11));
    }

}
