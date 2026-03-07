package com.zhixiangli.gomoku.alphabetasearch.algorithm;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.alphabetasearch.common.ProphetConst;
import com.zhixiangli.gomoku.alphabetasearch.common.SearchConst;
import com.zhixiangli.gomoku.core.analysis.GameReferee;
import com.zhixiangli.gomoku.core.analysis.GlobalAnalyser;
import com.zhixiangli.gomoku.core.analysis.PatternType;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Alpha-beta search with Zobrist-hashed transposition table, iterative-deepening
 * move ordering, killer move heuristic, history heuristic, late move reduction,
 * and lazy move generation to minimize expensive candidate evaluation.
 *
 * @author zhixiangli
 */
public class AlphaBetaSearchAlgorithm {

    private static final int TT_SIZE_POWER = 21; // 2^21 ≈ 2M entries

    /** Depth reduction for late moves. */
    private static final int LMR_REDUCTION = 2;

    /** Minimum depth to apply late move reduction. */
    private static final int LMR_MIN_DEPTH = 3;

    /** Moves searched before applying LMR (first N moves get full depth). */
    private static final int LMR_FULL_DEPTH_MOVES = 3;

    /** Threshold for considering a move as a strong threat (OPEN_FOUR level). */
    private static final double THREAT_THRESHOLD = ProphetConst.EVALUATION.get(PatternType.OPEN_FOUR) * 0.5;

    private final TranspositionTable tt;

    private final boolean isEnableCache;

    /** Killer moves: two slots per depth level. */
    private final Point[][] killerMoves;

    /** History heuristic table: indexed by [row * BOARD_SIZE + col]. */
    private final int[] historyTable;

    private final TranspositionTable.ProbeResult probeResult = new TranspositionTable.ProbeResult();

    public AlphaBetaSearchAlgorithm() {
        this(true);
    }

    public AlphaBetaSearchAlgorithm(final boolean isEnableCache) {
        this.isEnableCache = isEnableCache;
        this.tt = new TranspositionTable(TT_SIZE_POWER);
        this.killerMoves = new Point[SearchConst.MAX_DEPTH + 2][2];
        this.historyTable = new int[GomokuConst.CHESSBOARD_SIZE * GomokuConst.CHESSBOARD_SIZE];
    }

    double clearCacheAndSearch(
            final int depth, final double alpha, final double beta,
            final Chessboard chessboard, final Point point, final ChessType currentChessType,
            final ChessType rootChessType, final String path) throws Exception {
        clearCache();
        return search(depth, alpha, beta, chessboard, point, currentChessType, rootChessType, path);
    }

    /**
     * @param depth            the current search depth.
     * @param alpha            the lower bound estimation of root chess type.
     * @param beta             the upper bound estimation of root chess type.
     * @param chessboard       the current chessboard.
     * @param point            the point has been put.
     * @param currentChessType the chess type has been put.
     * @return the evaluation value from rootChessType's perspective.
     */
    public final double search(
            final int depth, final double alpha, final double beta, final Chessboard chessboard, final Point point,
            final ChessType currentChessType, final ChessType rootChessType, final String path) throws Exception {
        Preconditions.checkArgument(GameReferee.isInChessboard(point));
        Preconditions.checkArgument(chessboard.getChess(point) != ChessType.EMPTY);
        Preconditions.checkArgument(currentChessType != ChessType.EMPTY);

        final long hash = ZobristHash.computeHash(chessboard);
        return searchInternal(depth, alpha, beta, chessboard, point, currentChessType, rootChessType, hash);
    }

    /**
     * Internal search using Zobrist hash for transposition table lookups.
     * Includes: TT probe/store, lazy move generation, move ordering,
     * killer moves, history heuristic, and late move reduction (LMR).
     */
    final double searchInternal(
            final int depth, double alpha, double beta, final Chessboard chessboard, final Point point,
            final ChessType currentChessType, final ChessType rootChessType, final long hash) {

        // Terminal: win check
        if (GameReferee.isWin(chessboard, point)) {
            final double maxValue = ProphetConst.EVALUATION.get(PatternType.FIVE);
            final double result = (rootChessType == currentChessType) ? maxValue : -maxValue;
            return result * SearchConst.DECAY_FACTOR;
        }

        // Leaf node
        if (depth <= 0) {
            return AlphaBetaSearchProphet.evaluateChessboardValue(chessboard, rootChessType)
                    * SearchConst.DECAY_FACTOR;
        }

        // --- Transposition table probe ---
        Point ttBestMove = null;
        if (isEnableCache) {
            if (tt.probe(hash, probeResult)) {
                if (probeResult.depth >= depth) {
                    if (probeResult.flag == TranspositionTable.EXACT) {
                        return probeResult.value;
                    }
                    if (probeResult.flag == TranspositionTable.LOWER_BOUND) {
                        if (probeResult.value >= beta) {
                            return probeResult.value;
                        }
                        // Narrow the window using the lower bound
                        alpha = Math.max(alpha, probeResult.value);
                    }
                    if (probeResult.flag == TranspositionTable.UPPER_BOUND) {
                        if (probeResult.value <= alpha) {
                            return probeResult.value;
                        }
                        // Narrow the window using the upper bound
                        beta = Math.min(beta, probeResult.value);
                    }
                }
                // Always use TT best move for ordering, regardless of stored depth
                if (probeResult.bestMoveRow >= 0) {
                    ttBestMove = new Point(probeResult.bestMoveRow, probeResult.bestMoveCol);
                }
            }
        }

        final ChessType nextChessType = GameReferee.nextChessType(currentChessType);
        final boolean isMinNode = (rootChessType == currentChessType);
        final double origAlpha = alpha;
        final double origBeta = beta;
        double result = isMinNode ? Double.MAX_VALUE : -Double.MAX_VALUE;
        Point bestMove = null;
        int moveIndex = 0;

        // --- Lazy move generation: try TT best move first to avoid expensive nextMoves ---
        if (isEnableCache && ttBestMove != null && chessboard.getChess(ttBestMove) == ChessType.EMPTY) {
            chessboard.setChess(ttBestMove, nextChessType);
            final long newHash = hash ^ ZobristHash.pieceHash(ttBestMove.x, ttBestMove.y, nextChessType);
            final double searchValue = searchInternal(depth - 1, alpha, beta, chessboard,
                    ttBestMove, nextChessType, rootChessType, newHash);
            chessboard.setChess(ttBestMove, ChessType.EMPTY);

            bestMove = ttBestMove;
            if (isMinNode) {
                result = searchValue;
                beta = Math.min(beta, searchValue);
            } else {
                result = searchValue;
                alpha = Math.max(alpha, searchValue);
            }
            moveIndex = 1;

            if (beta <= alpha) {
                recordKillerMove(ttBestMove, depth);
                updateHistory(ttBestMove, depth);
                return storeTTAndReturn(hash, depth, result * SearchConst.DECAY_FACTOR,
                        origAlpha, origBeta, bestMove);
            }
        }

        // Generate full candidate list (expensive but needed)
        final int candidateLimit = Math.min(SearchConst.MAX_CANDIDATE_NUM, Math.max(5, 3 + depth));
        final Point[] candidateMoves = nextMoves(chessboard, candidateLimit);
        if (candidateMoves.length == 0 && bestMove == null) {
            return AlphaBetaSearchProphet.evaluateChessboardValue(chessboard, rootChessType)
                    * SearchConst.DECAY_FACTOR;
        }

        // Reorder and filter (skip TT best move as already searched)
        final Point[] orderedMoves = orderMoves(candidateMoves, ttBestMove, depth);

        // Search remaining candidates
        for (final Point nextPoint : orderedMoves) {
            // Skip TT best move (already searched above)
            if (ttBestMove != null && nextPoint.x == ttBestMove.x && nextPoint.y == ttBestMove.y) {
                continue;
            }

            chessboard.setChess(nextPoint, nextChessType);
            final long newHash = hash ^ ZobristHash.pieceHash(nextPoint.x, nextPoint.y, nextChessType);

            double searchValue;

            // Late move reduction: search later moves at reduced depth first
            if (isEnableCache && moveIndex >= LMR_FULL_DEPTH_MOVES && depth >= LMR_MIN_DEPTH
                    && !isSpecialMove(nextPoint, ttBestMove, depth)) {
                final int reducedDepth = Math.max(0, depth - 1 - LMR_REDUCTION);
                searchValue = searchInternal(reducedDepth, alpha, beta, chessboard,
                        nextPoint, nextChessType, rootChessType, newHash);
                // Re-search at full depth if it looks promising
                final boolean promising = isMinNode ? (searchValue < beta) : (searchValue > alpha);
                if (promising) {
                    searchValue = searchInternal(depth - 1, alpha, beta, chessboard,
                            nextPoint, nextChessType, rootChessType, newHash);
                }
            } else {
                searchValue = searchInternal(depth - 1, alpha, beta, chessboard,
                        nextPoint, nextChessType, rootChessType, newHash);
            }

            chessboard.setChess(nextPoint, ChessType.EMPTY);

            if (isMinNode) {
                if (searchValue < result) {
                    result = searchValue;
                    bestMove = nextPoint;
                }
                beta = Math.min(beta, searchValue);
            } else {
                if (searchValue > result) {
                    result = searchValue;
                    bestMove = nextPoint;
                }
                alpha = Math.max(alpha, searchValue);
            }

            if (beta <= alpha) {
                recordKillerMove(nextPoint, depth);
                updateHistory(nextPoint, depth);
                break;
            }
            moveIndex++;
        }

        if (bestMove == null && candidateMoves.length > 0) {
            bestMove = candidateMoves[0];
        }

        return storeTTAndReturn(hash, depth, result * SearchConst.DECAY_FACTOR,
                origAlpha, origBeta, bestMove);
    }

    /**
     * Store result in TT and return the decayed value.
     */
    private double storeTTAndReturn(final long hash, final int depth, final double decayedResult,
                                    final double origAlpha, final double origBeta, final Point bestMove) {
        if (isEnableCache) {
            final int flag;
            if (decayedResult <= origAlpha) {
                flag = TranspositionTable.UPPER_BOUND;
            } else if (decayedResult >= origBeta) {
                flag = TranspositionTable.LOWER_BOUND;
            } else {
                flag = TranspositionTable.EXACT;
            }
            tt.store(hash, depth, decayedResult, flag, bestMove);
        }
        return decayedResult;
    }

    /**
     * Check if a move is "special" (TT best move or killer move) and should not be reduced.
     */
    private boolean isSpecialMove(final Point move, final Point ttBestMove, final int depth) {
        if (ttBestMove != null && move.x == ttBestMove.x && move.y == ttBestMove.y) {
            return true;
        }
        if (depth < killerMoves.length) {
            for (final Point killer : killerMoves[depth]) {
                if (killer != null && move.x == killer.x && move.y == killer.y) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Reorder candidate moves for better alpha-beta pruning.
     * Priority: TT best move > killer moves > history-sorted moves.
     */
    private Point[] orderMoves(final Point[] candidates, final Point ttBestMove, final int depth) {
        if (!isEnableCache) {
            return candidates;
        }
        final List<Point> ordered = new ArrayList<>(candidates.length);

        // 1. TT best move first (if in candidates)
        if (ttBestMove != null) {
            for (final Point p : candidates) {
                if (p.x == ttBestMove.x && p.y == ttBestMove.y) {
                    ordered.add(p);
                    break;
                }
            }
        }

        // 2. Killer moves
        if (depth < killerMoves.length) {
            for (final Point killer : killerMoves[depth]) {
                if (killer != null && (ttBestMove == null || killer.x != ttBestMove.x || killer.y != ttBestMove.y)) {
                    for (final Point p : candidates) {
                        if (p.x == killer.x && p.y == killer.y && !containsPoint(ordered, p)) {
                            ordered.add(p);
                            break;
                        }
                    }
                }
            }
        }

        // 3. Remaining moves sorted by history heuristic score
        final List<Pair<Point, Integer>> remaining = new ArrayList<>();
        for (final Point p : candidates) {
            if (!containsPoint(ordered, p)) {
                remaining.add(ImmutablePair.of(p, historyTable[p.x * GomokuConst.CHESSBOARD_SIZE + p.y]));
            }
        }
        remaining.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));
        for (final Pair<Point, Integer> pair : remaining) {
            ordered.add(pair.getKey());
        }

        return ordered.toArray(new Point[0]);
    }

    private static boolean containsPoint(final List<Point> list, final Point p) {
        for (final Point q : list) {
            if (q.x == p.x && q.y == p.y) {
                return true;
            }
        }
        return false;
    }

    private void recordKillerMove(final Point move, final int depth) {
        if (depth >= killerMoves.length) {
            return;
        }
        final Point existing = killerMoves[depth][0];
        final boolean isNewMove = (existing == null) || (existing.x != move.x) || (existing.y != move.y);
        if (isNewMove) {
            killerMoves[depth][1] = existing;
            killerMoves[depth][0] = move;
        }
    }

    private void updateHistory(final Point move, final int depth) {
        historyTable[move.x * GomokuConst.CHESSBOARD_SIZE + move.y] += depth * depth;
    }

    /**
     * Generate and score candidate moves with default limit.
     */
    public Point[] nextMoves(final Chessboard chessboard) {
        return nextMoves(chessboard, SearchConst.MAX_CANDIDATE_NUM);
    }

    /**
     * Generate and score candidate moves, returning the top candidates sorted by heuristic value.
     * Also detects forced moves: if a winning move or must-block move exists,
     * returns only that single move to avoid wasting search time.
     * Optimized to avoid Stream API overhead.
     *
     * @param maxCandidates maximum number of candidates to return
     */
    Point[] nextMoves(final Chessboard chessboard, final int maxCandidates) {
        final Point[] candidates = GlobalAnalyser.getEmptyPointsAround(chessboard, SearchConst.AROUND_CANDIDATE_RANGE);
        if (candidates.length == 0) {
            return candidates;
        }

        // Score all candidates using arrays to minimize allocation
        final double[] scores = new double[candidates.length];
        double maxScore = -Double.MAX_VALUE;
        for (int i = 0; i < candidates.length; i++) {
            scores[i] = AlphaBetaSearchProphet.evaluatePointValue(chessboard, candidates[i]);
            if (scores[i] > maxScore) {
                maxScore = scores[i];
            }
        }

        // If the best move is a strong threat (near OPEN_FOUR level), limit candidates
        if (maxScore >= THREAT_THRESHOLD) {
            int forcingCount = 0;
            for (int i = 0; i < candidates.length; i++) {
                if (scores[i] >= THREAT_THRESHOLD) {
                    forcingCount++;
                }
            }
            if (forcingCount > 0 && forcingCount <= 3) {
                final Point[] forcing = new Point[forcingCount];
                int idx = 0;
                for (int i = 0; i < candidates.length; i++) {
                    if (scores[i] >= THREAT_THRESHOLD) {
                        forcing[idx++] = candidates[i];
                    }
                }
                return forcing;
            }
        }

        // Partial sort: find top candidates by score
        final int limit = Math.min(maxCandidates, candidates.length);
        // Simple selection: use indices array sorted by score
        final Integer[] indices = new Integer[candidates.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = i;
        }
        java.util.Arrays.sort(indices, (a, b) -> Double.compare(scores[b], scores[a]));

        final Point[] result = new Point[limit];
        for (int i = 0; i < limit; i++) {
            result[i] = candidates[indices[i]];
        }
        return result;
    }

    public void clearCache() {
        tt.clear();
        for (int i = 0; i < killerMoves.length; i++) {
            killerMoves[i][0] = null;
            killerMoves[i][1] = null;
        }
        java.util.Arrays.fill(historyTable, 0);
    }
}
