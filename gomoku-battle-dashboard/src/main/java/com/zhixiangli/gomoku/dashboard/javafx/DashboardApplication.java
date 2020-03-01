package com.zhixiangli.gomoku.dashboard.javafx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Calendar;

/**
 * bootstrap of the game.
 *
 * @author lizhixiang
 */
public class DashboardApplication extends Application {

    /*
     * (non-Javadoc)
     *
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(final Stage primaryStage) throws IOException {
        final FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gomoku_pane.fxml"));
        fxmlLoader.setController(new DashboardController());

        final Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/gomoku_style.css").toExternalForm());

        final String title = String.format("ALPHA GOMOKU (©%d LI ZHIXIANG)", Calendar.getInstance().get(Calendar.YEAR));
        primaryStage.setTitle(title);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

}
