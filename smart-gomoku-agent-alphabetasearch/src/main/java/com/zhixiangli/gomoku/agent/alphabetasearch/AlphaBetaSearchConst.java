/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.zhixiangli.gomoku.core.analysis.ChessPatternType;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchConst {

    public class Search {

        public static final double DECAY_FACTOR = 0.999;

        public static final int AROUND_CANDIDATE_RANGE = 2;

        public static final int MAX_CANDIDATE_NUM = 10;

        public static final int MAX_DEPTH = 6;

    }

    public class Cache {

        public static final int DURATION = 2;

        public static final long MAXIMUM_SIZE = 10000000;
    }

    public static final ImmutableMap<ChessPatternType, Double> ESTIMATED_MAP = Maps
            .immutableEnumMap(new HashMap<ChessPatternType, Double>() {
                private static final long serialVersionUID = -4224644576326725571L;
                {
                    put(ChessPatternType.FIVE, 1e25);
                    put(ChessPatternType.OPEN_FOUR, 1e6);
                    put(ChessPatternType.HALF_OPEN_FOUR, 1e5);
                    put(ChessPatternType.OPEN_THREE, 5e4);
                    put(ChessPatternType.SPACED_OPEN_THREE, 1e4);
                    put(ChessPatternType.HALF_OPEN_THREE, 1e3);
                    put(ChessPatternType.OPEN_TWO, 2e2);
                    put(ChessPatternType.ONE_SPACED_OPEN_TWO, 5e1);
                    put(ChessPatternType.TWO_SPACED_OPEN_TWO, 1e1);
                    put(ChessPatternType.HALF_OPEN_TWO, 1e0);
                    put(ChessPatternType.OTHERS, 0.0);
                }
            });

}
