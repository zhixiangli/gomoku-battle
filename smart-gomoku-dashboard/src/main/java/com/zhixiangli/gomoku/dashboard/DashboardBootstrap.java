/**
 * 
 */
package com.zhixiangli.gomoku.dashboard;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import com.zhixiangli.gomoku.console.ConsoleBootstrap;
import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.dashboard.javafx.DashboardApplication;

/**
 * @author zhixiangli
 *
 */
public class DashboardBootstrap extends DashboardApplication {

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(PlayerProperties.PLAYER_CONF, true, "player properties path");
        CommandLine cmd = new DefaultParser().parse(options, args);
        new ConsoleBootstrap(cmd.getOptionValue(PlayerProperties.PLAYER_CONF)).startDaemon();
        DashboardApplication.launch(args);
    }

}
