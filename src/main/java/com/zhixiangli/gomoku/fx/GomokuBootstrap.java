/**
 * 
 */
package com.zhixiangli.gomoku.fx;

import com.zhixiangli.gomoku.common.GomokuConstant;

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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gomoku_pane.fxml"));
        fxmlLoader.setController(new GomokuController());
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/gomoku_style.css").toExternalForm());

        primaryStage.setTitle(String.format("%s (%s)", GomokuConstant.TITLE, GomokuConstant.COPYRIGHT));
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * main method.
     * 
     * @param args arguments.
     */
    public static void main(String[] args) {
        GomokuBootstrap.launch(args);
    }

}
