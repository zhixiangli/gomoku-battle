/**
 * 
 */
package com.zhixiangli.smartgomoku.ai;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

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
     * random generator.
     */
    private static final Random RANDOM = new SecureRandom();
    
    /**
     * each level, find best number of point to next level.
     */
    private static final int SEARCH_WIDTH = 12;
    
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
        List<Pair<Point, Integer>> bestPointsList = this.getBestPoints(chessboard, chessType);
        bestPointsList.parallelStream().forEach(
            pair -> {
                Chessboard newChessboard = chessboard.clone();
                newChessboard.setChess(pair.getFirst(), chessType);
                int estimate = this.alphaBeta(0, -MAX_SCORE, MAX_SCORE, newChessboard,
                    GomokuReferee.nextChessType(chessType));
                pair.setSecond(estimate);
            });
        int bestEstimate = bestPointsList.stream().map(pair -> pair.getSecond())
            .max((a, b) -> Integer.compare(a, b)).get();
        List<Pair<Point, Integer>> resultPointsList = bestPointsList.stream()
            .filter(pair -> bestEstimate == pair.getSecond()).collect(Collectors.toList());
        return resultPointsList.get(RANDOM.nextInt(resultPointsList.size())).getFirst();
    }
    
    private List<Pair<Point, Integer>> getBestPoints(Chessboard chessboard, ChessType chessType) {
        List<Point> pointList = GomokuAlphaBetaPruningUtils.getEmptyPoints(chessboard);
        return pointList.parallelStream().map(point -> {
            Chessboard newChessboard = chessboard.clone();
            newChessboard.setChess(point, chessType);
            int estimate = GomokuAlphaBetaPruningUtils.getGlobalEstimate(newChessboard, chessType);
            return new Pair<Point, Integer>(point, estimate);
        }).sorted((a, b) -> Integer.compare(b.getSecond(), a.getSecond())).limit(SEARCH_WIDTH)
            .collect(Collectors.toList());
    }
    
    private int alphaBeta(int depth, int alpha, int beta, Chessboard chessboard, ChessType chessType) {
        if (SEARCH_DEPTH == depth) {
            return GomokuAlphaBetaPruningUtils.getGlobalEstimate(chessboard, chessType);
        } else {
            ChessType nextChessType = GomokuReferee.nextChessType(chessType);
            List<Pair<Point, Integer>> bestPointsList = this.getBestPoints(chessboard, chessType);
            for (Pair<Point, Integer> bestPoint : bestPointsList) {
                Point point = bestPoint.getFirst();
                if ((depth & 1) == 0) {
                    chessboard.setChess(point, chessType);
                    if (GomokuReferee.isWin(chessboard, point)) {
                        beta = -MAX_SCORE;
                    } else {
                        beta = Math.min(beta,
                            this.alphaBeta(depth + 1, alpha, beta, chessboard, nextChessType));
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
                        alpha = Math.max(alpha,
                            this.alphaBeta(depth + 1, alpha, beta, chessboard, nextChessType));
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
