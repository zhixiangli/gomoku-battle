/**
 * 
 */
package com.zhixiangli.smartgomoku.ai.mcts;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.zhixiangli.smartgomoku.common.GomokuReferee;
import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

/**
 * @author lizhixiang
 *
 */
public class GomokuTreeNode {

	private static final double EPSILON = 1e-6;

	private static final Random RANDOM = new Random();

	private double nGames;

	private double nWins;

	private Point point;

	private GomokuTreeNode[] children;

	private ChessType isWin;

	/**
	 * @param row
	 * @param column
	 */
	public GomokuTreeNode(int row, int column) {
		this(new Point(row, column));
	}

	/**
	 * @param row
	 * @param column
	 */
	public GomokuTreeNode(Point point) {
		super();
		this.point = point;
	}

	public GomokuTreeNode select(Chessboard chessboard) {
		if (null == children) {
			return null;
		}

		GomokuTreeNode selected = null;
		double best = Long.MIN_VALUE;
		for (GomokuTreeNode c : children) {
			double ucb = upperConfidenceBounds(c.nWins, c.nGames);
			if (ucb > best) {
				best = ucb;
				selected = c;
			}
		}
		return selected;
	}

	private double upperConfidenceBounds(double childNWins, double childNGames) {
		double tmp = childNGames + EPSILON;
		return childNWins / tmp + Math.sqrt(2 * Math.log(this.nGames + 1) / tmp) + EPSILON * RANDOM.nextDouble();
	}

	public void expand(Chessboard chessboard) {
		List<Point> rangeList = GomokuMCTS.expandSearchRange(chessboard, this.point, GomokuMCTS.SEARCH_RANGE);
		this.children = new GomokuTreeNode[rangeList.size()];
		for (int i = 0; i < rangeList.size(); ++i) {
			this.children[i] = new GomokuTreeNode(rangeList.get(i));
		}
	}

	public ChessType simulate(Chessboard chessboard, ChessType chessType) {
		List<Point> rangeList = GomokuMCTS.expandSearchRange(chessboard, Chessboard.DEFAULT_SIZE);
		ChessType currChessType = chessType;
		List<Point> choosePointList = new ArrayList<>();

		boolean isDraw = true;
		for (; rangeList.size() > 0; currChessType = GomokuReferee.nextChessType(currChessType)) {
			Point choosePoint = rangeList.get(RANDOM.nextInt(rangeList.size()));
			choosePointList.add(choosePoint);
			if (chessboard.setChess(choosePoint, currChessType)) {
				isDraw = false;
				break;
			}
			rangeList.remove(choosePoint);
		}
		for (Point point : choosePointList) {
			chessboard.setChess(point, ChessType.EMPTY);
		}
		return isDraw ? null : currChessType;
	}

	/**
	 * 
	 * @param chessboard
	 *            chessboard.
	 * @param chessType
	 *            tree node's chess type.
	 */
	public void go(Chessboard chessboard, ChessType chessType) {
		List<GomokuTreeNode> path = new ArrayList<>();
		GomokuTreeNode curNode = this;
		GomokuTreeNode tmpNode = this;
		ChessType currChessType = chessType;
		ChessType finalWinner = null;
		for (; null != tmpNode; tmpNode = tmpNode.select(chessboard)) {
			curNode = tmpNode;
			path.add(curNode);
			if (chessboard.setChess(curNode.getPoint(), currChessType)) {
				this.isWin = currChessType;
				finalWinner = currChessType;
				break;
			}
			currChessType = GomokuReferee.nextChessType(currChessType);
		}

		if (null == this.isWin) {
			curNode.expand(chessboard);
			curNode = curNode.select(chessboard);

			if (null != curNode) {
				path.add(curNode);
				if (chessboard.setChess(curNode.getPoint(), currChessType)) {
					this.isWin = currChessType;
					finalWinner = currChessType;
				} else {
					finalWinner = curNode.simulate(chessboard, currChessType);
				}
			}
		}

		currChessType = chessType;
		for (GomokuTreeNode node : path) {
			node.updateStatus(currChessType == finalWinner ? 1 : 0);
			chessboard.setChess(node.getPoint(), ChessType.EMPTY);
			currChessType = GomokuReferee.nextChessType(currChessType);
		}
	}

	public void updateStatus(double value) {
		this.nWins += value;
		++this.nGames;
	}

	/**
	 * @return the point
	 */
	public Point getPoint() {
		return point;
	}

	/**
	 * @return the nGames
	 */
	public double getnGames() {
		return nGames;
	}

	/**
	 * @return the nWins
	 */
	public double getnWins() {
		return nWins;
	}

}
