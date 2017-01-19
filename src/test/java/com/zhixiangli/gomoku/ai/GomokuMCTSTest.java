/**
 * 
 */
package com.zhixiangli.gomoku.ai;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.zhixiangli.gomoku.ai.mcts.GomokuMCTS;
import com.zhixiangli.gomoku.ai.mcts.GomokuTreeNode;
import com.zhixiangli.gomoku.chessboard.ChessType;
import com.zhixiangli.gomoku.chessboard.Chessboard;

/**
 * @author lizhixiang
 *
 */
public class GomokuMCTSTest {

    private GomokuMCTS gomokuMCTS;

    @Before
    public void setUp() throws Exception {
        this.gomokuMCTS = new GomokuMCTS();
    }

    @Test
    public void next() {

        Chessboard chessboard = new Chessboard();
        chessboard.setChess(5, 5, ChessType.BLACK);
        chessboard.setChess(6, 6, ChessType.BLACK);
        chessboard.setChess(7, 7, ChessType.BLACK);
        // chessboard.setChess(8, 8, ChessType.BLACK);
        chessboard.setChess(4, 5, ChessType.WHITE);
        chessboard.setChess(5, 6, ChessType.WHITE);
        chessboard.setChess(6, 7, ChessType.WHITE);
        // chessboard.setChess(7, 8, ChessType.WHITE);
        GomokuTreeNode node = GomokuMCTS.TREE_NODE_MAP.get(GomokuMCTS.registerTreeNode(chessboard));

        long st = System.nanoTime();

        this.gomokuMCTS.next(chessboard, ChessType.BLACK);

        long nd = System.nanoTime();

        System.out.println("time escape: " + (nd - st) / 1e9);
        System.out.println(node.getNumOfWin() + " / " + node.getNumOfGame());
        System.out.println("win: " + node.getNumOfWin() * 1.0 / node.getNumOfGame());

    }

    @Test
    public void simulate() {

        Chessboard chessboard = new Chessboard();
        chessboard.setChess(5, 5, ChessType.BLACK);
        chessboard.setChess(6, 6, ChessType.BLACK);
        chessboard.setChess(7, 7, ChessType.BLACK);
        // chessboard.setChess(8, 8, ChessType.BLACK);
        chessboard.setChess(4, 5, ChessType.WHITE);
        chessboard.setChess(5, 6, ChessType.WHITE);
        chessboard.setChess(6, 7, ChessType.WHITE);
        // chessboard.setChess(7, 8, ChessType.WHITE);
        GomokuTreeNode node = GomokuMCTS.TREE_NODE_MAP.get(GomokuMCTS.registerTreeNode(chessboard));

        int b = 0;
        int w = 0;
        for (int i = 0; i < 1000; i++) {
            ChessType type = node.simulate(chessboard.clone(), ChessType.BLACK);
            if (type == ChessType.BLACK) {
                ++b;
            } else {
                ++w;
            }
        }

        assertEquals(b > w, true);

    }

}
