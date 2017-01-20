/**
 * 
 */
package com.zhixiangli.gomoku.chessboard;

import java.awt.Point;

import com.google.common.base.Preconditions;
import com.zhixiangli.gomoku.fx.UIConstant;

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
     * chessboard length.
     */
    private int length = UIConstant.CHESSBOARD_LENGTH;

    /**
     * init an empty chessboard.
     */
    public Chessboard() {
        Preconditions.checkArgument(this.length > 0);
        this.chessboard = new ChessType[length][length];
        this.clear();
    }

    /**
     * 
     * clear the chessboard.
     */
    public void clear() {
        for (int row = 0; row < length; ++row) {
            for (int column = 0; column < length; ++column) {
                this.chessboard[row][column] = ChessType.EMPTY;
            }
        }
    }

    /**
     * 
     * get chess type.
     * 
     * @param row row index.
     * @param column column index.
     * @return chess type.
     */
    public ChessType getChess(int row, int column) {
        return this.chessboard[row][column];
    }

    public void setChess(int row, int column, ChessType chessType) {
        // Preconditions.checkArgument(chessboard[row][column] == ChessType.EMPTY);
        // Preconditions.checkArgument(chessType != ChessType.EMPTY);
        this.chessboard[row][column] = chessType;
    }

    public void setChess(Point point, ChessType chessType) {
        this.setChess(point.x, point.y, chessType);
    }

    /**
     * @return the chessboardLength
     */
    public int getLength() {
        return length;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = ChessType.values().length;
        int result = 1;
        for (int i = 0; i < length; ++i) {
            for (int j = 0; j < length; ++j) {
                result = result * prime + chessboard[i][j].ordinal();
            }
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#clone()
     */
    @Override
    public Chessboard clone() {
        Chessboard clonedChessboard = new Chessboard();
        for (int row = 0; row < length; ++row) {
            for (int column = 0; column < length; ++column) {
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
        for (int i = 0; i < length; ++i) {
            for (int j = 0; j < length; ++j) {
                sb.append(chessboard[i][j].ordinal());
            }
        }
        return sb.toString();
    }

}
