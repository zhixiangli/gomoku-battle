/**
 * 
 */
package com.zhixiangli.smartgomoku.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

/**
 * TODO
 * 
 * @author lizhixiang
 * @date 2015年6月6日
 */
public class ChessboardTest {
    
    private Chessboard chessboard;
    
    /**
     * TODO
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.chessboard = new Chessboard();
    }
    
    @Test
    public void test() {
        this.chessboard.setChess(0, 1, ChessType.BLACK);
        this.chessboard.setChess(14, 1, ChessType.WHITE);
        this.chessboard.setChess(3, 1, ChessType.BLACK);
        
        assertEquals(ChessType.BLACK, this.chessboard.getChess(0, 1));
        assertEquals(ChessType.BLACK, this.chessboard.getChess(3, 1));
        assertEquals(ChessType.WHITE, this.chessboard.getChess(14, 1));
        assertEquals(ChessType.EMPTY, this.chessboard.getChess(10, 1));
        assertEquals(ChessType.EMPTY, this.chessboard.getChess(0, 11));
        assertEquals(2, this.chessboard.getChessTypeCount(ChessType.BLACK));
        assertEquals(1, this.chessboard.getChessTypeCount(ChessType.WHITE));
        
        this.chessboard.clear();
        
        assertEquals(ChessType.EMPTY, this.chessboard.getChess(0, 1));
        assertEquals(ChessType.EMPTY, this.chessboard.getChess(3, 1));
        assertEquals(ChessType.EMPTY, this.chessboard.getChess(14, 1));
        assertEquals(ChessType.EMPTY, this.chessboard.getChess(10, 1));
        assertEquals(ChessType.EMPTY, this.chessboard.getChess(0, 11));
        assertEquals(0, this.chessboard.getChessTypeCount(ChessType.BLACK));
        assertEquals(0, this.chessboard.getChessTypeCount(ChessType.WHITE));
    }
    
}
