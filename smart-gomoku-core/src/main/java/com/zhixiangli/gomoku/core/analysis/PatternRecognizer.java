/**
 * 
 */
package com.zhixiangli.gomoku.core.analysis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.common.GomokuConst;

/**
 * given a chess type list, which length is at most 6, recognize the pattern of
 * a chess type.
 * 
 * @author zhixiangli
 *
 */
public class PatternRecognizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatternRecognizer.class);

    private static final int SEED = ChessType.values().length;

    private static final int POW_SEED_SIX = (int) Math.pow(SEED, 6);

    private static final int PATTERN_MAX_LENGTH = GomokuConst.CONSECUTIVE_NUM + 1;

    private static final int PATTERN_HASH_BOUND = 2 * (int) Math.pow(SEED, PATTERN_MAX_LENGTH);

    private static final PatternType[] BLACK_PATTERN_MAP = new PatternType[PATTERN_HASH_BOUND];

    private static final PatternType[] WHITE_PATTERN_MAP = new PatternType[PATTERN_HASH_BOUND];

    static {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        fillChessPatternTypes(new ArrayList<>());

        stopWatch.stop();
        LOGGER.info("fill chess pattern type finish. cost: " + stopWatch.getTime(TimeUnit.MILLISECONDS));
    }

    private static final void fillChessPatternTypes(List<ChessType> pattern) {
        for (ChessType chessType : Arrays.asList(ChessType.BLACK, ChessType.WHITE)) {
            getPatternType(pattern, chessType);
        }
        if (pattern.size() >= PATTERN_MAX_LENGTH) {
            return;
        }
        for (ChessType chessType : ChessType.values()) {
            pattern.add(chessType);
            fillChessPatternTypes(pattern);
            pattern.remove(pattern.size() - 1);
        }
    }

    public static final PatternType getBestPatternType(List<ChessType> pattern, ChessType consideredChessType) {
        if (pattern.size() <= PATTERN_MAX_LENGTH) {
            return PatternRecognizer.getPatternType(pattern, consideredChessType);
        }
        // (((((1*seed+s[0])*seed+s[1])*seed+s[2])*seed+s[3])*seed+s[4])*seed+s[5]
        // f(0,6)=seed^6+s[0]*seed^5+s[1]*seed^4+s[2]*seed^3+s[3]*seed^2+s[4]*seed+s[5]
        // f(1,7)=seed*f(0,6)-seed^7-s[0]*seed^6+seed^6+s[6]
        // =seed*f(0,6)+(1-s[0]-seed)*seed^6+s[6]
        int hashCode = hashPatternList(pattern, 0, PATTERN_MAX_LENGTH);
        PatternType[] patternMap = getPatternMap(consideredChessType);
        PatternType patternType = patternMap[hashCode];
        for (int i = 1; i + GomokuConst.CONSECUTIVE_NUM + 1 <= pattern.size(); ++i) {
            hashCode = SEED * hashCode + (1 - pattern.get(i - 1).ordinal() - SEED) * POW_SEED_SIX
                    + pattern.get(i + GomokuConst.CONSECUTIVE_NUM).ordinal();
            PatternType newPatternType = patternMap[hashCode];
            if (newPatternType.compareTo(patternType) > 0)
                patternType = newPatternType;
        }
        return patternType;
    }

    public static final PatternType getPatternType(List<ChessType> pattern, ChessType consideredChessType) {
        Preconditions.checkArgument(ChessType.EMPTY != consideredChessType);
        Preconditions.checkArgument(pattern.size() <= PATTERN_MAX_LENGTH);

        PatternType[] patternMap = getPatternMap(consideredChessType);
        int patternHashCode = hashPatternList(pattern, 0, pattern.size());

        PatternType bestPatternType = patternMap[patternHashCode];
        if (null != bestPatternType) {
            return bestPatternType;
        }

        // five chess in a row
        if (isFive(pattern, consideredChessType)) {
            patternMap[patternHashCode] = PatternType.FIVE;
            return PatternType.FIVE;
        }
        // open four
        int openPatternLength = isOpenPattern(pattern, consideredChessType);
        if (openPatternLength == 4) {
            patternMap[patternHashCode] = PatternType.OPEN_FOUR;
            return PatternType.OPEN_FOUR;
        }

        // others
        bestPatternType = PatternType.OTHERS;
        for (int i = 0; i < pattern.size(); ++i) {
            if (pattern.get(i) != ChessType.EMPTY) {
                continue;
            }
            for (ChessType newChessType : Arrays.asList(ChessType.BLACK, ChessType.WHITE)) {
                List<ChessType> newPattern = new ArrayList<>(pattern);
                newPattern.set(i, newChessType);
                PatternType newPatternType = getPatternType(newPattern, consideredChessType);
                switch (newPatternType) {
                case FIVE:
                    if (PatternType.HALF_OPEN_FOUR.compareTo(bestPatternType) > 0) {
                        bestPatternType = PatternType.HALF_OPEN_FOUR;
                    }
                    break;
                case OPEN_FOUR: // ..ooo. .oo.o.
                    if (openPatternLength == 3) {
                        if (PatternType.OPEN_THREE.compareTo(bestPatternType) > 0) {
                            bestPatternType = PatternType.OPEN_THREE;
                        }
                    } else {
                        if (PatternType.SPACED_OPEN_THREE.compareTo(bestPatternType) > 0) {
                            bestPatternType = PatternType.SPACED_OPEN_THREE;
                        }
                    }
                    break;
                case HALF_OPEN_FOUR:
                    if (PatternType.HALF_OPEN_THREE.compareTo(bestPatternType) > 0) {
                        bestPatternType = PatternType.HALF_OPEN_THREE;
                    }
                    break;
                case OPEN_THREE: // -> ...oo. ..o.o.
                case SPACED_OPEN_THREE: // -> .oo... .o..o. ..o.o.
                    if (openPatternLength == 2) {
                        if (PatternType.OPEN_TWO.compareTo(bestPatternType) > 0) {
                            bestPatternType = PatternType.OPEN_TWO;
                        }
                        break;
                    }
                    Pair<Integer, Integer> spacedOpenTwo = isSpacedOpenPattern(pattern, consideredChessType);
                    Preconditions.checkNotNull(spacedOpenTwo);
                    Preconditions.checkArgument(spacedOpenTwo.getKey() == 2);
                    Preconditions.checkArgument(spacedOpenTwo.getValue() == 1 || spacedOpenTwo.getValue() == 2);
                    if (spacedOpenTwo.getValue() == 1) {
                        if (PatternType.ONE_SPACED_OPEN_TWO.compareTo(bestPatternType) > 0) {
                            bestPatternType = PatternType.ONE_SPACED_OPEN_TWO;
                        }
                    } else {
                        if (PatternType.TWO_SPACED_OPEN_TWO.compareTo(bestPatternType) > 0) {
                            bestPatternType = PatternType.TWO_SPACED_OPEN_TWO;
                        }
                    }
                    break;
                case HALF_OPEN_THREE:
                    if (PatternType.HALF_OPEN_TWO.compareTo(bestPatternType) > 0) {
                        bestPatternType = PatternType.HALF_OPEN_TWO;
                    }
                    break;
                default:
                }
            }
        }
        patternMap[patternHashCode] = bestPatternType;
        return bestPatternType;
    }

    private static final boolean isFive(List<ChessType> pattern, ChessType chessType) {
        List<Pair<ChessType, Integer>> analyzed = analyzePattern(pattern);
        for (Pair<ChessType, Integer> pair : analyzed) {
            if (pair.getKey() == chessType && pair.getValue() >= GomokuConst.CONSECUTIVE_NUM) {
                return true;
            }
        }
        return false;
    }

    private static final int isOpenPattern(List<ChessType> pattern, ChessType chessType) {
        List<Pair<ChessType, Integer>> analyzed = analyzePattern(pattern);
        if (analyzed.size() != 3) {
            return 0;
        }
        if (analyzed.get(0).getKey() == ChessType.EMPTY && analyzed.get(1).getKey() == chessType
                && analyzed.get(2).getKey() == ChessType.EMPTY) {
            return analyzed.get(1).getValue();
        }
        return 0;
    }

    private static final Pair<Integer, Integer> isSpacedOpenPattern(List<ChessType> pattern, ChessType chessType) {
        List<Pair<ChessType, Integer>> analyzed = analyzePattern(pattern);
        if (analyzed.size() != 5) {
            return null;
        }
        if (analyzed.get(0).getKey() == ChessType.EMPTY && analyzed.get(1).getKey() == chessType
                && analyzed.get(2).getKey() == ChessType.EMPTY && analyzed.get(3).getKey() == chessType
                && analyzed.get(4).getKey() == ChessType.EMPTY) {
            return ImmutablePair.of(analyzed.get(1).getValue() + analyzed.get(3).getValue(),
                    analyzed.get(2).getValue());
        }
        return null;
    }

    private static final List<Pair<ChessType, Integer>> analyzePattern(List<ChessType> pattern) {
        List<Pair<ChessType, Integer>> analyzed = new ArrayList<>();
        int count = 0;
        ChessType lastType = null;
        for (ChessType chessType : pattern) {
            if (chessType == lastType) {
                ++count;
            } else {
                if (count > 0) {
                    analyzed.add(ImmutablePair.of(lastType, count));
                }
                count = 1;
            }
            lastType = chessType;
        }
        analyzed.add(ImmutablePair.of(lastType, count));
        return analyzed;
    }

    private static final int hashPatternList(List<ChessType> pattern, int fromIndex, int toIndex) {
        int hash = 1;
        int seed = ChessType.values().length;
        for (int i = fromIndex; i < toIndex; ++i) {
            hash = hash * seed + pattern.get(i).ordinal();
        }
        return hash;
    }

    private static final PatternType[] getPatternMap(ChessType chessType) {
        return ChessType.BLACK == chessType ? BLACK_PATTERN_MAP : WHITE_PATTERN_MAP;
    }

}
