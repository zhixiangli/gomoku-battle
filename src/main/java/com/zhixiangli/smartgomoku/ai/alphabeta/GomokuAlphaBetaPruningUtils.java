/**
 * 
 */
package com.zhixiangli.smartgomoku.ai.alphabeta;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import com.zhixiangli.smartgomoku.common.GomokuConstant;
import com.zhixiangli.smartgomoku.common.GomokuReferee;
import com.zhixiangli.smartgomoku.common.Pair;
import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

/**
 * utils of AI.
 * 
 * @author lizhixiang
 * @date 2015年6月6日
 */
public class GomokuAlphaBetaPruningUtils {

	/**
	 * 
	 * find point can be put chess.
	 * 
	 * @param chessboard
	 *            Chessboard.
	 * @return point list.
	 */
	public static List<Point> getEmptyPoints(Chessboard chessboard) {
		List<Point> pointList = new ArrayList<>();
		int size = chessboard.getSize();

		int deltaRange = 2;
		boolean[][] canPut = new boolean[size][size];
		for (int row = 0; row < size; ++row) {
			for (int column = 0; column < size; ++column) {
				if (ChessType.EMPTY == chessboard.getChess(row, column)) {
					continue;
				}
				for (int i = Math.max(0, row - deltaRange); i < Math.min(size, row + deltaRange); ++i) {
					for (int j = Math.max(0, column - deltaRange); j < Math.min(size, column + deltaRange); ++j) {
						if (ChessType.EMPTY == chessboard.getChess(i, j)) {
							canPut[i][j] = true;
						}
					}
				}
			}
		}

		for (int row = 0; row < size; ++row) {
			for (int column = 0; column < size; ++column) {
				if (canPut[row][column]) {
					pointList.add(new Point(row, column));
				}
			}
		}
		return pointList;
	}

	/**
	 * 
	 * get estimate according to chess type.
	 * 
	 * @param chessboard
	 *            chessboard.
	 * @param chessType
	 *            chess type, black or white.
	 * @return estimate.
	 */
	public static double getGlobalEstimate(Chessboard chessboard, ChessType chessType) {
		double blackEstimate = 0;
		double whiteEstimate = 0;
		int size = chessboard.getSize();
		for (int row = 0; row < size; ++row) {
			for (int column = 0; column < size; ++column) {
				if (ChessType.EMPTY != chessboard.getChess(row, column)) {
					Point point = new Point(row, column);
					for (int i = 0; i < GomokuConstant.DIRECTIONS.length; ++i) {
						double estimate = getSingleEstimate(chessboard, point, GomokuConstant.DIRECTIONS[i]);
						if (ChessType.BLACK == chessboard.getChess(row, column)) {
							blackEstimate += estimate;
						} else {
							whiteEstimate += estimate;
						}
					}
				}
			}
		}
		double k = 2;
		return ChessType.BLACK == chessType ? blackEstimate - k * whiteEstimate : whiteEstimate - k * blackEstimate;
	}

	/**
	 * 
	 * get estimate of a point.
	 * 
	 * @param chessboard
	 *            chessboard.
	 * @param point
	 *            positon of chess.
	 * @param delta
	 *            direction.
	 * @return estimate.
	 */
	public static double getSingleEstimate(Chessboard chessboard, Point point, Point delta) {
		Pair<Integer, Integer> pair = GomokuReferee.analyseContinuousCount(chessboard, point, delta);
		int type = Math.min(pair.getFirst() * 3 + pair.getSecond(), 5 * 3 + 0);
		switch (type) {

		case 15: // ooooo
			return 603040271;

		case 14: // .oooo.
			return 120608053;

		case 13: // .oooox
			return 24121607;

		case 11: // .ooo.
			return 4824317;

		case 10: // .ooox
			return 964861;

		case 12: // xoooox
			return 192971;

		case 8: // .oo.
			return 38593;

		case 7: // .oox
			return 7717;

		case 9: // xooox
			return 1543;

		case 5: // .o.
			return 307;

		case 4: // .ox
			return 59;

		case 6: // xoox
			return 11;

		case 3: // xox
			return 2;

		default:
			return 0;
		}
	}

}
