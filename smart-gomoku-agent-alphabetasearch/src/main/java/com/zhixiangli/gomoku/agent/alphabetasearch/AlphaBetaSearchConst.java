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

    public class Cache {

        public static final int DURATION = 2;

        public static final long MAXIMUM_SIZE = 10000000;
    }

    public class Estimate {

        public static final double WIN = 1L << 60;

        public static final double FIVE = 1e12;

        public static final double OPEN_FOUR = 1e11;

        public static final double DOUBLE_OPEN_THREE = 1e10;

        public static final double OPEN_AND_HALF_OPEN_THREE = 1e9;

        public static final double HALF_OPEN_FOUR = 1e8;

        public static final double OPEN_THREE = 1e7;

        public static final double SPACED_OPEN_THREE = 1e6;

        public static final double DOUBLE_OPEN_TWO = 1e5;

        public static final double HALF_OPEN_THREE = 1e4;

        public static final double OPEN_TWO = 1e3;

        public static final double ONE_SPACED_OPEN_TWO = 1e2;

        public static final double TWO_SPACED_OPEN_TWO = 1e1;

        public static final double HALF_OPEN_TWO = 1;

    }
}
