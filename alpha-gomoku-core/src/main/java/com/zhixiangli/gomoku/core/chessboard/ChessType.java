/**
 * 
 */
package com.zhixiangli.gomoku.core.chessboard;

/**
 * chess type.
 * 
 * @author lizhixiang
 *
 */
public enum ChessType {

    EMPTY('.'),

    BLACK('B'),

    WHITE('W'),

    ;

    private char chessChar;

    private ChessType(char chessChar) {
        this.chessChar = chessChar;
    }

    public static ChessType getChessType(char ch) {
        for (ChessType chessType : ChessType.values()) {
            if (ch == chessType.getChessChar()) {
                return chessType;
            }
        }
        return null;
    }

    /**
     * @return the chessChar
     */
    public char getChessChar() {
        return chessChar;
    }

}
