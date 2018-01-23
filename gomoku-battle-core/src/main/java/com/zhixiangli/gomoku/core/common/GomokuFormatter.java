/**
 * 
 */
package com.zhixiangli.gomoku.core.common;

import java.awt.Point;

/**
 * @author zhixiangli
 *
 */
public class GomokuFormatter {

    public static final String encodePoint(Point point) {
        return String.format("%s%s", encodeAxis(point.x), encodeAxis(point.y));
    }

    public static final String encodeAxis(int x) {
        return Integer.toHexString(x);
    }

    public static final int decodeAxis(char hex) {
        return Integer.parseInt("" + hex, 16);
    }

}
