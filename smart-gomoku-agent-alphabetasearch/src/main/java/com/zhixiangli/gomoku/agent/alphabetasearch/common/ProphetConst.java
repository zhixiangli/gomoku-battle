/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch.common;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.zhixiangli.gomoku.core.chessboard.PatternType;

/**
 * @author zhixiangli
 *
 */
public class ProphetConst {

    public static final ImmutableMap<PatternType, Double> EVALUATION = Maps
            .immutableEnumMap(new HashMap<PatternType, Double>() {
                private static final long serialVersionUID = 1L;

                {
                    put(PatternType.FIVE, 1e25);

                    put(PatternType.OPEN_FOUR, 1e6);

                    put(PatternType.HALF_OPEN_FOUR, 300.0);

                    put(PatternType.OPEN_THREE, 300.0);
                    put(PatternType.SPACED_OPEN_THREE, 300.0);

                    put(PatternType.HALF_OPEN_THREE, 250.0);

                    put(PatternType.OPEN_TWO, 200.0);
                    put(PatternType.ONE_SPACED_OPEN_TWO, 180.0);
                    put(PatternType.TWO_SPACED_OPEN_TWO, 150.0);

                    put(PatternType.HALF_OPEN_TWO, 100.0);

                    put(PatternType.OTHERS, 0.0);
                }
            });

}
