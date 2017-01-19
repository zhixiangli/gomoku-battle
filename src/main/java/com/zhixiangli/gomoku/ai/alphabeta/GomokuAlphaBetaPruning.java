/**
 * 
 */
package com.zhixiangli.gomoku.ai.alphabeta;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.zhixiangli.gomoku.ai.GomokuAgent;
import com.zhixiangli.gomoku.chessboard.ChessType;
import com.zhixiangli.gomoku.chessboard.Chessboard;
import com.zhixiangli.gomoku.common.GomokuReferee;

/**
 * alpha beta pruning strategy.
 * 
 * @author lizhixiang
 * @date 2015年5月24日
 */
public class GomokuAlphaBetaPruning implements GomokuAgent {

    /**
     * each level, find best number of point to next level.
     */
    private static final int SEARCH_WIDTH = 25;

    /**
     * the depth of search level.
     */
    private static final int SEARCH_DEPTH = 3;

    /**
     * the score when win.
     */
    private static final double MAX_SCORE = 1e20;

    /*
     * (non-Javadoc)
     * 
     * @see com.zhixiangli.smartgomoku.ai.GomokuAI#next(com.zhixiangli.smartgomoku
     * .model.Chessboard, com.zhixiangli.smartgomoku.model.ChessType)
     */
    @Override
    public Point next(Chessboard chessboard, ChessType chessType) {
        List<Pair<Point, Double>> bestPointsList = this.getBestPoints(chessboard, chessType);
        if (bestPointsList.isEmpty()) {
            return new Point(chessboard.getLength() / 2, chessboard.getLength() / 2);
        }

        bestPointsList.parallelStream().forEach(pair -> {
            try {
                Chessboard newChessboard = chessboard.clone();
                newChessboard.setChess(pair.getLeft(), chessType);
                double estimate = GomokuReferee.isWin(newChessboard, pair.getLeft()) ? MAX_SCORE
                        : this.alphaBeta(0, -MAX_SCORE, MAX_SCORE, newChessboard,
                                GomokuReferee.nextChessType(chessType));
                pair.setValue(estimate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        double bestEstimate =
                bestPointsList.stream().map(pair -> pair.getRight()).max((a, b) -> Double.compare(a, b)).get();
        List<Pair<Point, Double>> resultPointsList = bestPointsList.stream()
                .filter(pair -> Math.abs(bestEstimate - pair.getRight()) < 1e-6).collect(Collectors.toList());
        int randomIndex = GomokuReferee.RANDOM.nextInt(resultPointsList.size());
        return resultPointsList.get(randomIndex).getLeft();
    }

    private final List<Pair<Point, Double>> getBestPoints(Chessboard chessboard, ChessType chessType) {
        List<Point> pointList = GomokuAlphaBetaPruningUtils.getEmptyPoints(chessboard);
        return pointList.parallelStream().map(point -> {
            Chessboard newChessboard = chessboard.clone();
            newChessboard.setChess(point, chessType);
            double estimate = GomokuReferee.isWin(newChessboard, point) ? MAX_SCORE
                    : GomokuAlphaBetaPruningUtils.getGlobalEstimate(newChessboard, chessType);
            return MutablePair.of(point, estimate);
        }).sorted((a, b) -> Double.compare(b.getRight(), a.getRight())).limit(SEARCH_WIDTH)
                .collect(Collectors.toList());
    }

    private final double alphaBeta(int depth, double alpha, double beta, Chessboard chessboard, ChessType chessType) {
        if (SEARCH_DEPTH == depth) {
            return GomokuAlphaBetaPruningUtils.getGlobalEstimate(chessboard, chessType);
        } else {
            ChessType nextChessType = GomokuReferee.nextChessType(chessType);
            List<Pair<Point, Double>> bestPointsList = this.getBestPoints(chessboard, chessType);
            for (Pair<Point, Double> bestPoint : bestPointsList) {
                Point point = bestPoint.getLeft();
                if ((depth & 1) == 0) {
                    chessboard.setChess(point, chessType);
                    if (GomokuReferee.isWin(chessboard, point)) {
                        beta = -MAX_SCORE;
                    } else {
                        beta = Math.min(beta, this.alphaBeta(depth + 1, alpha, beta, chessboard, nextChessType));
                    }
                    chessboard.setChess(point, ChessType.EMPTY);
                    if (beta <= alpha) {
                        break;
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
                        break;
                    }
                }
            }
            return ((depth & 1) == 0 ? beta : alpha) * 0.99;
        }
    }
}
