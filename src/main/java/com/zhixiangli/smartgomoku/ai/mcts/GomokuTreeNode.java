/**
 * 
 */
package com.zhixiangli.smartgomoku.ai.mcts;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.zhixiangli.smartgomoku.common.GomokuReferee;
import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

/**
 * @author lizhixiang
 *
 */
public class GomokuTreeNode {

	private static final double EPSILON = 1e-6;

	private static final Random RANDOM = new SecureRandom();

	private int numOfWin;

	private int numOfGame;

	private long[] childrenId;

	private Point[] childrenMove;

	private ChessType whoseTurn;

	private boolean isEnd;

	private Chessboard chessboard;

	private long id;

	public GomokuTreeNode(Chessboard chessboard, ChessType whoseTurn, boolean isEnd) {
		this.numOfGame = 0;
		this.numOfWin = 0;
		this.chessboard = chessboard.clone();
		this.whoseTurn = whoseTurn;
		this.isEnd = isEnd;
		this.id = generateId();
	}

	private long generateId() {
		StringBuilder id = new StringBuilder();
		for (int i = 0; i < Chessboard.DEFAULT_SIZE; ++i) {
			for (int j = 0; j < Chessboard.DEFAULT_SIZE; ++j) {
				ChessType chessType = this.chessboard.getChess(i, j);
				if (chessType == ChessType.EMPTY) {
					continue;
				}
				id.append((char) (i + 'a'));
				id.append((char) (j + 'a'));
				id.append(chessType.ordinal());
			}
		}
		return id.toString().hashCode();
	}

	public GomokuTreeNode select() {
		if (null == childrenId) {
			return null;
		}

		GomokuTreeNode selected = null;
		double best = 0;
		for (long id : childrenId) {
			GomokuTreeNode c = GomokuMCTS.TREE_NODE_MAP.get(id);
			double ucb = upperConfidenceBounds(c.numOfWin, c.numOfGame);
			if (ucb > best) {
				best = ucb;
				selected = c;
			}
		}
		return selected;
	}

	private double upperConfidenceBounds(double childNumOfWin, double childNumOfGames) {
		return childNumOfWin / (childNumOfGames + EPSILON)
				+ Math.sqrt(2 * Math.log(this.numOfGame + 1) / (childNumOfGames + EPSILON))
				+ EPSILON * RANDOM.nextDouble();
	}

	public void expand() {
		List<Point> emptyList = GomokuMCTS.searchRange(chessboard);
		if (emptyList.size() == 0) {
			emptyList.add(new Point(Chessboard.DEFAULT_SIZE / 2, Chessboard.DEFAULT_SIZE / 2));
		}
		this.childrenId = new long[emptyList.size()];
		this.childrenMove = new Point[emptyList.size()];
		for (int i = 0; i < emptyList.size(); ++i) {
			Point movePoint = emptyList.get(i);
			Chessboard newChessboard = chessboard.clone();
			boolean isEnd = newChessboard.setChess(movePoint, this.whoseTurn);
			ChessType chessType = GomokuReferee.nextChessType(whoseTurn);
			this.childrenId[i] = GomokuMCTS.nodeFactory(newChessboard, chessType, isEnd);
			this.childrenMove[i] = movePoint;
		}
	}

	public ChessType simulate() {
		List<Point> emptyList = GomokuMCTS.searchRange(chessboard);
		// prevent point repeating.
		Set<Point> emptySet = new HashSet<>(emptyList);

		List<Point> choseList = new ArrayList<>();

		ChessType whoseTurn = this.whoseTurn;
		ChessType winnerType = ChessType.EMPTY;
		while (!emptyList.isEmpty()) {
			int randomIndex = RANDOM.nextInt(emptyList.size());
			Point chosePoint = emptyList.get(randomIndex);
			choseList.add(chosePoint);

			if (chessboard.setChess(chosePoint, whoseTurn)) {
				winnerType = whoseTurn;
				break;
			}
			emptyList.remove(randomIndex);

			List<Point> aroundList = GomokuMCTS.searchAround(chessboard, chosePoint.x, chosePoint.y);
			for (Point point : aroundList) {
				if (emptySet.contains(point)) {
					continue;
				}
				emptyList.add(point);
				emptySet.add(point);
			}

			whoseTurn = GomokuReferee.nextChessType(whoseTurn);
		}
		for (Point p : choseList) {
			chessboard.setChess(p, ChessType.EMPTY);
		}

		return winnerType;

	}

	public void updateStatus(ChessType chessType) {
		++this.numOfGame;
		if (chessType != ChessType.EMPTY && chessType != this.whoseTurn) {
			++this.numOfWin;
		}
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @return the isEnd
	 */
	public boolean isEnd() {
		return isEnd;
	}

	/**
	 * @return the whoseTurn
	 */
	public ChessType getWhoseTurn() {
		return whoseTurn;
	}

	/**
	 * @return the childrenId
	 */
	public long[] getChildrenId() {
		return childrenId;
	}

	/**
	 * @return the childrenMove
	 */
	public Point[] getChildrenMove() {
		return childrenMove;
	}

	/**
	 * @return the chessboard
	 */
	public Chessboard getChessboard() {
		return chessboard;
	}

	/**
	 * @return the numOfWin
	 */
	public int getNumOfWin() {
		return numOfWin;
	}

	/**
	 * @return the numOfGame
	 */
	public int getNumOfGame() {
		return numOfGame;
	}

}
