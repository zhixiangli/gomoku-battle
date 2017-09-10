/**
 * 
 */
package com.zhixiangli.gomoku.agent.alphabetasearch.algorithm;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.zhixiangli.gomoku.core.chessboard.Chessboard;
import com.zhixiangli.gomoku.core.common.GomokuConst;

/**
 * @author zhixiangli
 *
 */
public class AlphaBetaSearchTable {

    private int searchDepth;

    private Pair<Long, Long> tableHashCode;

    public AlphaBetaSearchTable(int searchDepth, Chessboard chessboard) {
        this.searchDepth = searchDepth;
        this.tableHashCode = hashChessboard(chessboard);
    }

    private Pair<Long, Long> hashChessboard(Chessboard chessboard) {
        long result1 = 1;
        long result2 = 1;
        int prime1 = 13;
        int prime2 = 7;
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                int type = chessboard.getChess(i, j).ordinal();
                result1 = result1 * prime1 + type;
                result2 = result2 * prime2 + type;
            }
        }
        return ImmutablePair.of(result1, result2);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + searchDepth;
        result = prime * result + ((tableHashCode == null) ? 0 : tableHashCode.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AlphaBetaSearchTable)) {
            return false;
        }
        AlphaBetaSearchTable other = (AlphaBetaSearchTable) obj;
        if (searchDepth != other.searchDepth) {
            return false;
        }
        if (tableHashCode == null) {
            if (other.tableHashCode != null) {
                return false;
            }
        } else if (!tableHashCode.equals(other.tableHashCode)) {
            return false;
        }
        return true;
    }

}
