/**
 *
 */
package com.zhixiangli.gomoku.console.common;

/**
 * @author lizx
 *
 */
public class ConsoleResponse {

    private int rowIndex;

    private int columnIndex;

    public ConsoleResponse(final int rowIndex, final int columnIndex) {
        super();
        this.rowIndex = rowIndex;
        this.columnIndex = columnIndex;
    }

    /**
     * @return the rowIndex
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * @param rowIndex
     *            the rowIndex to set
     */
    public void setRowIndex(final int rowIndex) {
        this.rowIndex = rowIndex;
    }

    /**
     * @return the columnIndex
     */
    public int getColumnIndex() {
        return columnIndex;
    }

    /**
     * @param columnIndex
     *            the columnIndex to set
     */
    public void setColumnIndex(final int columnIndex) {
        this.columnIndex = columnIndex;
    }

}
