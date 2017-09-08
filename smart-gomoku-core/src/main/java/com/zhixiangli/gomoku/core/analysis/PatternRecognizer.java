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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.chessboard.ChessPatternType;
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

    private static final int PATTERN_MAX_LENGTH = GomokuConst.CONSECUTIVE_NUM + 1;

    private static final int PATTERN_HASH_BOUND = 2 * (int) Math.pow(ChessType.values().length, PATTERN_MAX_LENGTH);

    private static final ChessPatternType[] BLACK_PATTERN_MAP = new ChessPatternType[PATTERN_HASH_BOUND];

    private static final ChessPatternType[] WHITE_PATTERN_MAP = new ChessPatternType[PATTERN_HASH_BOUND];

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

    public static final ChessPatternType getPatternType(List<ChessType> pattern, ChessType consideredChessType) {
        Preconditions.checkArgument(ChessType.EMPTY != consideredChessType);
        Preconditions.checkArgument(pattern.size() <= PATTERN_MAX_LENGTH);

        ChessPatternType[] patternMap = getPatternMap(consideredChessType);
        int patternHashCode = hashPatternList(pattern);

        ChessPatternType bestPatternType = patternMap[patternHashCode];
        if (null != bestPatternType) {
            return bestPatternType;
        }

        // five chess in a row
        if (isFive(pattern, consideredChessType)) {
            patternMap[patternHashCode] = ChessPatternType.FIVE;
            return ChessPatternType.FIVE;
        }
        // open four
        int openPatternLength = isOpenPattern(pattern, consideredChessType);
        if (openPatternLength == 4) {
            patternMap[patternHashCode] = ChessPatternType.OPEN_FOUR;
            return ChessPatternType.OPEN_FOUR;
        }

        // others
        bestPatternType = ChessPatternType.OTHERS;
        for (int i = 0; i < pattern.size(); ++i) {
            if (pattern.get(i) != ChessType.EMPTY) {
                continue;
            }
            for (ChessType newChessType : Arrays.asList(ChessType.BLACK, ChessType.WHITE)) {
                List<ChessType> newPattern = new ArrayList<>(pattern);
                newPattern.set(i, newChessType);
                ChessPatternType newPatternType = getPatternType(newPattern, consideredChessType);
                switch (newPatternType) {
                case FIVE:
                    if (ChessPatternType.HALF_OPEN_FOUR.compareTo(bestPatternType) > 0) {
                        bestPatternType = ChessPatternType.HALF_OPEN_FOUR;
                    }
                    break;
                case OPEN_FOUR: // ..ooo. .oo.o.
                    if (openPatternLength == 3) {
                        if (ChessPatternType.OPEN_THREE.compareTo(bestPatternType) > 0) {
                            bestPatternType = ChessPatternType.OPEN_THREE;
                        }
                    } else {
                        if (ChessPatternType.SPACED_OPEN_THREE.compareTo(bestPatternType) > 0) {
                            bestPatternType = ChessPatternType.SPACED_OPEN_THREE;
                        }
                    }
                    break;
                case HALF_OPEN_FOUR:
                    if (ChessPatternType.HALF_OPEN_THREE.compareTo(bestPatternType) > 0) {
                        bestPatternType = ChessPatternType.HALF_OPEN_THREE;
                    }
                    break;
                case OPEN_THREE: // -> ...oo. ..o.o.
                case SPACED_OPEN_THREE: // -> .oo... .o..o. ..o.o.
                    if (openPatternLength == 2) {
                        if (ChessPatternType.OPEN_TWO.compareTo(bestPatternType) > 0) {
                            bestPatternType = ChessPatternType.OPEN_TWO;
                        }
                        break;
                    }
                    ImmutablePair<Integer, Integer> spacedOpenTwoPattern = isSpacedOpenPattern(pattern,
                            consideredChessType);
                    Preconditions.checkNotNull(spacedOpenTwoPattern);
                    Preconditions.checkArgument(spacedOpenTwoPattern.getKey() == 2);
                    Preconditions.checkArgument(
                            spacedOpenTwoPattern.getValue() == 1 || spacedOpenTwoPattern.getValue() == 2);
                    if (spacedOpenTwoPattern.getValue() == 1) {
                        if (ChessPatternType.ONE_SPACED_OPEN_TWO.compareTo(bestPatternType) > 0) {
                            bestPatternType = ChessPatternType.ONE_SPACED_OPEN_TWO;
                        }
                    } else {
                        if (ChessPatternType.TWO_SPACED_OPEN_TWO.compareTo(bestPatternType) > 0) {
                            bestPatternType = ChessPatternType.TWO_SPACED_OPEN_TWO;
                        }
                    }
                    break;
                case HALF_OPEN_THREE:
                    if (ChessPatternType.HALF_OPEN_TWO.compareTo(bestPatternType) > 0) {
                        bestPatternType = ChessPatternType.HALF_OPEN_TWO;
                    }
                    break;
                default:
                }
            }
        }
        patternMap[patternHashCode] = bestPatternType;
        return bestPatternType;
    }

    private static final int hashPatternList(List<ChessType> patternList) {
        int hash = 1;
        int seed = ChessType.values().length;
        for (ChessType type : patternList) {
            hash = hash * seed + type.ordinal();
        }
        return hash;
    }

    private static final boolean isFive(List<ChessType> pattern, ChessType chessType) {
        List<ImmutablePair<ChessType, Integer>> analyzed = analyzePattern(pattern);
        for (ImmutablePair<ChessType, Integer> pair : analyzed) {
            if (pair.getKey() == chessType && pair.getValue() >= GomokuConst.CONSECUTIVE_NUM) {
                return true;
            }
        }
        return false;
    }

    private static final int isOpenPattern(List<ChessType> pattern, ChessType chessType) {
        List<ImmutablePair<ChessType, Integer>> analyzed = analyzePattern(pattern);
        if (analyzed.size() != 3) {
            return 0;
        }
        if (analyzed.get(0).getKey() == ChessType.EMPTY && analyzed.get(1).getKey() == chessType
                && analyzed.get(2).getKey() == ChessType.EMPTY) {
            return analyzed.get(1).getValue();
        }
        return 0;
    }

    private static final ImmutablePair<Integer, Integer> isSpacedOpenPattern(List<ChessType> pattern,
            ChessType chessType) {
        List<ImmutablePair<ChessType, Integer>> analyzed = analyzePattern(pattern);
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

    private static final List<ImmutablePair<ChessType, Integer>> analyzePattern(List<ChessType> pattern) {
        List<ImmutablePair<ChessType, Integer>> analyzed = new ArrayList<>();
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

    private static final ChessPatternType[] getPatternMap(ChessType chessType) {
        return ChessType.BLACK == chessType ? BLACK_PATTERN_MAP : WHITE_PATTERN_MAP;
    }

}
