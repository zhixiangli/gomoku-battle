package com.zhixiangli.gomoku.core.common;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class GomokuFormatterTest {

    @Test
    public void emptyHistoryCreatesAnEmptyChessboard() {
        final Chessboard chessboard = GomokuFormatter.toChessboard("");

        assertEquals(ChessType.EMPTY, chessboard.getChess(7, 7));
    }

    @Test
    public void malformedSgfIsRejected() {
        assertInvalid("B[77");
        assertInvalid("B[7f]");
        assertInvalid("B[77];W[77]");
    }

    @Test
    public void arbitraryBoardPositionsDoNotRequireMoveOrder() {
        final Chessboard chessboard = GomokuFormatter.toChessboard("W[77];W[78]");

        assertEquals(ChessType.WHITE, chessboard.getChess(7, 7));
        assertEquals(ChessType.WHITE, chessboard.getChess(7, 8));
    }

    private void assertInvalid(final String sgf) {
        try {
            GomokuFormatter.toChessboard(sgf);
            fail("Expected invalid SGF to be rejected: " + sgf);
        } catch (final IllegalArgumentException expected) {
            // expected
        }
    }

}
