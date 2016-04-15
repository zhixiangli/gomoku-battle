/**
 * 
 */
package com.zhixiangli.smartgomoku.ai;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.zhixiangli.smartgomoku.ai.mcts.GomokuMCTS;
import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

/**
 * @author lizhixiang
 *
 */
public class GomokuTreeNodeTest {

    @Test
    public void nodeFactory() {
	Chessboard chessboard0 = new Chessboard();
	chessboard0.setChess(3, 5, ChessType.BLACK);
	chessboard0.setChess(13, 5, ChessType.WHITE);
	chessboard0.setChess(3, 6, ChessType.BLACK);

	Chessboard chessboard1 = new Chessboard();
	chessboard1.setChess(3, 5, ChessType.BLACK);
	chessboard1.setChess(13, 5, ChessType.WHITE);
	chessboard1.setChess(3, 6, ChessType.BLACK);

	GomokuMCTS.registerTreeNode(chessboard1);
	GomokuMCTS.registerTreeNode(chessboard0);
	assertEquals(1, GomokuMCTS.TREE_NODE_MAP.size());
    }

}
