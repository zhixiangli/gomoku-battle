package com.zhixiangli.gomoku.core.chessboard;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.Point;
import java.util.Arrays;

/**
 * the data structure of chessboard.
 *
 * @author lizhixiang
 */
public class Chessboard implements Cloneable {

    /**
     * chessboard.
     */
    private final ChessType[][] chessboard;

    /**
     * init an empty chessboard.
     */
    public Chessboard() {
        chessboard = new ChessType[GomokuConst.CHESSBOARD_SIZE][GomokuConst.CHESSBOARD_SIZE];
        clear();
    }

    public Chessboard(final String strs) {
        this();
        final String[] rows = StringUtils.split(strs);
        Preconditions.checkArgument(ArrayUtils.getLength(rows) == GomokuConst.CHESSBOARD_SIZE);
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            Preconditions.checkArgument(StringUtils.length(rows[i]) == GomokuConst.CHESSBOARD_SIZE);
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                final ChessType chessType = ChessType.getChessType(rows[i].charAt(j));
                Preconditions.checkNotNull(chessType);
                chessboard[i][j] = chessType;
            }
        }
    }

    /**
     * clear the chessboard.
     */
    public void clear() {
        for (int row = 0; row < GomokuConst.CHESSBOARD_SIZE; ++row) {
            for (int column = 0; column < GomokuConst.CHESSBOARD_SIZE; ++column) {
                chessboard[row][column] = ChessType.EMPTY;
            }
        }
    }

    /**
     * get chess type.
     *
     * @param row    row index.
     * @param column column index.
     * @return chess type.
     */
    public ChessType getChess(final int row, final int column) {
        return chessboard[row][column];
    }

    public ChessType getChess(Point p) {
        return this.chessboard[p.x][p.y];
    }

    public void setChess(int row, int column, ChessType chessType) {
        this.chessboard[row][column] = chessType;
    }

    public void setChess(Point point, ChessType chessType) {
        this.setChess(point.x, point.y, chessType);
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
        result = prime * result + Arrays.deepHashCode(chessboard);
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        Chessboard other = (Chessboard) obj;
        if (!Arrays.deepEquals(chessboard, other.chessboard)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    public Chessboard clone() {
        Chessboard clonedChessboard = new Chessboard();
        for (int row = 0; row < GomokuConst.CHESSBOARD_SIZE; ++row) {
            for (int column = 0; column < GomokuConst.CHESSBOARD_SIZE; ++column) {
                ChessType chessType = chessboard[row][column];
                if (ChessType.EMPTY != chessType) {
                    clonedChessboard.setChess(row, column, chessType);
                }
            }
        }
        return clonedChessboard;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < GomokuConst.CHESSBOARD_SIZE; ++i) {
            for (int j = 0; j < GomokuConst.CHESSBOARD_SIZE; ++j) {
                sb.append(chessboard[i][j].getChessChar());
            }
            sb.append(StringUtils.LF);
        }
        return sb.toString();
    }

}
