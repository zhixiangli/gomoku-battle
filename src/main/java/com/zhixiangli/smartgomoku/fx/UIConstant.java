/**
 * 
 */
package com.zhixiangli.smartgomoku.fx;

import java.util.Calendar;

import com.zhixiangli.smartgomoku.model.Chessboard;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * UI constant.
 * 
 * @author lizhixiang
 * @date 2015年6月5日
 */
public class UIConstant {

	/**
	 * title.
	 */
	public static final String TITLE = "SMART GOMOKU";

	/**
	 * copyright.
	 */
	public static final String COPYRIGHT = String.format("©%d ZhixiangLi", Calendar.getInstance().get(Calendar.YEAR));

	/**
	 * human player text in choice box.
	 */
	public static final String HUMAN_PLAYER = "HUMAN";

	/**
	 * computer play text in choice box.
	 */
	public static final String COMPUTER_PLAYER = "COMPUTER";

	/**
	 * build choice box.
	 */
	public static final ObservableList<String> CHOICE_BOX_CHOICES = FXCollections.observableArrayList();

	static {
		CHOICE_BOX_CHOICES.add(UIConstant.HUMAN_PLAYER);
		CHOICE_BOX_CHOICES.add(UIConstant.COMPUTER_PLAYER);
	}

	/**
	 * length of each cell.
	 */
	public static final double CELL_SIZE = 800.0 / Chessboard.DEFAULT_SIZE;

	/**
	 * game on's text in announcement text area.
	 */
	public static final String GAME_ON = "game on";

	/**
	 * game draw's text in announcement text area.
	 */
	public static final String GAME_DRAW = "game draw";

	/**
	 * white player win's text in announcement text area.
	 */
	public static final String WHITE_WIN = "white win";

	/**
	 * black palyer win's text in announcement text area.
	 */
	public static final String BLACK_WIN = "black win";
}
