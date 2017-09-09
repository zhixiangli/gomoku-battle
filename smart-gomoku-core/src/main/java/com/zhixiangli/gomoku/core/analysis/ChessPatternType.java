/**
 * 
 */
package com.zhixiangli.gomoku.core.chessboard;

/**
 * 
 * WARNING!!!
 * 
 * ENUM IS ORDER BY ITS VALUE.
 * 
 * @author zhixiangli
 *
 */
public enum ChessPatternType {

    OTHERS,

    /**
     * at least 1 position to form THREE_HALF.
     * 
     * ...oox, ..o.ox
     */
    HALF_OPEN_TWO,

    /**
     * .o..o.
     */
    TWO_SPACED_OPEN_TWO,

    /**
     * .o.o..
     */
    ONE_SPACED_OPEN_TWO,

    /**
     * at least 1 position to form THREE_LIVE.
     * 
     * ..oo..
     */
    OPEN_TWO,

    /**
     * at least 1 position to form FOUR_HALF.
     * 
     * ..ooox, .o.oox, .oo.ox, o..oo, o.o.o, x.ooo.x
     */
    HALF_OPEN_THREE,

    /**
     * .o.oo.
     */
    SPACED_OPEN_THREE,

    /**
     * at least 1 position to form FOUR_LIVE.
     * 
     * ..ooo.
     */
    OPEN_THREE,

    /**
     * only 1 position to form FIVE.
     * 
     * .oooox, o.ooo, oo.oo,
     */
    HALF_OPEN_FOUR,

    /**
     * 2 positions to form FIVE.
     * 
     * .oooo.
     */
    OPEN_FOUR,

    /**
     * at least 5 chesses.
     */
    FIVE,

    ;

}
