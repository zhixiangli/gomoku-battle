/**
 * 
 */
package com.zhixiangli.gomoku.core.common;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author zhixiangli
 *
 */
public class GomokuFormatterTest {

    @Test
    public void encodePoints() {
        List<Point> points = Arrays.asList(new Point(1, 2), new Point(6, 12));
        Assert.assertEquals("BC GM", GomokuFormatter.encodePoints(points));
    }

}
