package com.zhixiangli.gomoku.alphabetasearch.algorithm;

import java.awt.Point;

/**
 * Array-based transposition table using Zobrist hashing.
 * Each entry stores the search depth, value, bound flag, and best move
 * to enable correct reuse across transpositions.
 *
 * @author zhixiangli
 */
public final class TranspositionTable {

    /** The stored value is the exact minimax value. */
    public static final int EXACT = 0;
    /** The stored value is a lower bound (beta cutoff in MAX node). */
    public static final int LOWER_BOUND = 1;
    /** The stored value is an upper bound (alpha cutoff in MIN node). */
    public static final int UPPER_BOUND = 2;

    private final long[] hashes;
    private final int[] depths;
    private final double[] values;
    private final int[] flags;
    private final int[] bestMoveRows;
    private final int[] bestMoveCols;
    private final int mask;

    /**
     * @param sizePowerOf2 log2 of the table size (e.g. 20 for ~1M entries)
     */
    public TranspositionTable(final int sizePowerOf2) {
        final int size = 1 << sizePowerOf2;
        this.mask = size - 1;
        this.hashes = new long[size];
        this.depths = new int[size];
        this.values = new double[size];
        this.flags = new int[size];
        this.bestMoveRows = new int[size];
        this.bestMoveCols = new int[size];
        clear();
    }

    /**
     * Probe the table for a matching entry.
     *
     * @return true if a valid entry was found for this hash
     */
    public boolean probe(final long hash, final ProbeResult result) {
        final int index = (int) (hash & mask);
        if (hashes[index] == hash && depths[index] >= 0) {
            result.depth = depths[index];
            result.value = values[index];
            result.flag = flags[index];
            result.bestMoveRow = bestMoveRows[index];
            result.bestMoveCol = bestMoveCols[index];
            return true;
        }
        return false;
    }

    /**
     * Store an entry in the table. Uses an always-replace policy with
     * depth preference: deeper entries are preferred over shallower ones.
     */
    public void store(final long hash, final int depth, final double value,
                      final int flag, final Point bestMove) {
        final int index = (int) (hash & mask);
        // Replace if: different position, or new search is at least as deep
        if (hashes[index] != hash || depth >= depths[index]) {
            hashes[index] = hash;
            depths[index] = depth;
            values[index] = value;
            flags[index] = flag;
            if (bestMove != null) {
                bestMoveRows[index] = bestMove.x;
                bestMoveCols[index] = bestMove.y;
            } else {
                bestMoveRows[index] = -1;
                bestMoveCols[index] = -1;
            }
        }
    }

    public void clear() {
        for (int i = 0; i < hashes.length; i++) {
            hashes[i] = 0;
            depths[i] = -1;
            values[i] = 0;
            flags[i] = 0;
            bestMoveRows[i] = -1;
            bestMoveCols[i] = -1;
        }
    }

    /**
     * Reusable result object to avoid allocation during probing.
     */
    public static final class ProbeResult {
        public int depth;
        public double value;
        public int flag;
        public int bestMoveRow;
        public int bestMoveCol;
    }
}
