/**
 * 
 */
package com.zhixiangli.gomoku.core.common;

import com.zhixiangli.gomoku.core.chessboard.ChessPatternType;

/**
 * @author zhixiangli
 *
 */
public class GomokuPatternStatistics {

    private int five;

    private int openFour;

    private int halfOpenFour;

    private int openThree;

    private int spacedOpenThree;

    private int halfOpenThree;

    private int openTwo;

    private int oneSpacedOpenTwo;

    private int twoSpacedOpenTwo;

    private int halfOpenTwo;

    public void update(ChessPatternType patternType) {
        switch (patternType) {
        case FIVE:
            ++this.five;
            break;
        case OPEN_FOUR:
            ++this.openFour;
            break;
        case HALF_OPEN_FOUR:
            ++this.halfOpenFour;
            break;
        case OPEN_THREE:
            ++this.openThree;
            break;
        case SPACED_OPEN_THREE:
            ++this.spacedOpenThree;
            break;
        case HALF_OPEN_THREE:
            ++this.halfOpenThree;
            break;
        case OPEN_TWO:
            ++this.openTwo;
            break;
        case ONE_SPACED_OPEN_TWO:
            ++this.oneSpacedOpenTwo;
            break;
        case TWO_SPACED_OPEN_TWO:
            ++this.twoSpacedOpenTwo;
            break;
        case HALF_OPEN_TWO:
            ++this.halfOpenTwo;
            break;
        default:
        }
    }

    /**
     * @return the five
     */
    public int getFive() {
        return five;
    }

    /**
     * @return the openFour
     */
    public int getOpenFour() {
        return openFour;
    }

    /**
     * @return the halfOpenFour
     */
    public int getHalfOpenFour() {
        return halfOpenFour;
    }

    /**
     * @return the openThree
     */
    public int getOpenThree() {
        return openThree;
    }

    /**
     * @return the spacedOpenThree
     */
    public int getSpacedOpenThree() {
        return spacedOpenThree;
    }

    /**
     * @return the halfOpenThree
     */
    public int getHalfOpenThree() {
        return halfOpenThree;
    }

    /**
     * @return the openTwo
     */
    public int getOpenTwo() {
        return openTwo;
    }

    /**
     * @return the oneSpacedOpenTwo
     */
    public int getOneSpacedOpenTwo() {
        return oneSpacedOpenTwo;
    }

    /**
     * @return the twoSpacedOpenTwo
     */
    public int getTwoSpacedOpenTwo() {
        return twoSpacedOpenTwo;
    }

    /**
     * @return the halfOpenTwo
     */
    public int getHalfOpenTwo() {
        return halfOpenTwo;
    }

}
