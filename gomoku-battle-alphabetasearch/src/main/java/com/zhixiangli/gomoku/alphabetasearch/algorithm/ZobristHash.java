package com.zhixiangli.gomoku.alphabetasearch.algorithm;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;

import java.util.Random;

/**
 * Zobrist hashing for O(1) incremental board position hashing.
 * Uses XOR-based scheme: toggling a piece on/off is the same operation.
 *
 * @author zhixiangli
 */
public final class ZobristHash {

    private static final long[][][] TABLE =
            new long[GomokuConst.CHESSBOARD_SIZE][GomokuConst.CHESSBOARD_SIZE][2];

    static {
        final Random rng = new Random(0x5A0B51517L);
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; i++) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; j++) {
                TABLE[i][j][0] = rng.nextLong();
                TABLE[i][j][1] = rng.nextLong();
            }
        }
    }

    private ZobristHash() {
    }

    /**
     * Get the hash component for a single piece.
     * XOR this value into the running hash to add or remove the piece.
     */
    public static long pieceHash(final int row, final int col, final ChessType type) {
        return TABLE[row][col][type == ChessType.BLACK ? 0 : 1];
    }

    /**
     * Compute the full Zobrist hash of a board from scratch.
     */
    public static long computeHash(final Chessboard board) {
        long hash = 0;
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; i++) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; j++) {
                final ChessType type = board.getChess(i, j);
                if (type != ChessType.EMPTY) {
                    hash ^= pieceHash(i, j, type);
                }
            }
        }
        return hash;
    }
}
