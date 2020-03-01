package com.zhixiangli.gomoku.alphabetasearch.algorithm;

import com.zhixiangli.gomoku.core.analysis.PatternType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

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

/**
 * @author zhixiangli
 */
public class AlphaBetaSearchProphetTest {

    @Before
    public void setUp() {
    }

    @Test
    public void evaluateChessPatternType() {
        final List<List<PatternType>> types = new ArrayList<>();

        // victory
        types.add(Collections.singletonList(FIVE));

        types.add(Collections.singletonList(OPEN_FOUR));

        types.add(Arrays.asList(HALF_OPEN_FOUR, HALF_OPEN_FOUR));

        types.add(Arrays.asList(HALF_OPEN_FOUR, OPEN_THREE));
        types.add(Arrays.asList(HALF_OPEN_FOUR, SPACED_OPEN_THREE));

        types.add(Arrays.asList(OPEN_THREE, OPEN_THREE));
        types.add(Arrays.asList(OPEN_THREE, SPACED_OPEN_THREE));
        types.add(Arrays.asList(SPACED_OPEN_THREE, SPACED_OPEN_THREE));

        // have a chance to win
        types.add(Arrays.asList(OPEN_THREE, HALF_OPEN_THREE));
        types.add(Arrays.asList(SPACED_OPEN_THREE, HALF_OPEN_THREE));

        types.add(Collections.singletonList(HALF_OPEN_FOUR));

        types.add(Collections.singletonList(OPEN_THREE));
        types.add(Collections.singletonList(SPACED_OPEN_THREE));

        types.add(Arrays.asList(OPEN_TWO, OPEN_TWO));
        types.add(Arrays.asList(OPEN_TWO, ONE_SPACED_OPEN_TWO));
        types.add(Arrays.asList(OPEN_TWO, TWO_SPACED_OPEN_TWO));
        types.add(Arrays.asList(ONE_SPACED_OPEN_TWO, ONE_SPACED_OPEN_TWO));
        types.add(Arrays.asList(ONE_SPACED_OPEN_TWO, TWO_SPACED_OPEN_TWO));
        types.add(Arrays.asList(TWO_SPACED_OPEN_TWO, TWO_SPACED_OPEN_TWO));

        types.add(Collections.singletonList(HALF_OPEN_THREE));

        types.add(Collections.singletonList(OPEN_TWO));

        types.add(Collections.singletonList(ONE_SPACED_OPEN_TWO));

        types.add(Collections.singletonList(TWO_SPACED_OPEN_TWO));

        types.add(Collections.singletonList(HALF_OPEN_TWO));

        types.add(Collections.singletonList(OTHERS));

        for (int i = 0; (i + 1) < types.size(); ++i) {
            final double first = evaluateChessPatternType(types.get(i));
            System.out.println(String.format("%s = %f", types.get(i), first));
            final double second = evaluateChessPatternType(types.get(i + 1));
            if (first < second) {
                System.out.println(
                        String.format("%s(%f) should >= %s(%f)", types.get(i), first, types.get(i + 1), second));
            }
            Assert.assertTrue(first >= second);
        }
    }

    private double evaluateChessPatternType(final List<PatternType> type) {
        final Map<PatternType, Integer> counter = new EnumMap<>(PatternType.class);
        type.forEach(pattern -> counter.compute(pattern, (k, v) -> (null == v) ? 1 : (v + 1)));
        return AlphaBetaSearchProphet.evaluateChessPatternType(counter)
                * ((type.size() == 1) ? type.get(0).getChessNum() : 1);
    }

}
