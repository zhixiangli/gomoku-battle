/**
 * 
 */
package com.zhixiangli.gomoku.core.chessboard;

import java.awt.Point;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.core.common.GomokuConst;

/**
 * the data structure of chessboard.
 * 
 * @author lizhixiang
 *
 */
public class Chessboard implements Cloneable {

    /**
     * chessboard.
     */
    private ChessType[][] chessboard;

    /**
     * init an empty chessboard.
     */
    public Chessboard() {
        Preconditions.checkArgument(GomokuConst.CHESSBOARD_SIZE > 0);
        this.chessboard = new ChessType[GomokuConst.CHESSBOARD_SIZE][GomokuConst.CHESSBOARD_SIZE];
        this.clear();
    }

    /**
     * 
     * clear the chessboard.
     */
    public void clear() {
        for (int row = 0; row < GomokuConst.CHESSBOARD_SIZE; ++row) {
            for (int column = 0; column < GomokuConst.CHESSBOARD_SIZE; ++column) {
                this.chessboard[row][column] = ChessType.EMPTY;
            }
        }
    }

    /**
     * 
     * get chess type.
     * 
     * @param row
     *            row index.
     * @param column
     *            column index.
     * @return chess type.
     */
    public ChessType getChess(int row, int column) {
        return this.chessboard[row][column];
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
                switch (chessboard[i][j]) {
                case BLACK:
                    sb.append(GomokuConst.ChessChar.BLACK);
                    break;
                case WHITE:
                    sb.append(GomokuConst.ChessChar.WHITE);
                    break;
                case EMPTY:
                    sb.append(GomokuConst.ChessChar.EMPTY);
                    break;
                default:
                }
            }
            sb.append(StringUtils.LF);
        }
        return sb.toString();
    }

}
