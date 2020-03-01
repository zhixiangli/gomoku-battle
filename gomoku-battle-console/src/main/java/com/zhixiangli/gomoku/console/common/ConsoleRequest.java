package com.zhixiangli.gomoku.console.common;

/**
 * @author lizx
 */
public class ConsoleRequest {

    private ConsoleCommand command;

    private int rows;

    private int columns;

    private String chessboard;

    public ConsoleRequest(final ConsoleCommand command, final int rows, final int columns, final String chessboard) {
        super();
        this.command = command;
        this.rows = rows;
        this.columns = columns;
        this.chessboard = chessboard;
    }

    /**
     * @return the command
     */
    public ConsoleCommand getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(final ConsoleCommand command) {
        this.command = command;
    }

    /**
     * @return the rows
     */
    public int getRows() {
        return rows;
    }

    /**
     * @param rows the rows to set
     */
    public void setRows(int rows) {
        this.rows = rows;
    }

    /**
     * @return the columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @param columns the columns to set
     */
    public void setColumns(final int columns) {
        this.columns = columns;
    }

    /**
     * @return the chessboard
     */
    public String getChessboard() {
        return chessboard;
    }

    /**
     * @param chessboard the chessboard to set
     */
    public void setChessboard(final String chessboard) {
        this.chessboard = chessboard;
    }

}
