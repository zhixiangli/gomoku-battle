package com.zhixiangli.gomoku.dashboard.javafx;

import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.service.ChessboardService;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.apache.commons.lang3.StringUtils;

import java.awt.Point;
import java.io.IOException;

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
     * marker for latest move.
     */
    @FXML
    private Circle lastMoveMarker;

    /**
     * horizontal grid line through cell center.
     */
    @FXML
    private Line horizontalLine;

    /**
     * vertical grid line through cell center.
     */
    @FXML
    private Line verticalLine;

    /**
     * chess position
     */
    private final Point position;

    /**
     * Gomoku Service.
     */
    private final ChessboardService chessboardService = ChessboardService.getInstance();

    DashboardCellPane(final Point position) throws IOException {
        this.position = position;

        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cell_pane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    /**
     * initialize.
     */
    @FXML
    public void initialize() {
        // bind grid lines through cell center, clipped at board edges.
        int row = position.x;
        int col = position.y;
        int maxIndex = GomokuConst.CHESSBOARD_SIZE - 1;

        // horizontal line
        if (col == 0) {
            horizontalLine.startXProperty().bind(cellPane.widthProperty().divide(2));
        }
        horizontalLine.startYProperty().bind(cellPane.heightProperty().divide(2));
        if (col == maxIndex) {
            horizontalLine.endXProperty().bind(cellPane.widthProperty().divide(2));
        } else {
            horizontalLine.endXProperty().bind(cellPane.widthProperty());
        }
        horizontalLine.endYProperty().bind(cellPane.heightProperty().divide(2));

        // vertical line
        verticalLine.startXProperty().bind(cellPane.widthProperty().divide(2));
        if (row == 0) {
            verticalLine.startYProperty().bind(cellPane.heightProperty().divide(2));
        }
        verticalLine.endXProperty().bind(cellPane.widthProperty().divide(2));
        if (row == maxIndex) {
            verticalLine.endYProperty().bind(cellPane.heightProperty().divide(2));
        } else {
            verticalLine.endYProperty().bind(cellPane.heightProperty());
        }

        // location of this chess piece.
        cellCircle.layoutXProperty().bind(cellPane.widthProperty().divide(2));
        cellCircle.layoutYProperty().bind(cellPane.heightProperty().divide(2));

        // radius of this chess piece.
        cellCircle.radiusProperty().bind(Bindings.min(cellPane.widthProperty(), cellPane.heightProperty()).divide(2.5));

        // location and size for latest move marker.
        lastMoveMarker.layoutXProperty().bind(cellPane.widthProperty().divide(2));
        lastMoveMarker.layoutYProperty().bind(cellPane.heightProperty().divide(2));
        lastMoveMarker.radiusProperty().bind(Bindings.min(cellPane.widthProperty(), cellPane.heightProperty()).divide(8));

        // the chess type of this cell, and add listener when the chess type
        // is changed.
        chessboardService.addChessboardChangeListener(position,
                (observable, oldValue, newValue) -> updateCellPane(newValue));
        updateCellPane(chessboardService.getChessboard(position));

        chessboardService.addLastMovePointChangeListener((observable, oldValue, newValue) -> {
            if (position.equals(oldValue) || position.equals(newValue)) {
                updateCellPane(chessboardService.getChessboard(position));
            }
        });

    }

    private void updateCellPane(final ChessType chessType) {
        if (ChessType.EMPTY == chessType) {
            cellCircle.visibleProperty().set(false);
            lastMoveMarker.visibleProperty().set(false);
            return;
        }

        cellCircle.visibleProperty().set(true);
        boolean isLatestPosition = position.equals(chessboardService.getLastMovePoint());

        if (isLatestPosition) {
            cellCircle.setFill((ChessType.BLACK == chessType) ? Color.rgb(20, 20, 20) : Color.rgb(245, 245, 240));
            cellCircle.setStroke((ChessType.BLACK == chessType) ? Color.rgb(235, 205, 120) : Color.rgb(95, 75, 25));
            cellCircle.setStrokeWidth(2.0);
            lastMoveMarker.setFill((ChessType.BLACK == chessType) ? Color.rgb(245, 245, 240) : Color.rgb(35, 35, 35));
            lastMoveMarker.setStroke((ChessType.BLACK == chessType) ? Color.rgb(35, 35, 35) : Color.rgb(255, 255, 255));
            lastMoveMarker.visibleProperty().set(true);
        } else {
            cellCircle.setFill((ChessType.BLACK == chessType) ? Color.rgb(50, 50, 50) : Color.rgb(225, 225, 220));
            cellCircle.setStroke((ChessType.BLACK == chessType) ? Color.rgb(35, 35, 35) : Color.rgb(180, 180, 170));
            cellCircle.setStrokeWidth(0.8);
            lastMoveMarker.visibleProperty().set(false);
        }
    }

    /**
     * when this cell is clicked, this method will be called.
     */
    @FXML
    public void takeMove() {
        final ChessType chessType = chessboardService.getCurrentChessType();
        if ((chessType != ChessType.BLACK) && (chessType != ChessType.WHITE)) {
            return;
        }
        if (chessType == ChessType.BLACK && StringUtils.isNotBlank(PlayerProperties.playerBlackCommand)) {
            return;
        }
        if (chessType == ChessType.WHITE && StringUtils.isNotBlank(PlayerProperties.playerWhiteCommand)) {
            return;
        }

        chessboardService.takeMove(position);
    }

}
