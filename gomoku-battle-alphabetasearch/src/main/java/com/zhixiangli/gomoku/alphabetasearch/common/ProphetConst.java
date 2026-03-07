package com.zhixiangli.gomoku.alphabetasearch.common;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.zhixiangli.gomoku.core.analysis.PatternType;

import java.util.HashMap;

/**
 * @author zhixiangli
 */
public class ProphetConst {

    public static final ImmutableMap<PatternType, Double> EVALUATION = Maps
            .immutableEnumMap(new HashMap<PatternType, Double>() {
                private static final long serialVersionUID = 1L;

                {
                    put(PatternType.FIVE, 1e8);

                    // Winning threat hierarchy (OPEN_FOUR >> HALF_OPEN_FOUR >> OPEN_THREE ...)
                    // is tuned so that search strongly prefers immediate forcing moves.
                    put(PatternType.OPEN_FOUR, 1e6);

                    put(PatternType.HALF_OPEN_FOUR, 8e4);

                    put(PatternType.OPEN_THREE, 1e4);
                    put(PatternType.SPACED_OPEN_THREE, 6e3);

                    put(PatternType.HALF_OPEN_THREE, 8e2);

                    put(PatternType.OPEN_TWO, 2e2);
                    put(PatternType.ONE_SPACED_OPEN_TWO, 1.2e2);
                    put(PatternType.TWO_SPACED_OPEN_TWO, 5e1);

                    put(PatternType.HALF_OPEN_TWO, 1e1);

                    put(PatternType.OTHERS, 0.0);
                }
            });

    private ProphetConst() {
    }
}
