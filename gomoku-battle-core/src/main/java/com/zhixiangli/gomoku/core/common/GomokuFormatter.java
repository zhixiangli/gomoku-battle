/**
 * 
 */
package com.zhixiangli.gomoku.core.common;

import java.awt.Point;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;

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

    public static final String toSGF(List<Pair<ChessType, Point>> history) {
        List<String> sgf = history.stream().map(pair -> String.format("%c[%s]", pair.getKey().getChessChar(),
                GomokuFormatter.encodePoint(pair.getValue()))).collect(Collectors.toList());
        return StringUtils.join(sgf, ";");
    }

    public static final Chessboard toChessboard(String sgf) {
        String[] pieces = StringUtils.split(sgf, ';');
        Chessboard board = new Chessboard();
        for (String piece : pieces) {
            int row = GomokuFormatter.decodeAxis(piece.charAt(2));
            int column = GomokuFormatter.decodeAxis(piece.charAt(3));
            board.setChess(row, column, ChessType.getChessType(piece.charAt(0)));
        }
        return board;
    }

}
