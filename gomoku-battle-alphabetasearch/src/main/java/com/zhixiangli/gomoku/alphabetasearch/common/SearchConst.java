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
     * The range (in number of cells) around existing stones to consider
     * when generating candidate moves. Only empty positions within this
     * distance from an already-placed stone are evaluated, which significantly
     * reduces the search space on a large board.
     */
    public static final int AROUND_CANDIDATE_RANGE = 2;

    /**
     * The maximum number of candidate moves to evaluate at each search node.
     * Candidates are ranked by a heuristic evaluation and only the top moves
     * are explored further, keeping the branching factor manageable.
     */
    public static final int MAX_CANDIDATE_NUM = 15;

    /**
     * The maximum depth (in plies) that the alpha-beta search will explore.
     * A higher value allows the AI to look further ahead, improving play
     * strength at the cost of increased computation time.
     */
    public static final int MAX_DEPTH = 7;

    private SearchConst() {
    }
}
