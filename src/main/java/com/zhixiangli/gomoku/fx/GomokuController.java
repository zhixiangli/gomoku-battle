/**
 * 
 */
package com.zhixiangli.gomoku.fx;

import java.io.IOException;

import com.zhixiangli.gomoku.agent.GomokuAgent;
import com.zhixiangli.gomoku.chessboard.ChessPlayer;
import com.zhixiangli.gomoku.chessboard.ChessState;
import com.zhixiangli.gomoku.common.GomokuConstant;
import com.zhixiangli.gomoku.service.GomokuService;

import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	private ChoiceBox<ChessPlayer> blackPlayerChoiceBox;

	/**
	 * white player choice box.
	 */
	@FXML
	private ChoiceBox<ChessPlayer> whitePlayerChoiceBox;

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
	private GomokuService gomokuService;

	/**
	 * 
	 * initialize.
	 * 
	 * @throws IOException
	 */
	@FXML
	public void initialize() throws IOException {
		gomokuService = new GomokuService();

		// initialize choice box.
		initializeChoiceBoxes();

		// add listener to update announcement when chessboard state is changed.
		initailizeAnnouncement();

		// add each cell pane to grid pane.
		initializeChessboardGridPane();

		// initialize strategy according to the choice box.
		initializePlayerStrategy();

	}

	private void initailizeAnnouncement() {
		this.announcementTextArea.setEditable(false);
		SimpleObjectProperty<ChessState> chessboardStateProperty = this.gomokuService.getChessStateProperty();
		this.setAnnouncement(chessboardStateProperty.get());
		chessboardStateProperty.addListener(event -> this.setAnnouncement(chessboardStateProperty.get()));
	}

	private void initializeChessboardGridPane() throws IOException {
		this.chessboardGridPane.getChildren().clear();
		for (int row = 0; row < GomokuConstant.CHESSBOARD_GRID_NUM; ++row) {
			for (int column = 0; column < GomokuConstant.CHESSBOARD_GRID_NUM; ++column) {
				this.chessboardGridPane.getChildren().add(new GomokuCellPane(row, column, this.gomokuService));
			}
		}
	}

	/**
	 * 
	 * set play's strategy, computer or human role.
	 */
	private void initializePlayerStrategy() {
		Class<? extends GomokuAgent> blackPlayerStrategyClass = blackPlayerChoiceBox.getSelectionModel()
				.getSelectedItem().getAgent();
		this.gomokuService.setBlackPlayerStrategyClass(blackPlayerStrategyClass);

		Class<? extends GomokuAgent> whitePlayerStrategyClass = whitePlayerChoiceBox.getSelectionModel()
				.getSelectedItem().getAgent();
		this.gomokuService.setWhitePlayerStrategyClass(whitePlayerStrategyClass);

		this.gomokuService.getCurrentAgentProperty().set(blackPlayerStrategyClass);
	}

	private void initializeChoiceBoxes() {
		ObservableList<ChessPlayer> items = FXCollections.observableArrayList(ChessPlayer.values());
		if (blackPlayerChoiceBox.getItems().isEmpty()) {
			blackPlayerChoiceBox.setItems(items);
			blackPlayerChoiceBox.getSelectionModel().select(0);
		}
		if (whitePlayerChoiceBox.getItems().isEmpty()) {
			whitePlayerChoiceBox.setItems(items);
			whitePlayerChoiceBox.getSelectionModel().select(1);
		}
	}

	/**
	 * 
	 * set announcement to text area according to chessboard state.
	 * 
	 * @param chessboardState
	 *            chessboard state.
	 */
	private void setAnnouncement(ChessState chessboardState) {
		String announcementString;
		switch (chessboardState) {
		case GAME_DRAW:
			announcementString = ChessState.GAME_DRAW.name();
			break;
		case BLACK_WIN:
			announcementString = ChessState.BLACK_WIN.name();
			break;
		case WHITE_WIN:
			announcementString = ChessState.WHITE_WIN.name();
			break;
		case GAME_ON:
		default:
			announcementString = ChessState.GAME_ON.name();
			break;
		}
		this.announcementTextArea.setText(announcementString);
	}

}
