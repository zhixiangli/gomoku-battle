/**
 *
 */
package com.zhixiangli.gomoku.core.common;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.util.ArrayList;
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
        return decodeCoordinate(hex);
    }

    public static String toSGF(final List<Pair<ChessType, Point>> history) {
        final List<String> sgf = history.stream().map(pair -> String.format("%c[%s]", pair.getKey().getChessChar(),
                GomokuFormatter.encodePoint(pair.getValue()))).collect(Collectors.toList());
        return StringUtils.join(sgf, ";");
    }

    public static Chessboard toChessboard(final String sgf) {
        final Chessboard board = new Chessboard();
        for (final Pair<ChessType, Point> move : toHistory(sgf)) {
            board.setChess(move.getRight(), move.getLeft());
        }
        return board;
    }

    public static List<Pair<ChessType, Point>> toHistory(final String sgf) {
        if (sgf == null) {
            throw new IllegalArgumentException("SGF cannot be null");
        }
        if (sgf.isEmpty()) {
            return List.of();
        }

        final Chessboard board = new Chessboard();
        final List<Pair<ChessType, Point>> history = new ArrayList<>();
        final String[] pieces = sgf.split(";", -1);
        for (final String piece : pieces) {
            if ((piece.length() != 5) || (piece.charAt(1) != '[') || (piece.charAt(4) != ']')) {
                throw new IllegalArgumentException("Invalid SGF move: " + piece);
            }
            final ChessType chessType = ChessType.getChessType(piece.charAt(0));
            if ((chessType == null) || (chessType == ChessType.EMPTY)) {
                throw new IllegalArgumentException("Invalid SGF move: " + piece);
            }
            final int row = decodeCoordinate(piece.charAt(2));
            final int column = decodeCoordinate(piece.charAt(3));
            if (board.getChess(row, column) != ChessType.EMPTY) {
                throw new IllegalArgumentException("Duplicate SGF move: " + piece);
            }
            board.setChess(row, column, chessType);
            history.add(Pair.of(chessType, new Point(row, column)));
        }
        return List.copyOf(history);
    }

    private static int decodeCoordinate(final char coordinate) {
        final int value = Character.digit(coordinate, 16);
        if ((value < 0) || (value >= GomokuConst.CHESSBOARD_SIZE)) {
            throw new IllegalArgumentException("SGF coordinate is out of range: " + coordinate);
        }
        return value;
    }

}
