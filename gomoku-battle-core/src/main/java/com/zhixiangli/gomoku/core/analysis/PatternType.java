package com.zhixiangli.gomoku.core.analysis;

/**
 * WARNING!!!
 * <p>
 * ENUM IS ORDER BY ITS VALUE.
 *
 * @author zhixiangli
 */
public enum PatternType {

    OTHERS(0),

    /**
     * at least 1 position to form THREE_HALF.
     * <p>
     * ...oox, ..o.ox
     */
    HALF_OPEN_TWO(2),

    /**
     * .o..o.
     */
    TWO_SPACED_OPEN_TWO(2),

    /**
     * .o.o..
     */
    ONE_SPACED_OPEN_TWO(2),

    /**
     * at least 1 position to form THREE_LIVE.
     * <p>
     * ..oo..
     */
    OPEN_TWO(2),

    /**
     * at least 1 position to form FOUR_HALF.
     * <p>
     * ..ooox, .o.oox, .oo.ox, o..oo, o.o.o, x.ooo.x
     */
    HALF_OPEN_THREE(3),

    /**
     * .o.oo.
     */
    SPACED_OPEN_THREE(3),

    /**
     * at least 1 position to form FOUR_LIVE.
     * <p>
     * ..ooo.
     */
    OPEN_THREE(3),

    /**
     * only 1 position to form FIVE.
     * <p>
     * .oooox, o.ooo, oo.oo,
     */
    HALF_OPEN_FOUR(4),

    /**
     * 2 positions to form FIVE.
     * <p>
     * .oooo.
     */
    OPEN_FOUR(4),

    /**
     * at least 5 chesses.
     */
    FIVE(5),

    ;

    private final int chessNum;

    PatternType(final int chessNum) {
        this.chessNum = chessNum;
    }

    /**
     * @return the chessNum
     */
    public int getChessNum() {
        return chessNum;
    }

}
