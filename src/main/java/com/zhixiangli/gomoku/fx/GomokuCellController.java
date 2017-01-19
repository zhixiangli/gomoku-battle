/**
 * 
 */
package com.zhixiangli.gomoku.fx;

import com.zhixiangli.gomoku.model.ChessType;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * cell controller.
 * 
 * @author lizhixiang
 * @date 2015年6月5日
 */
class GomokuCellController {

    /**
     * row index of this cell.
     */
    private int row;

    /**
     * column index of this cell.
     */
    private int column;

    /**
     * GomokuManager.
     */
    private GomokuFXManager gomokuManger;

    /**
     * pane of this cell.
     */
    @FXML
    private Pane cellPane;

    /**
     * chess of this cell.
     */
    @FXML
    private Circle cellCircle;

    public GomokuCellController(int row, int column, GomokuFXManager gomokuManager) {
        this.row = row;
        this.column = column;
        this.gomokuManger = gomokuManager;
    }

    /**
     * 
     * initialize.
     */
    @FXML
    public void initialize() {

        // pane is disabled when following contition meets.
        this.cellPane.disableProperty()
                .bind(this.gomokuManger.getChessboardStateProperty().isNotEqualTo(ChessboardState.GAME_ON).or(
                        this.gomokuManger.getChessboardProperty(row, column).isNotEqualTo(ChessType.EMPTY.ordinal())));

        // location of this chess piece.
        this.cellCircle.layoutXProperty().bind(this.cellPane.widthProperty().divide(2));
        this.cellCircle.layoutYProperty().bind(this.cellPane.heightProperty().divide(2));

        // radius of this chess piece.
        this.cellCircle.radiusProperty()
                .bind(Bindings.min(cellPane.widthProperty(), cellPane.heightProperty()).divide(2.5));

        // the chess type of this cell, and add listener when the chess type
        // is changed.
        SimpleIntegerProperty chessTypeProperty = this.gomokuManger.getChessboardProperty(row, column);
        this.updateCell(chessTypeProperty);
        chessTypeProperty.addListener(event -> this.updateCell(chessTypeProperty));
    }

    private void updateCell(SimpleIntegerProperty chessTypeProperty) {
        if (ChessType.EMPTY.ordinal() == chessTypeProperty.get()) {
            this.cellCircle.visibleProperty().set(false);
        } else {
            this.cellCircle.visibleProperty().set(true);
            this.cellCircle.setFill(ChessType.BLACK.ordinal() == chessTypeProperty.get() ? Color.BLACK : Color.WHITE);
        }
    }

    /**
     * 
     * when this cell is clicked, this method will be called.
     */
    @FXML
    public void makeMove() {
        this.gomokuManger.makeMove(row, column);
    }

}
