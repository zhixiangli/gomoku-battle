/**
 * 
 */
package com.zhixiangli.gomoku.dashboard.javafx;

import java.awt.Point;
import java.io.IOException;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.dashboard.common.DashboardConst;
import com.zhixiangli.gomoku.dashboard.service.DashboardService;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * pane of each cell of chessboard.
 * 
 * @author lizhixiang
 */
class DashboardCellPane extends Pane {

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

    /**
     * chess position
     */
    private Point point;

    /**
     * Gomoku Service.
     */
    private DashboardService dashboardService = DashboardService.getInstance();

    public DashboardCellPane(Point point) throws IOException {
        this.point = point;

        this.setPrefHeight(DashboardConst.GRID_LENGTH);
        this.setPrefWidth(DashboardConst.GRID_LENGTH);

        // set row index and column index when this pane is put in a grid pane.
        GridPane.setRowIndex(this, point.x);
        GridPane.setColumnIndex(this, point.y);

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cell_pane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    /**
     * 
     * initialize.
     */
    @FXML
    public void initialize() {
        // location of this chess piece.
        this.cellCircle.layoutXProperty().bind(this.cellPane.widthProperty().divide(2));
        this.cellCircle.layoutYProperty().bind(this.cellPane.heightProperty().divide(2));

        // radius of this chess piece.
        this.cellCircle.radiusProperty()
                .bind(Bindings.min(cellPane.widthProperty(), cellPane.heightProperty()).divide(2.5));

        // the chess type of this cell, and add listener when the chess type
        // is changed.
        this.dashboardService.addChessboardChangeListener(point,
                (observable, oldValue, newValue) -> this.updatePane(newValue));
        this.updatePane(ChessType.EMPTY);
    }

    private void updatePane(ChessType chessType) {
        if (ChessType.EMPTY == chessType) {
            this.cellCircle.visibleProperty().set(false);
        } else {
            this.cellCircle.visibleProperty().set(true);
            this.cellCircle.setFill(ChessType.BLACK == chessType ? Color.BLACK : Color.WHITE);
        }
    }

    /**
     * 
     * when this cell is clicked, this method will be called.
     */
    @FXML
    public void takeMove() {
        this.dashboardService.takeMove(point);
    }

}
