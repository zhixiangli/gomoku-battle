/**
 * 
 */
package com.zhixiangli.smartgomoku.ai;

import static org.junit.Assert.*;

import java.awt.Point;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.zhixiangli.smartgomoku.ai.alphabeta.GomokuAlphaBetaPruningUtils;
import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

/**
 * TODO
 * 
 * @author lizhixiang
 * @date 2015年6月6日
 */
public class GomokuAlphaBetaPruningUtilsTest {

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
    public void testGetEmptyPoints() {
	List<Point> pointList;
	String expected;

	pointList = GomokuAlphaBetaPruningUtils.getEmptyPoints(chessboard);
	expected = pointList.toString().replace("java.awt.Point", "");
	assertEquals("[[x=7,y=7]]", expected);

	chessboard.setChess(2, 1, ChessType.BLACK);
	pointList = GomokuAlphaBetaPruningUtils.getEmptyPoints(chessboard);
	pointList.sort((a, b) -> {
	    if (a.x == b.x) {
		return Integer.compare(a.y, b.y);
	    } else {
		return Integer.compare(a.x, b.x);
	    }
	});
	expected = pointList.toString().replace("java.awt.Point", "");
	assertEquals("[[x=1,y=0], [x=1,y=1], [x=1,y=2], [x=2,y=0], [x=2,y=2], [x=3,y=0], [x=3,y=1], [x=3,y=2]]",
		expected);
    }

}
