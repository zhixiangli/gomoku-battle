package com.zhixiangli.gomoku.dashboard.javafx;

import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.console.common.PlayerProperties.PlayerType;
import com.zhixiangli.gomoku.core.chessboard.ChessState;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.core.service.ChessboardService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.awt.Point;
import java.io.IOException;

/**
 * gomoku controller.
 *
 * @author lizhixiang
 */
public class DashboardController {

    @FXML
    private Label blackAliasArea;

    @FXML
    private Label whiteAliasArea;

    @FXML
    private ChoiceBox<PlayerType> blackPlayerChoice;

    @FXML
    private ChoiceBox<PlayerType> whitePlayerChoice;

    /**
     * announcement label.
     */
    @FXML
    private Label announcementArea;

    /**
     * chessboard grid pane.
     */
    @FXML
    private GridPane chessboardGridPane;

    private final ChessboardService chessboardService = ChessboardService.getInstance();

    private boolean initializingPlayerChoice;

    /**
     * initialize.
     */
    @FXML
    public void initialize() throws IOException {
        initializePlayerSelector();
        initializePlayerAlias();

        // add listener to update announcement when chessboard state is changed.
        initializeAnnouncement();

        // add each cell pane to grid pane.
        initializeGridPane();
    }

    public void newGame() {
        chessboardService.restart();
    }

    @FXML
    public void updateBlackPlayer() {
        PlayerProperties.setPlayerType(ChessType.BLACK, blackPlayerChoice.getValue());
        blackAliasArea.setText(PlayerProperties.getPlayerAlias(ChessType.BLACK));
        if (!initializingPlayerChoice) {
            chessboardService.restart();
        }
    }

    @FXML
    public void updateWhitePlayer() {
        PlayerProperties.setPlayerType(ChessType.WHITE, whitePlayerChoice.getValue());
        whiteAliasArea.setText(PlayerProperties.getPlayerAlias(ChessType.WHITE));
        if (!initializingPlayerChoice) {
            chessboardService.restart();
        }
    }

    private void initializePlayerSelector() {
        initializingPlayerChoice = true;
        blackPlayerChoice.setItems(FXCollections.observableArrayList(PlayerType.values()));
        whitePlayerChoice.setItems(FXCollections.observableArrayList(PlayerType.values()));
        blackPlayerChoice.setValue(PlayerProperties.getPlayerType(ChessType.BLACK));
        whitePlayerChoice.setValue(PlayerProperties.getPlayerType(ChessType.WHITE));
        initializingPlayerChoice = false;
    }

    private void initializePlayerAlias() {
        blackAliasArea.setText(PlayerProperties.getPlayerAlias(ChessType.BLACK));
        whiteAliasArea.setText(PlayerProperties.getPlayerAlias(ChessType.WHITE));
    }

    private void initializeAnnouncement() {
        chessboardService.addChessStateChangeListener(
                (observable, oldValue, newValue) -> {
                    if (Platform.isFxApplicationThread()) {
                        announcementArea.setText(getAnnouncementText(newValue));
                    } else {
                        Platform.runLater(() -> announcementArea.setText(getAnnouncementText(newValue)));
                    }
                });
        announcementArea.setText(getAnnouncementText(ChessState.GAME_READY));
    }

    private String getAnnouncementText(final ChessState chessState) {
        if (ChessState.GAME_READY == chessState) {
            return "Click board or NEW GAME to start";
        }
        return chessState.name();
    }

    private void initializeGridPane() throws IOException {
        chessboardGridPane.getChildren().clear();
        for (int row = 0; row < GomokuConst.CHESSBOARD_SIZE; ++row) {
            for (int column = 0; column < GomokuConst.CHESSBOARD_SIZE; ++column) {
                final Point point = new Point(row, column);
                chessboardGridPane.add(new DashboardCellPane(point), column, row);
            }
        }
    }
}
