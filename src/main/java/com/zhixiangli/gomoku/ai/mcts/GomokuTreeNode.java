/**
 * 
 */
package com.zhixiangli.gomoku.ai.mcts;

import java.awt.Point;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.zhixiangli.gomoku.common.GomokuReferee;
import com.zhixiangli.gomoku.model.ChessType;
import com.zhixiangli.gomoku.model.Chessboard;

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

    private long id;

    public GomokuTreeNode(Chessboard chessboard) {
        this.numOfGame = 0;
        this.numOfWin = 0;
        this.id = chessboard.getId();
    }

    public int select() {
        int selected = -1;
        if (null != childrenId) {
            double best = 0;
            for (int i = 0; i < this.childrenId.length; ++i) {
                GomokuTreeNode c = GomokuMCTS.TREE_NODE_MAP.get(this.childrenId[i]);
                double ucb = upperConfidenceBounds(c.numOfWin, c.numOfGame);
                if (ucb > best) {
                    best = ucb;
                    selected = i;
                }
            }
        }
        return selected;
    }

    private final double upperConfidenceBounds(double childNumOfWin, double childNumOfGames) {
        return childNumOfWin / (childNumOfGames + EPSILON)
                + Math.sqrt(2 * Math.log(this.numOfGame + 1) / (childNumOfGames + EPSILON))
                + EPSILON * RANDOM.nextDouble();
    }

    public void expand(Chessboard chessboard, ChessType chessType) {
        List<Point> emptyList = GomokuMCTS.searchRange(chessboard);
        if (emptyList.size() == 0) {
            emptyList.add(new Point(Chessboard.DEFAULT_SIZE / 2, Chessboard.DEFAULT_SIZE / 2));
        }
        this.childrenId = new long[emptyList.size()];
        this.childrenMove = new Point[emptyList.size()];
        for (int i = 0; i < emptyList.size(); ++i) {
            Point move = emptyList.get(i);
            chessboard.setChess(move, chessType);
            this.childrenMove[i] = move;
            this.childrenId[i] = GomokuMCTS.registerTreeNode(chessboard);
            chessboard.setChess(move, ChessType.EMPTY);
        }
    }

    public ChessType simulate(Chessboard chessboard, ChessType chessType) {
        List<Point> emptyList = GomokuMCTS.searchRange(chessboard);
        // prevent point repeating.
        Set<Point> emptySet = new HashSet<>(emptyList);

        List<Point> choseList = new ArrayList<>();

        ChessType winnerType = ChessType.EMPTY;
        while (!emptyList.isEmpty()) {
            int randomIndex = RANDOM.nextInt(emptyList.size());
            Point chosePoint = emptyList.get(randomIndex);
            choseList.add(chosePoint);

            if (chessboard.setChess(chosePoint, chessType)) {
                winnerType = chessType;
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

            chessType = GomokuReferee.nextChessType(chessType);
        }
        return winnerType;

    }

    public void updateStatus(boolean isWin) {
        ++this.numOfGame;
        if (isWin) {
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
