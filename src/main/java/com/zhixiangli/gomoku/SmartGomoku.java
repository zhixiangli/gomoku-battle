/**
 * 
 */
package com.zhixiangli.gomoku;

import java.awt.Point;
import java.util.logging.Logger;

import com.zhixiangli.gomoku.agent.GomokuAgent;
import com.zhixiangli.gomoku.chessboard.ChessType;
import com.zhixiangli.gomoku.chessboard.Chessboard;

/**
 * entry of AI.
 * 
 * @author lizhixiang
 * @date 2015年6月7日
 */
public class SmartGomoku {

    private static final Logger LOGGER = Logger.getLogger(SmartGomoku.class.getName());

    /**
     * 
     * get next move.
     * 
     * @param gomokuAIClass subclass of GomokuAI.class, strategy of AI.
     * @param chessboard Chessboard.class
     * @param chessType ChessType.class
     * @return point of next move.
     */
    public static Point next(Class<? extends GomokuAgent> gomokuAIClass, Chessboard chessboard, ChessType chessType) {
        try {
            System.out.println("current player: " + chessType);
            GomokuAgent gomokuAI = gomokuAIClass.newInstance();
            Point nextPoint = gomokuAI.next(chessboard.clone(), chessType);
            System.out.println("player set: " + nextPoint);
            return nextPoint;
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.severe(() -> e.getMessage());
        }
        return null;
    }
}
