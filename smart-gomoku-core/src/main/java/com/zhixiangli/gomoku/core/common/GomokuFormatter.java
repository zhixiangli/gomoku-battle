/**
 * 
 */
package com.zhixiangli.gomoku.core.common;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

/**
 * @author zhixiangli
 *
 */
public class GomokuFormatter {

    public static final String encodePoints(List<Point> point) {
        return StringUtils.join(point.stream().map(p -> encodePoint(p)).collect(Collectors.toList()),
                StringUtils.SPACE);
    }

    public static final String encodePoint(Point point) {
        return String.format("%c%c", encodeInt(point.x), encodeInt(point.y));
    }

    private static final char encodeInt(int x) {
        return (char) (x + 'A');
    }

}
