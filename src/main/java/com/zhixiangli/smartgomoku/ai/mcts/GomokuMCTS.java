/**
 * 
 */
package com.zhixiangli.smartgomoku.ai.mcts;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.zhixiangli.smartgomoku.ai.GomokuAI;
import com.zhixiangli.smartgomoku.common.GomokuReferee;
import com.zhixiangli.smartgomoku.common.Pair;
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

	public static final int SEARCH_RANGE = 2;

	public static final int EXPERIMENT_TIMES = 5000;

	public static List<Point> expandSearchRange(Chessboard chessboard, Point point, int range) {
		int r0 = Math.max(0, point.x - range);
		int r1 = Math.min(Chessboard.DEFAULT_SIZE, point.x + range);
		int c0 = Math.max(0, point.y - range);
		int c1 = Math.min(Chessboard.DEFAULT_SIZE, point.y + range);
		List<Point> rangeList = new ArrayList<>();
		for (int i = r0; i < r1; ++i) {
			for (int j = c0; j < c1; ++j) {
				if (chessboard.getChess(i, j) != ChessType.EMPTY) {
					continue;
				}
				rangeList.add(new Point(i, j));
			}
		}
		return rangeList;
	}

	public static List<Point> expandSearchRange(Chessboard chessboard, int range) {
		Set<Point> rangeSet = new HashSet<>();
		for (int i = 0; i < Chessboard.DEFAULT_SIZE; ++i) {
			for (int j = 0; j < Chessboard.DEFAULT_SIZE; ++j) {
				if (chessboard.getChess(i, j) == ChessType.EMPTY) {
					continue;
				}
				rangeSet.addAll(expandSearchRange(chessboard, new Point(i, j), range));
			}
		}
		return new ArrayList<>(rangeSet);
	}

	@Override
	public Point next(Chessboard chessboard, ChessType chessType) {
		List<Point> rangeList = expandSearchRange(chessboard, GomokuMCTS.SEARCH_RANGE);
		List<Pair<Point, Double>> valueList = rangeList.stream().map(point -> new Pair<Point, Double>(point, 0.0))
				.collect(Collectors.toList());
		valueList.parallelStream().forEach(pair -> {
			GomokuTreeNode root = new GomokuTreeNode(pair.getFirst());
			Chessboard chessboardCloned = chessboard.clone();
			for (int i = 0; i < EXPERIMENT_TIMES; ++i) {
				root.go(chessboardCloned, chessType);
			}
			pair.setSecond(root.getnWins() / root.getnGames());
		});
		double bestEstimate = valueList.stream().map(pair -> pair.getSecond()).max((a, b) -> Double.compare(a, b))
				.get();
		List<Pair<Point, Double>> resultPointsList = valueList.stream().filter(pair -> bestEstimate == pair.getSecond())
				.collect(Collectors.toList());
		return resultPointsList.get(GomokuReferee.RANDOM.nextInt(resultPointsList.size())).getFirst();
	}

}
