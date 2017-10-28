/**
 * 
 */
package com.zhixiangli.gomoku.core.chessboard;

/**
 * chessboard's state.
 * 
 * @author lizhixiang
 */
public enum ChessState {

    GAME_READY,

    /**
     * game is on.
     */
    GAME_ON,

    /**
     * game is draw.
     */
    GAME_DRAW,

    /**
     * white player win.
     */
    WHITE_WIN,

    /**
     * black player win.
     */
    BLACK_WIN,

}
