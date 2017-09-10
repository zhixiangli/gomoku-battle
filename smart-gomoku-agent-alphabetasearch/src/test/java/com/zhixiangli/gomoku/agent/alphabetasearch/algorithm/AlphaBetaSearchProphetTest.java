/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch.algorithm;

import static com.zhixiangli.gomoku.core.chessboard.PatternType.FIVE;
import static com.zhixiangli.gomoku.core.chessboard.PatternType.HALF_OPEN_FOUR;
import static com.zhixiangli.gomoku.core.chessboard.PatternType.HALF_OPEN_THREE;
import static com.zhixiangli.gomoku.core.chessboard.PatternType.HALF_OPEN_TWO;
import static com.zhixiangli.gomoku.core.chessboard.PatternType.ONE_SPACED_OPEN_TWO;
import static com.zhixiangli.gomoku.core.chessboard.PatternType.OPEN_FOUR;
import static com.zhixiangli.gomoku.core.chessboard.PatternType.OPEN_THREE;
import static com.zhixiangli.gomoku.core.chessboard.PatternType.OPEN_TWO;
import static com.zhixiangli.gomoku.core.chessboard.PatternType.OTHERS;
import static com.zhixiangli.gomoku.core.chessboard.PatternType.SPACED_OPEN_THREE;
import static com.zhixiangli.gomoku.core.chessboard.PatternType.TWO_SPACED_OPEN_TWO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.zhixiangli.gomoku.core.chessboard.PatternType;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchProphetTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void test() {
        System.out.println(StringUtils.join(PatternType.values(), " "));
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

        types.add(Arrays.asList(OPEN_THREE, HALF_OPEN_THREE));
        types.add(Arrays.asList(SPACED_OPEN_THREE, HALF_OPEN_THREE));

        // have a chance to win

        // open two + open two -> half open two + open three -> half open two + half
        // open four

        types.add(Arrays.asList(OPEN_THREE, HALF_OPEN_TWO));
        types.add(Arrays.asList(SPACED_OPEN_THREE, HALF_OPEN_TWO));

        types.add(Arrays.asList(OPEN_TWO, OPEN_TWO));
        types.add(Arrays.asList(OPEN_TWO, ONE_SPACED_OPEN_TWO));
        types.add(Arrays.asList(OPEN_TWO, TWO_SPACED_OPEN_TWO));
        types.add(Arrays.asList(ONE_SPACED_OPEN_TWO, ONE_SPACED_OPEN_TWO));
        types.add(Arrays.asList(ONE_SPACED_OPEN_TWO, TWO_SPACED_OPEN_TWO));
        types.add(Arrays.asList(TWO_SPACED_OPEN_TWO, TWO_SPACED_OPEN_TWO));

        types.add(Arrays.asList(HALF_OPEN_FOUR));

        types.add(Arrays.asList(OPEN_THREE));

        types.add(Arrays.asList(SPACED_OPEN_THREE));

        // half open two + open two -> half open two + half open three
        types.add(Arrays.asList(OPEN_TWO, HALF_OPEN_TWO));
        types.add(Arrays.asList(ONE_SPACED_OPEN_TWO, HALF_OPEN_TWO));
        types.add(Arrays.asList(TWO_SPACED_OPEN_TWO, HALF_OPEN_TWO));

        types.add(Arrays.asList(HALF_OPEN_THREE));

        types.add(Arrays.asList(OPEN_TWO));

        types.add(Arrays.asList(ONE_SPACED_OPEN_TWO));

        types.add(Arrays.asList(TWO_SPACED_OPEN_TWO));

        types.add(Arrays.asList(HALF_OPEN_TWO));

        types.add(Arrays.asList(OTHERS));

        for (int i = 0; i + 1 < types.size(); ++i) {
            double first = AlphaBetaSearchProphet.evaluateChessPatternType(types.get(i));
            double second = AlphaBetaSearchProphet.evaluateChessPatternType(types.get(i + 1));
            if (first < second) {
                System.out.println(String.format("%s should larger than %s", types.get(i), types.get(i + 1)));
            }
            Assert.assertTrue(first >= second);
        }
    }
}
