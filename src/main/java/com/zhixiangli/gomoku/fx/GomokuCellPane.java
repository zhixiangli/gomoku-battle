/**
 * 
 */
package com.zhixiangli.gomoku.fx;

import java.io.IOException;

import com.zhixiangli.gomoku.chessboard.ChessType;
import com.zhixiangli.gomoku.common.GomokuConstant;
import com.zhixiangli.gomoku.service.GomokuService;
import com.zhixiangli.gomoku.chessboard.ChessState;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleIntegerProperty;
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
 * @date 2015年6月5日
 */
class GomokuCellPane extends Pane {

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
	 * row index of this cell.
	 */
	private int rowIndex;

	/**
	 * column index of this cell.
	 */
	private int columnIndex;

	/**
	 * Gomoku Service.
	 */
	private GomokuService gomokuService;

	public GomokuCellPane(int rowIndex, int columnIndex, GomokuService gomokuService) throws IOException {
		this.rowIndex = rowIndex;
		this.columnIndex = columnIndex;
		this.gomokuService = gomokuService;

		this.setPrefHeight(GomokuConstant.GRID_SIZE);
		this.setPrefWidth(GomokuConstant.GRID_SIZE);

		// set row index and column index when this pane is put in a grid pane.
		GridPane.setRowIndex(this, rowIndex);
		GridPane.setColumnIndex(this, columnIndex);

		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/chess_pane.fxml"));
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

		// pane is disabled when following contition meets.
		this.cellPane.disableProperty().bind(gomokuService.getChessStateProperty().isNotEqualTo(ChessState.GAME_ON).or(
				gomokuService.getChessboardProperty(rowIndex, columnIndex).isNotEqualTo(ChessType.EMPTY.ordinal())));

		// location of this chess piece.
		this.cellCircle.layoutXProperty().bind(this.cellPane.widthProperty().divide(2));
		this.cellCircle.layoutYProperty().bind(this.cellPane.heightProperty().divide(2));

		// radius of this chess piece.
		this.cellCircle.radiusProperty()
				.bind(Bindings.min(cellPane.widthProperty(), cellPane.heightProperty()).divide(2.5));

		// the chess type of this cell, and add listener when the chess type
		// is changed.
		SimpleIntegerProperty chessTypeProperty = this.gomokuService.getChessboardProperty(rowIndex, columnIndex);
		this.updateGrid(chessTypeProperty);
		chessTypeProperty.addListener(event -> this.updateGrid(chessTypeProperty));
	}

	private void updateGrid(SimpleIntegerProperty chessTypeProperty) {
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
	public void moveOnMouseClicked() {
		this.gomokuService.moveOnMouseClicked(rowIndex, columnIndex);
	}

}
