/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch.algorithm;

import static com.zhixiangli.gomoku.core.analysis.PatternType.FIVE;
import static com.zhixiangli.gomoku.core.analysis.PatternType.HALF_OPEN_FOUR;
import static com.zhixiangli.gomoku.core.analysis.PatternType.HALF_OPEN_THREE;
import static com.zhixiangli.gomoku.core.analysis.PatternType.HALF_OPEN_TWO;
import static com.zhixiangli.gomoku.core.analysis.PatternType.ONE_SPACED_OPEN_TWO;
import static com.zhixiangli.gomoku.core.analysis.PatternType.OPEN_FOUR;
import static com.zhixiangli.gomoku.core.analysis.PatternType.OPEN_THREE;
import static com.zhixiangli.gomoku.core.analysis.PatternType.OPEN_TWO;
import static com.zhixiangli.gomoku.core.analysis.PatternType.OTHERS;
import static com.zhixiangli.gomoku.core.analysis.PatternType.SPACED_OPEN_THREE;
import static com.zhixiangli.gomoku.core.analysis.PatternType.TWO_SPACED_OPEN_TWO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zhixiangli.gomoku.core.analysis.PatternType;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchProphetTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void evaluateChessPatternType() {
        List<List<PatternType>> types = new ArrayList<>();

        // victory
        types.add(Arrays.asList(FIVE));

        types.add(Arrays.asList(OPEN_FOUR));

        types.add(Arrays.asList(HALF_OPEN_FOUR, HALF_OPEN_FOUR));

        types.add(Arrays.asList(HALF_OPEN_FOUR, OPEN_THREE));
        types.add(Arrays.asList(HALF_OPEN_FOUR, SPACED_OPEN_THREE));

        types.add(Arrays.asList(OPEN_THREE, OPEN_THREE));
        types.add(Arrays.asList(OPEN_THREE, SPACED_OPEN_THREE));
        types.add(Arrays.asList(SPACED_OPEN_THREE, SPACED_OPEN_THREE));

        // have a chance to win
        types.add(Arrays.asList(OPEN_THREE, HALF_OPEN_THREE));
        types.add(Arrays.asList(SPACED_OPEN_THREE, HALF_OPEN_THREE));

        types.add(Arrays.asList(HALF_OPEN_FOUR));

        types.add(Arrays.asList(OPEN_THREE));
        types.add(Arrays.asList(SPACED_OPEN_THREE));

        types.add(Arrays.asList(OPEN_TWO, OPEN_TWO));
        types.add(Arrays.asList(OPEN_TWO, ONE_SPACED_OPEN_TWO));
        types.add(Arrays.asList(OPEN_TWO, TWO_SPACED_OPEN_TWO));
        types.add(Arrays.asList(ONE_SPACED_OPEN_TWO, ONE_SPACED_OPEN_TWO));
        types.add(Arrays.asList(ONE_SPACED_OPEN_TWO, TWO_SPACED_OPEN_TWO));
        types.add(Arrays.asList(TWO_SPACED_OPEN_TWO, TWO_SPACED_OPEN_TWO));

        types.add(Arrays.asList(HALF_OPEN_THREE));

        types.add(Arrays.asList(OPEN_TWO));

        types.add(Arrays.asList(ONE_SPACED_OPEN_TWO));

        types.add(Arrays.asList(TWO_SPACED_OPEN_TWO));

        types.add(Arrays.asList(HALF_OPEN_TWO));

        types.add(Arrays.asList(OTHERS));

        for (int i = 0; i + 1 < types.size(); ++i) {
            int first = this.evaluateChessPatternType(types.get(i));
            System.out.println(String.format("%s = %d", types.get(i), first));
            int second = this.evaluateChessPatternType(types.get(i + 1));
            if (first < second) {
                System.out.println(
                        String.format("%s(%d) should >= %s(%d)", types.get(i), first, types.get(i + 1), second));
            }
            Assert.assertTrue(first >= second);
        }
    }

    private int evaluateChessPatternType(List<PatternType> type) {
        Map<PatternType, Integer> counter = new EnumMap<>(PatternType.class);
        type.stream().forEach(pattern -> counter.compute(pattern, (k, v) -> null == v ? 1 : v + 1));
        return AlphaBetaSearchProphet.evaluateChessPatternType(counter)
                * (type.size() == 1 ? type.get(0).getChessNum() : 1);
    }

}
