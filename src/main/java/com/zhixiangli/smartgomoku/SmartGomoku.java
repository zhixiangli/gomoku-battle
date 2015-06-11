/**
 * 
 */
package com.zhixiangli.smartgomoku;

import java.awt.Point;
import java.util.logging.Logger;

import com.zhixiangli.smartgomoku.ai.GomokuAI;
import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

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
     * @param gomokuAIClass
     *            subclass of GomokuAI.class, strategy of AI.
     * @param chessboard
     *            Chessboard.class
     * @param chessType
     *            ChessType.class
     * @return point of next move.
     */
    public static Point next(Class<? extends GomokuAI> gomokuAIClass, Chessboard chessboard,
        ChessType chessType) {
        try {
            GomokuAI gomokuAI = gomokuAIClass.newInstance();
            return gomokuAI.next(chessboard, chessType);
        } catch (InstantiationException | IllegalAccessException e) {
            LOGGER.severe(() -> e.getMessage());
        }
        return null;
    }
}
