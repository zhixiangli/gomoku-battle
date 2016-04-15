/**
 * 
 */
package com.zhixiangli.smartgomoku.common;

import static org.junit.Assert.*;

import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

/**
 * TODO
 * 
 * @author lizhixiang
 * @date 2015年6月6日
 */
public class GomokuRefereeTest {

    private Chessboard chessboard;

    /**
     * TODO
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
	this.chessboard = new Chessboard();
	this.chessboard.setChess(0, 0, ChessType.BLACK);
	this.chessboard.setChess(0, 1, ChessType.BLACK);
	this.chessboard.setChess(0, 2, ChessType.BLACK);
	this.chessboard.setChess(0, 3, ChessType.BLACK);
	this.chessboard.setChess(0, 4, ChessType.BLACK);

	this.chessboard.setChess(6, 6, ChessType.WHITE);
	this.chessboard.setChess(7, 7, ChessType.WHITE);

	this.chessboard.setChess(8, 8, ChessType.BLACK);
	this.chessboard.setChess(9, 9, ChessType.BLACK);
	this.chessboard.setChess(10, 10, ChessType.BLACK);
	this.chessboard.setChess(11, 11, ChessType.BLACK);

    }

    @Test
    public void test() {
	assertEquals(true, GomokuReferee.isWin(chessboard, new Point(0, 0)));
	assertEquals(true, GomokuReferee.isWin(chessboard, new Point(0, 1)));
	assertEquals(true, GomokuReferee.isWin(chessboard, new Point(0, 2)));
	assertEquals(true, GomokuReferee.isWin(chessboard, new Point(0, 3)));
	assertEquals(true, GomokuReferee.isWin(chessboard, new Point(0, 4)));

	assertEquals(false, GomokuReferee.isWin(chessboard, new Point(6, 6)));
	assertEquals(false, GomokuReferee.isWin(chessboard, new Point(7, 7)));
	assertEquals(false, GomokuReferee.isWin(chessboard, new Point(8, 8)));
	assertEquals(false, GomokuReferee.isWin(chessboard, new Point(9, 9)));
	assertEquals(false, GomokuReferee.isWin(chessboard, new Point(10, 10)));
	assertEquals(false, GomokuReferee.isWin(chessboard, new Point(11, 11)));

    }

}
