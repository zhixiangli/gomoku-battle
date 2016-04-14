/**
 * 
 */
package com.zhixiangli.smartgomoku.ai.mcts;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zhixiangli.smartgomoku.ai.GomokuAI;
import com.zhixiangli.smartgomoku.common.GomokuReferee;
import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

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

	public static final int EXPERIMENT_TIMES = 10000;

	public static final long nodeFactory(Chessboard chessboard, ChessType chessType, boolean isEnd) {
		GomokuTreeNode node = new GomokuTreeNode(chessboard, chessType, isEnd);
		long nodeId = node.getId();
		TREE_NODE_MAP.putIfAbsent(nodeId, node);
		return nodeId;
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
		GomokuTreeNode curNode = TREE_NODE_MAP.get(nodeFactory(chessboard, chessType, false));
		for (int i = 0; i < EXPERIMENT_TIMES; ++i) {
			this.mcts(curNode);
		}

		long[] childrenId = curNode.getChildrenId();
		Point[] childrenMove = curNode.getChildrenMove();
		double winRate = 0;
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

	public void mcts(GomokuTreeNode curNode) {
		List<GomokuTreeNode> dfsPath = new ArrayList<>();
		GomokuTreeNode tmpNode = curNode;
		ChessType winnerType = ChessType.EMPTY;
		for (; null != tmpNode; tmpNode = tmpNode.select()) {
			curNode = tmpNode;
			dfsPath.add(curNode);
			if (curNode.isEnd()) {
				winnerType = GomokuReferee.nextChessType(curNode.getWhoseTurn());
				break;
			}
		}

		if (winnerType == ChessType.EMPTY) {
			curNode.expand();
			curNode = curNode.select();
			if (null != curNode) {
				dfsPath.add(curNode);
				winnerType = curNode.simulate();
			}
		}

		for (GomokuTreeNode node : dfsPath) {
			node.updateStatus(winnerType);
		}

	}

}
