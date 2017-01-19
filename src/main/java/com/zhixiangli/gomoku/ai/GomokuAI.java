/**
 * 
 */
package com.zhixiangli.gomoku.ai;

import java.awt.Point;

import com.zhixiangli.gomoku.model.ChessType;
import com.zhixiangli.gomoku.model.Chessboard;

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
     * @param chessboard Chessboard.
     * @param chessType the chess type to move.
     * @return the point to move.
     */
    Point next(Chessboard chessboard, ChessType chessType);

}
