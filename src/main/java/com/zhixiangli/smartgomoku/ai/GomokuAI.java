/**
 * 
 */
package com.zhixiangli.smartgomoku.ai;

import java.awt.Point;

import com.zhixiangli.smartgomoku.model.ChessType;
import com.zhixiangli.smartgomoku.model.Chessboard;

/**
 * Gomoku AI interface.
 * 
 * @author lizhixiang
 *
 */
public interface GomokuAI {

    /**
     * 
     * get next move.
     * 
     * @param chessboard
     *            Chessboard.
     * @param chessType
     *            the chess type to move.
     * @return the point to move.
     */
    Point next(Chessboard chessboard, ChessType chessType);

}
