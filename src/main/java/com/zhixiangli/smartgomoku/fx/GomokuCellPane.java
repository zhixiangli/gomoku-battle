/**
 * 
 */
package com.zhixiangli.smartgomoku.fx;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

/**
 * pane of each cell of chessboard.
 * 
 * @author lizhixiang
 * @date 2015年6月5日
 */
class GomokuCellPane extends Pane {
    
    public GomokuCellPane(int row, int column, GomokuFXManager gomokuManager) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/cellpane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(new GomokuCellController(row, column, gomokuManager));
        fxmlLoader.load();
        
        this.setPrefHeight(UIConstant.CELL_SIZE);
        this.setPrefWidth(UIConstant.CELL_SIZE);
        
        // set row index and column index when this pane is put in a grid pane.
        GridPane.setRowIndex(this, row);
        GridPane.setColumnIndex(this, column);
    }
    
}
