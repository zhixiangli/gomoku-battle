/**
 * 
 */
package com.zhixiangli.gomoku.fx;

import java.io.IOException;

import com.zhixiangli.gomoku.agent.GomokuAgent;
import com.zhixiangli.gomoku.agent.alphabeta.GomokuAlphaBetaPruning;
import com.zhixiangli.gomoku.chessboard.ChessboardState;

import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;

/**
 * gomoku controller.
 * 
 * @author lizhixiang
 * @date 2015年5月25日
 */
public class GomokuController {

    /**
     * black player choice box.
     */
    @FXML
    private ChoiceBox<String> blackPlayerChoiceBox;

    /**
     * white player choice box.
     */
    @FXML
    private ChoiceBox<String> whitePlayerChoiceBox;

    /**
     * announcement text area.
     */
    @FXML
    private TextArea announcementTextArea;

    /**
     * chessboard grid pane.
     */
    @FXML
    private GridPane chessboardGridPane;

    /**
     * GomokuManager.
     */
    private GomokuFXManager gomokuManager;

    /**
     * 
     * initialize.
     * 
     * @throws IOException
     */
    @FXML
    public void initialize() throws IOException {
        this.gomokuManager = new GomokuFXManager();

        // initialize choice box.
        if (this.blackPlayerChoiceBox.getItems().isEmpty()) {
            this.blackPlayerChoiceBox.setItems(UIConstant.CHOICE_BOX_CHOICES);
            this.blackPlayerChoiceBox.getSelectionModel().select(0);
        }
        if (this.whitePlayerChoiceBox.getItems().isEmpty()) {
            this.whitePlayerChoiceBox.setItems(UIConstant.CHOICE_BOX_CHOICES);
            this.whitePlayerChoiceBox.getSelectionModel().select(1);
        }

        // set strategy according to the choice box.
        this.setPlayerStrategy();

        // add listener when current play changed.
        SimpleObjectProperty<Class<? extends GomokuAgent>> currentPlayerProperty =
                this.gomokuManager.getCurrentPlayerProperty();
        this.makeStrategyMove(currentPlayerProperty);
        currentPlayerProperty.addListener(event -> this.makeStrategyMove(currentPlayerProperty));

        // add listener to update announcement when chessboard state is changed.
        this.announcementTextArea.setEditable(false);
        SimpleObjectProperty<ChessboardState> chessboardStateProperty = this.gomokuManager.getChessboardStateProperty();
        this.setAnnouncement(chessboardStateProperty.get());
        chessboardStateProperty.addListener(event -> this.setAnnouncement(chessboardStateProperty.get()));

        // add each cell pane to grid pane.
        this.chessboardGridPane.getChildren().clear();
        for (int row = 0; row <UIConstant.CHESSBOARD_LENGTH; ++row) {
            for (int column = 0; column < UIConstant.CHESSBOARD_LENGTH; ++column) {
                this.chessboardGridPane.getChildren().add(new GomokuCellPane(row, column, this.gomokuManager));
            }
        }

    }

    /**
     * 
     * set play's strategy, computer or human role.
     */
    private void setPlayerStrategy() {
        Class<? extends GomokuAgent> blackPlayerStrategyClass =
                UIConstant.COMPUTER_PLAYER.equals(this.blackPlayerChoiceBox.getSelectionModel().getSelectedItem())
                        ? GomokuAlphaBetaPruning.class : null;
        Class<? extends GomokuAgent> whitePlayerStrategyClass =
                UIConstant.COMPUTER_PLAYER.equals(this.whitePlayerChoiceBox.getSelectionModel().getSelectedItem())
                        ? GomokuAlphaBetaPruning.class : null;
        this.gomokuManager.setBlackPlayerStrategyClass(blackPlayerStrategyClass);
        this.gomokuManager.setWhitePlayerStrategyClass(whitePlayerStrategyClass);
        this.gomokuManager.getCurrentPlayerProperty().set(blackPlayerStrategyClass);
    }

    /**
     * 
     * make move if current player is computer.
     * 
     * @param currentPlayerProperty current player's property.
     */
    private void makeStrategyMove(SimpleObjectProperty<Class<? extends GomokuAgent>> currentPlayerProperty) {
        if (null != currentPlayerProperty.get()) {
            this.gomokuManager.makeStrategyMove();
        }
    }

    /**
     * 
     * set announcement to text area according to chessboard state.
     * 
     * @param chessboardState chessboard state.
     */
    private void setAnnouncement(ChessboardState chessboardState) {
        String announcementString;
        switch (chessboardState) {
            case GAME_DRAW:
                announcementString = UIConstant.GAME_DRAW;
                break;
            case BLACK_WIN:
                announcementString = UIConstant.BLACK_WIN;
                break;
            case WHITE_WIN:
                announcementString = UIConstant.WHITE_WIN;
                break;
            case GAME_ON:
            default:
                announcementString = UIConstant.GAME_ON;
                break;
        }
        this.announcementTextArea.setText(announcementString);
    }

}
