/**
 * 
 */
package com.zhixiangli.gomoku.ai.mcts;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zhixiangli.gomoku.ai.GomokuAI;
import com.zhixiangli.gomoku.common.GomokuReferee;
import com.zhixiangli.gomoku.model.ChessType;
import com.zhixiangli.gomoku.model.Chessboard;

/**
 * monte carlo tree search of gomoku.
 * 
 * @author lizhixiang
 * 
 *
 */
public class GomokuMCTS implements GomokuAI {

    public static final Map<Long, GomokuTreeNode> TREE_NODE_MAP = new HashMap<>();

    public static final int SEARCH_RANGE = 2;

    public static final int EXPERIMENT_TIMES = 100000;

    public static final long registerTreeNode(Chessboard chessboard) {
        long id = chessboard.getId();
        if (!TREE_NODE_MAP.containsKey(id)) {
            GomokuTreeNode node = new GomokuTreeNode(chessboard);
            TREE_NODE_MAP.put(id, node);
        }
        return id;
    }

    public static List<Point> searchAround(Chessboard chessboard, int x, int y) {
        List<Point> aroundList = new ArrayList<>();
        int x0 = Math.max(0, x - SEARCH_RANGE), x1 = Math.min(Chessboard.DEFAULT_SIZE - 1, x + SEARCH_RANGE);
        int y0 = Math.max(0, y - SEARCH_RANGE), y1 = Math.min(Chessboard.DEFAULT_SIZE - 1, y + SEARCH_RANGE);
        for (int a = x0; a <= x1; ++a) {
            for (int b = y0; b <= y1; ++b) {
                if (chessboard.getChess(a, b) != ChessType.EMPTY) {
                    continue;
                }
                aroundList.add(new Point(a, b));
            }
        }
        return aroundList;
    }

    public static List<Point> searchRange(Chessboard chessboard) {
        Set<Point> rangeSet = new HashSet<>();
        for (int i = 0; i < Chessboard.DEFAULT_SIZE; ++i) {
            for (int j = 0; j < Chessboard.DEFAULT_SIZE; ++j) {
                if (chessboard.getChess(i, j) == ChessType.EMPTY) {
                    continue;
                }
                rangeSet.addAll(searchAround(chessboard, i, j));
            }
        }
        return new ArrayList<Point>(rangeSet);
    }

    @Override
    public Point next(Chessboard chessboard, ChessType chessType) {
        GomokuTreeNode curNode = TREE_NODE_MAP.get(registerTreeNode(chessboard));
        for (int i = 0; i < EXPERIMENT_TIMES; ++i) {
            this.mcts(chessboard.clone(), curNode, chessType);
        }

        long[] childrenId = curNode.getChildrenId();
        Point[] childrenMove = curNode.getChildrenMove();
        double winRate = -1;
        Point p = null;
        for (int i = 0; i < childrenId.length; ++i) {
            GomokuTreeNode child = TREE_NODE_MAP.get(childrenId[i]);
            double tmpRate = child.getNumOfWin() * 1.0 / child.getNumOfGame();
            if (tmpRate > winRate) {
                winRate = tmpRate;
                p = childrenMove[i];
            }
        }

        return p;
    }

    public void mcts(Chessboard chessboard, GomokuTreeNode curNode, ChessType chessType) {
        ChessType winnerType = ChessType.EMPTY;
        ChessType currentChessType = chessType;

        List<GomokuTreeNode> dfsPath = new ArrayList<>();
        dfsPath.add(curNode);

        while (curNode.getChildrenId() != null) {
            int selected = curNode.select();
            Point move = curNode.getChildrenMove()[selected];
            if (chessboard.setChess(move, currentChessType)) {
                winnerType = currentChessType;
                break;
            }
            curNode = GomokuMCTS.TREE_NODE_MAP.get(curNode.getChildrenId()[selected]);
            dfsPath.add(curNode);
            currentChessType = GomokuReferee.nextChessType(currentChessType);
        }

        if (winnerType == ChessType.EMPTY) {
            curNode.expand(chessboard, currentChessType);
            int selected = curNode.select();
            if (selected >= 0) {
                Point move = curNode.getChildrenMove()[selected];
                curNode = GomokuMCTS.TREE_NODE_MAP.get(curNode.getChildrenId()[selected]);
                dfsPath.add(curNode);
                if (chessboard.setChess(move, currentChessType)) {
                    winnerType = currentChessType;
                } else {
                    winnerType = curNode.simulate(chessboard, GomokuReferee.nextChessType(currentChessType));
                }
            }
        }

        currentChessType = chessType;
        for (GomokuTreeNode node : dfsPath) {
            node.updateStatus(winnerType != currentChessType);
            currentChessType = GomokuReferee.nextChessType(currentChessType);
        }

    }
}
