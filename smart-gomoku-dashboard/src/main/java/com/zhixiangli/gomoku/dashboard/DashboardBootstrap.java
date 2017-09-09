/**
 * 
 */
package com.zhixiangli.gomoku.dashboard;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.zhixiangli.gomoku.core.console.ConsoleMaster;
import com.zhixiangli.gomoku.dashboard.javafx.DashboardApplication;

/**
 * @author zhixiangli
 *
 */
public class DashboardBootstrap extends DashboardApplication {

    /**
     * @param args
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {

        Thread t = new Thread(new ConsoleMaster());
        t.setDaemon(true);
        t.start();

        DashboardApplication.launch(args);
    }

}
