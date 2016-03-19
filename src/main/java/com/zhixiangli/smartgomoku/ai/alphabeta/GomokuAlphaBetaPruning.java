/**
 * 
 */
package com.zhixiangli.smartgomoku.ai.alphabeta;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;

import com.zhixiangli.smartgomoku.ai.GomokuAI;
import com.zhixiangli.smartgomoku.common.GomokuReferee;
import com.zhixiangli.smartgomoku.common.Pair;
import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

/**
 * alpha beta pruning strategy.
 * 
 * @author lizhixiang
 * @date 2015年5月24日
 */
public class GomokuAlphaBetaPruning implements GomokuAI {

	/**
	 * each level, find best number of point to next level.
	 */
	private static final int SEARCH_WIDTH = 16;

	/**
	 * the depth of search level.
	 */
	private static final int SEARCH_DEPTH = 3;

	/**
	 * the score when win.
	 */
	private static final int MAX_SCORE = (int) 1e9;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.zhixiangli.smartgomoku.ai.GomokuAI#next(com.zhixiangli.smartgomoku
	 * .model.Chessboard, com.zhixiangli.smartgomoku.model.ChessType)
	 */
	@Override
	public Point next(Chessboard chessboard, ChessType chessType) {
		List<Pair<Point, Double>> bestPointsList = this.getBestPoints(chessboard, chessType);
		bestPointsList.parallelStream().forEach(pair -> {
			Chessboard newChessboard = chessboard.clone();
			newChessboard.setChess(pair.getFirst(), chessType);
			double estimate = this.alphaBeta(0, -MAX_SCORE, MAX_SCORE, newChessboard,
					GomokuReferee.nextChessType(chessType));
			pair.setSecond(estimate);
		});
		double bestEstimate = bestPointsList.stream().map(pair -> pair.getSecond()).max((a, b) -> Double.compare(a, b))
				.get();
		List<Pair<Point, Double>> resultPointsList = bestPointsList.stream()
				.filter(pair -> bestEstimate == pair.getSecond()).collect(Collectors.toList());
		return resultPointsList.get(GomokuReferee.RANDOM.nextInt(resultPointsList.size())).getFirst();
	}

	private List<Pair<Point, Double>> getBestPoints(Chessboard chessboard, ChessType chessType) {
		List<Point> pointList = GomokuAlphaBetaPruningUtils.getEmptyPoints(chessboard);
		return pointList.parallelStream().map(point -> {
			Chessboard newChessboard = chessboard.clone();
			newChessboard.setChess(point, chessType);
			double estimate = GomokuAlphaBetaPruningUtils.getGlobalEstimate(newChessboard, chessType);
			return new Pair<Point, Double>(point, estimate);
		}).sorted((a, b) -> Double.compare(b.getSecond(), a.getSecond())).limit(SEARCH_WIDTH)
				.collect(Collectors.toList());
	}

	private double alphaBeta(int depth, double alpha, double beta, Chessboard chessboard, ChessType chessType) {
		if (SEARCH_DEPTH == depth) {
			return GomokuAlphaBetaPruningUtils.getGlobalEstimate(chessboard, chessType);
		} else {
			ChessType nextChessType = GomokuReferee.nextChessType(chessType);
			List<Pair<Point, Double>> bestPointsList = this.getBestPoints(chessboard, chessType);
			for (Pair<Point, Double> bestPoint : bestPointsList) {
				Point point = bestPoint.getFirst();
				if ((depth & 1) == 0) {
					chessboard.setChess(point, chessType);
					if (GomokuReferee.isWin(chessboard, point)) {
						beta = -MAX_SCORE;
					} else {
						beta = Math.min(beta, this.alphaBeta(depth + 1, alpha, beta, chessboard, nextChessType));
					}
					chessboard.setChess(point, ChessType.EMPTY);
					if (beta <= alpha) {
						return beta;
					}
				} else {
					chessboard.setChess(point, chessType);
					if (GomokuReferee.isWin(chessboard, point)) {
						alpha = MAX_SCORE;
					} else {
						alpha = Math.max(alpha, this.alphaBeta(depth + 1, alpha, beta, chessboard, nextChessType));
					}
					chessboard.setChess(point, ChessType.EMPTY);
					if (alpha >= beta) {
						return alpha;
					}
				}
			}
			return (depth & 1) == 0 ? beta : alpha;
		}
	}
}
