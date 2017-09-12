/**
 * 
 */
package com.zhixiangli.gomoku.dashboard;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.zhixiangli.gomoku.core.console.ConsoleBootstrap;
import com.zhixiangli.gomoku.dashboard.javafx.DashboardApplication;

/**
 * @author zhixiangli
 *
 */
public class DashboardBootstrap extends DashboardApplication {

    public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
        new ConsoleBootstrap().startDaemon();
        DashboardApplication.launch(args);
    }

}
