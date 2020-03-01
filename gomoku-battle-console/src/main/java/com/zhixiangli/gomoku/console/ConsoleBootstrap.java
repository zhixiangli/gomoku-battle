package com.zhixiangli.gomoku.console;

import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.core.chessboard.ChessState;
import com.zhixiangli.gomoku.core.service.ChessboardService;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;

/**
 * @author zhixiangli
 */
public class ConsoleBootstrap extends ConsoleMaster implements Runnable {

    private final ChessboardService chessboardService = ChessboardService.getInstance();

    public ConsoleBootstrap(final String playProperties) throws IOException {
        super(playProperties);
    }

    @Override
    public void run() {
    }

    public Thread startDaemon() {
        final Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
        return t;
    }

    public void startLoop() throws InterruptedException {
        chessboardService.addChessStateChangeListener((observable, oldValue, newValue) -> {
            if (newValue != ChessState.GAME_ON) {
                chessboardService.restart();
            }
        });
        chessboardService.restart();
        startDaemon().join();
    }

    public static void main(final String[] args) throws ParseException, IOException, InterruptedException {
        final Options options = new Options();
        options.addOption(PlayerProperties.PLAYER_CONF, true, "player properties path");
        final CommandLine cmd = new DefaultParser().parse(options, args);
        new ConsoleBootstrap(cmd.getOptionValue(PlayerProperties.PLAYER_CONF)).startLoop();
    }

}
