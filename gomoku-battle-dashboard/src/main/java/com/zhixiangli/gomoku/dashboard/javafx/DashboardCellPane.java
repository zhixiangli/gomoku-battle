package com.zhixiangli.gomoku.dashboard.javafx;

import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.service.ChessboardService;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import org.apache.commons.lang3.StringUtils;

import java.awt.Point;
import java.io.IOException;

/**
 * pane of each cell of chessboard.
 *
 * @author lizhixiang
 */
class DashboardCellPane extends Pane {

    private static final double PULSE_ANIMATION_DURATION_MS = 600;

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
     * pulsing animation for the last move marker.
     */
    private FadeTransition pulseAnimation;

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
        lastMoveMarker.radiusProperty().bind(Bindings.min(cellPane.widthProperty(), cellPane.heightProperty()).divide(4));

        // pulsing animation to draw attention to the last move.
        pulseAnimation = new FadeTransition(Duration.millis(PULSE_ANIMATION_DURATION_MS), lastMoveMarker);
        pulseAnimation.setFromValue(1.0);
        pulseAnimation.setToValue(0.3);
        pulseAnimation.setCycleCount(Animation.INDEFINITE);
        pulseAnimation.setAutoReverse(true);

        // the chess type of this cell, and add listener when the chess type
        // is changed.
        chessboardService.addChessboardChangeListener(position,
                (observable, oldValue, newValue) -> Platform.runLater(() -> updateCellPane(newValue)));
        updateCellPane(chessboardService.getChessboard(position));

        chessboardService.addLastMovePointChangeListener((observable, oldValue, newValue) -> {
            if (position.equals(oldValue) || position.equals(newValue)) {
                Platform.runLater(() -> updateCellPane(chessboardService.getChessboard(position)));
            }
        });

    }

    private void updateCellPane(final ChessType chessType) {
        if (ChessType.EMPTY == chessType) {
            cellCircle.visibleProperty().set(false);
            lastMoveMarker.visibleProperty().set(false);
            pulseAnimation.stop();
            return;
        }

        cellCircle.visibleProperty().set(true);
        cellCircle.setFill((ChessType.BLACK == chessType) ? Color.rgb(30, 30, 30) : Color.rgb(235, 235, 230));
        cellCircle.setStroke((ChessType.BLACK == chessType) ? Color.rgb(35, 35, 35) : Color.rgb(180, 180, 170));
        cellCircle.setStrokeWidth(0.8);

        boolean isLatestPosition = position.equals(chessboardService.getLastMovePoint());
        if (isLatestPosition) {
            lastMoveMarker.visibleProperty().set(true);
            pulseAnimation.play();
        } else {
            lastMoveMarker.visibleProperty().set(false);
            pulseAnimation.stop();
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
