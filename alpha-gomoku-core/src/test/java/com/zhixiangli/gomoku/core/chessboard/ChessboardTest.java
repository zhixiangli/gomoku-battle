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
    public void setUp() throws Exception {
        this.chessboard = new Chessboard();
    }

    @Test
    public void test() {
        this.chessboard.setChess(0, 1, ChessType.BLACK);
        this.chessboard.setChess(14, 1, ChessType.WHITE);
        this.chessboard.setChess(3, 1, ChessType.BLACK);
        Assert.assertEquals(ChessType.BLACK, this.chessboard.getChess(0, 1));
        Assert.assertEquals(ChessType.BLACK, this.chessboard.getChess(3, 1));
        Assert.assertEquals(ChessType.WHITE, this.chessboard.getChess(14, 1));
        Assert.assertEquals(ChessType.EMPTY, this.chessboard.getChess(10, 1));
        Assert.assertEquals(ChessType.EMPTY, this.chessboard.getChess(0, 11));

        this.chessboard.clear();
        Assert.assertEquals(ChessType.EMPTY, this.chessboard.getChess(0, 1));
        Assert.assertEquals(ChessType.EMPTY, this.chessboard.getChess(3, 1));
        Assert.assertEquals(ChessType.EMPTY, this.chessboard.getChess(14, 1));
        Assert.assertEquals(ChessType.EMPTY, this.chessboard.getChess(10, 1));
        Assert.assertEquals(ChessType.EMPTY, this.chessboard.getChess(0, 11));
    }

}
