/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchConst {

    public class Search {

        public static final double DECAY_FACTOR = 0.999;

        public static final int AROUND_CANDIDATE_RANGE = 2;

        public static final int MAX_CANDIDATE_NUM = 11;

        public static final int MAX_DEPTH = 5;

    }

    public class Estimate {

        public static final double WIN = 1L << 30;

        public static final double FIVE = 1L << 27;

        public static final double OPEN_FOUR = 1L << 24;

        public static final double HALF_OPEN_FOUR = 1L << 21;

        public static final double OPEN_THREE = 1L << 18;

        public static final double SPACED_OPEN_THREE = 1L << 15;

        public static final double HALF_OPEN_THREE = 1L << 12;

        public static final double OPEN_TWO = 1L << 9;

        public static final double ONE_SPACED_OPEN_TWO = 1L << 6;

        public static final double TWO_SPACED_OPEN_TWO = 1L << 3;

        public static final double HALF_OPEN_TWO = 1L;

    }
}
