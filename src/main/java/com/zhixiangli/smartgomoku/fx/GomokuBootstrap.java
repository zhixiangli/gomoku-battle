/**
 * 
 */
package com.zhixiangli.smartgomoku.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * bootstrap of the game.
 * 
 * @author lizhixiang
 *
 */
public class GomokuBootstrap extends Application {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javafx.application.Application#start(javafx.stage.Stage)
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gomoku.fxml"));
		fxmlLoader.setController(new GomokuController());
		Scene scene = new Scene(fxmlLoader.load());
		scene.getStylesheets().add(getClass().getResource("/gomoku.css").toExternalForm());

		primaryStage.setTitle(String.format("%s (%s)", UIConstant.TITLE, UIConstant.COPYRIGHT));
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	/**
	 * main method.
	 * 
	 * @param args
	 *            arguments.
	 */
	public static void main(String[] args) {
		GomokuBootstrap.launch(args);
	}

}
