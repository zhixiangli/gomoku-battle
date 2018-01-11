/**
 * 
 */
package com.zhixiangli.gomoku.dashboard.javafx;

import java.util.Calendar;

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
public class DashboardApplication extends Application {

    /*
     * (non-Javadoc)
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gomoku_pane.fxml"));
        fxmlLoader.setController(new DashboardController());

        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/gomoku_style.css").toExternalForm());

        String title = String.format("ALPHA GOMOKU (©%d LI ZHIXIANG)", Calendar.getInstance().get(Calendar.YEAR));
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}
