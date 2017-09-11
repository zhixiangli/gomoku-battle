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

    public static final ImmutableMap<PatternType, Integer> EVALUATION = Maps
            .immutableEnumMap(new HashMap<PatternType, Integer>() {
                private static final long serialVersionUID = 1L;

                {
                    put(PatternType.FIVE, 100000000);

                    put(PatternType.OPEN_FOUR, 100000);

                    put(PatternType.HALF_OPEN_FOUR, 10000);

                    put(PatternType.OPEN_THREE, 2000);
                    put(PatternType.SPACED_OPEN_THREE, 1000);

                    put(PatternType.HALF_OPEN_THREE, 100);

                    put(PatternType.OPEN_TWO, 30);
                    put(PatternType.ONE_SPACED_OPEN_TWO, 20);
                    put(PatternType.TWO_SPACED_OPEN_TWO, 10);

                    put(PatternType.HALF_OPEN_TWO, 1);

                    put(PatternType.OTHERS, 0);
                }
            });

}
