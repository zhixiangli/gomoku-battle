/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch.common;

import java.awt.Point;

/**
 * @author zhixiangli
 *
 */
public class SearchUtils {

    public static final double DECAY_FACTOR = 0.999;

    public static final int AROUND_CANDIDATE_RANGE = 2;

    public static final int MAX_CANDIDATE_NUM = 10;

    public static final int MAX_DEPTH = 6;

    public static final String encodePoint(Point point) {
        return String.format("%c%c", encodeInt(point.x), encodeInt(point.y));
    }

    private static final char encodeInt(int x) {
        return (char) (x + 'A');
    }

}
