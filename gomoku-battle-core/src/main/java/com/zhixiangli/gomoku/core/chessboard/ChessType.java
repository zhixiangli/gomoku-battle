package com.zhixiangli.gomoku.core.chessboard;

/**
 * chess type.
 *
 * @author lizhixiang
 */
public enum ChessType {

    EMPTY('.'),

    BLACK('B'),

    WHITE('W'),

    ;

    private final char chessChar;

    ChessType(final char chessChar) {
        this.chessChar = chessChar;
    }

    public static ChessType getChessType(final char ch) {
        for (final ChessType chessType : ChessType.values()) {
            if (ch == chessType.chessChar) {
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
