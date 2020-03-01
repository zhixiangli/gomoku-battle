/**
 *
 */
package com.zhixiangli.gomoku.core.common;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhixiangli
 *
 */
public class GomokuFormatter {

    private GomokuFormatter() {
    }

    public static String encodePoint(final Point point) {
        return String.format("%s%s", encodeAxis(point.x), encodeAxis(point.y));
    }

    public static String encodeAxis(final int x) {
        return Integer.toHexString(x);
    }

    public static int decodeAxis(final char hex) {
        return Integer.parseInt(String.valueOf(hex), 16);
    }

    public static String toSGF(final List<Pair<ChessType, Point>> history) {
        final List<String> sgf = history.stream().map(pair -> String.format("%c[%s]", pair.getKey().getChessChar(),
                GomokuFormatter.encodePoint(pair.getValue()))).collect(Collectors.toList());
        return StringUtils.join(sgf, ";");
    }

    public static Chessboard toChessboard(final String sgf) {
        final String[] pieces = StringUtils.split(sgf, ';');
        final Chessboard board = new Chessboard();
        for (final String piece : pieces) {
            final int row = GomokuFormatter.decodeAxis(piece.charAt(2));
            final int column = GomokuFormatter.decodeAxis(piece.charAt(3));
            board.setChess(row, column, ChessType.getChessType(piece.charAt(0)));
        }
        return board;
    }

}
