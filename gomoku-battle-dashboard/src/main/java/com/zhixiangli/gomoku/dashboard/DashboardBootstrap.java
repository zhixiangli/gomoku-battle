package com.zhixiangli.gomoku.dashboard;

import com.zhixiangli.gomoku.console.ConsoleBootstrap;
import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.dashboard.javafx.DashboardApplication;
import javafx.application.Application;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

/**
 * Launcher class that does not extend Application, so that JavaFX can be
 * started from a fat JAR without module-path arguments on Java 17+.
 *
 * @author zhixiangli
 */
public class DashboardBootstrap {

    public static void main(final String[] args) throws ParseException, IOException {
        final Options options = new Options();
        options.addOption(PlayerProperties.PLAYER_CONF, true, "player properties path");
        final CommandLine cmd = new DefaultParser().parse(options, args);
        new ConsoleBootstrap(cmd.getOptionValue(PlayerProperties.PLAYER_CONF)).startDaemon();
        Application.launch(DashboardApplication.class, args);
    }

}
