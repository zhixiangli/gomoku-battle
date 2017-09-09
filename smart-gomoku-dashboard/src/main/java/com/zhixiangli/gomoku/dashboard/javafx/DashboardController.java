/**
 * 
 */
package com.zhixiangli.gomoku.dashboard.javafx;

import java.awt.Point;
import java.io.IOException;

import com.zhixiangli.gomoku.core.chessboard.ChessState;
import com.zhixiangli.gomoku.core.common.GomokuConst;
import com.zhixiangli.gomoku.dashboard.common.DashboardConst;
import com.zhixiangli.gomoku.dashboard.service.DashboardService;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

/**
 * gomoku controller.
 * 
 * @author lizhixiang
 */
public class DashboardController {

    @FXML
    private TextArea blackAliasArea;

    @FXML
    private TextArea whiteAliasArea;

    /**
     * announcement text area.
     */
    @FXML
    private TextArea announcementArea;

    /**
     * chessboard grid pane.
     */
    @FXML
    private GridPane chessboardGridPane;

    private DashboardService dashboardService = DashboardService.getInstance();

    /**
     * 
     * initialize.
     * 
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {
        // initialize choice box.
        initializePlayerAlias();

        // add listener to update announcement when chessboard state is changed.
        initailizeAnnouncement();

        // add each cell pane to grid pane.
        initializeGridPane();
    }

    public void newGame() {
        dashboardService.newGame();
    }

    private void initializePlayerAlias() {
        blackAliasArea.setEditable(false);
        blackAliasArea.setText(DashboardConst.Player.playerBlackAlias);

        whiteAliasArea.setEditable(false);
        whiteAliasArea.setText(DashboardConst.Player.playerWhiteAlias);
    }

    private void initailizeAnnouncement() {
        announcementArea.setEditable(false);
        dashboardService.addChessStateChangeListener(
                (observable, oldValue, newValue) -> announcementArea.setText(newValue.name()));
        announcementArea.setText(ChessState.GAME_READY.name());
    }

    private void initializeGridPane() throws IOException {
        chessboardGridPane.getChildren().clear();
        for (int row = 0; row < GomokuConst.CHESSBOARD_SIZE; ++row) {
            for (int column = 0; column < GomokuConst.CHESSBOARD_SIZE; ++column) {
                Point point = new Point(row, column);
                chessboardGridPane.add(new DashboardCellPane(point), column, row);
            }
        }
    }
}
