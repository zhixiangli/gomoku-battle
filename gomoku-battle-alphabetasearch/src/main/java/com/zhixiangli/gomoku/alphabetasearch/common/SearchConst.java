package com.zhixiangli.gomoku.alphabetasearch.common;

/**
 * Constants used by the alpha-beta pruning search algorithm for the Gomoku game.
 *
 * @author zhixiangli
 */
public class SearchConst {

    /**
     * Decay factor applied to the evaluation score at each depth level.
     * A value slightly less than 1.0 encourages the algorithm to prefer
     * shorter winning paths and longer losing paths, making the AI
     * play more aggressively when it has an advantage.
     */
    public static final double DECAY_FACTOR = 0.999;

    /**
     * The Chebyshev distance (in cells) used to find candidate moves around
     * existing stones. For each occupied cell at (row, col), a square region
     * from (row - range, col - range) to (row + range, col + range) is scanned
     * and any empty position within that region becomes a candidate move.
     * This limits the search space to positions near existing stones.
     */
    public static final int AROUND_CANDIDATE_RANGE = 2;

    /**
     * The maximum number of candidate moves to evaluate at each search node.
     * Candidates are ranked by a heuristic evaluation and only the top moves
     * are explored further, keeping the branching factor manageable.
     */
    public static final int MAX_CANDIDATE_NUM = 10;

    /**
     * The maximum depth (in plies) that the alpha-beta search will explore.
     * A higher value allows the AI to look further ahead, improving play
     * strength at the cost of increased computation time.
     */
    public static final int MAX_DEPTH = 7;

    private SearchConst() {
    }
}
