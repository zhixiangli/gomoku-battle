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
    private Point position;

    /**
     * Gomoku Service.
     */
    private DashboardService dashboardService = DashboardService.getInstance();

    public DashboardCellPane(Point position) throws IOException {
        this.position = position;

        setPrefHeight(DashboardConst.GRID_LENGTH);
        setPrefWidth(DashboardConst.GRID_LENGTH);

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
        cellCircle.layoutXProperty().bind(cellPane.widthProperty().divide(2));
        cellCircle.layoutYProperty().bind(cellPane.heightProperty().divide(2));

        // radius of this chess piece.
        cellCircle.radiusProperty().bind(Bindings.min(cellPane.widthProperty(), cellPane.heightProperty()).divide(2.5));

        // the chess type of this cell, and add listener when the chess type
        // is changed.
        dashboardService.addChessboardChangeListener(position,
                (observable, oldValue, newValue) -> updateCellPane(newValue, true));
        updateCellPane(ChessType.EMPTY, true);

        dashboardService.addLastMovePointChangeListener((observable, oldValue, newValue) -> {
            if (position.equals(oldValue)) {
                updateCellPane(dashboardService.getChessboard(oldValue), false);
            }
        });

    }

    private void updateCellPane(ChessType chessType, boolean isLatestPosition) {
        if (ChessType.EMPTY == chessType) {
            cellCircle.visibleProperty().set(false);
        } else {
            cellCircle.visibleProperty().set(true);
            if (isLatestPosition) {
                cellCircle.setFill(ChessType.BLACK == chessType ? Color.DIMGRAY : Color.CORNSILK);
            } else {
                cellCircle.setFill(ChessType.BLACK == chessType ? Color.BLACK : Color.WHITE);
            }
        }
    }

    /**
     * 
     * when this cell is clicked, this method will be called.
     */
    @FXML
    public void takeMove() {
        dashboardService.takeMove(position);
    }

}
