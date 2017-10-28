/**
 * 
 */
package com.zhixiangli.gomoku.console;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;

import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.core.chessboard.ChessState;
import com.zhixiangli.gomoku.core.service.ChessboardService;

/**
 * @author zhixiangli
 *
 */
public class ConsoleBootstrap extends ConsoleMaster implements Runnable {

    private ChessboardService chessboardService = ChessboardService.getInstance();

    public ConsoleBootstrap(String playProperties) throws FileNotFoundException, IOException {
        super(playProperties);
    }

    @Override
    public void run() {
    }

    public Thread startDaemon() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
        return t;
    }

    public void startLoop() throws InterruptedException {
        this.chessboardService.addChessStateChangeListener((observable, oldValue, newValue) -> {
            if (newValue != ChessState.GAME_ON) {
                this.chessboardService.restart();
            }
        });
        this.chessboardService.restart();
        this.startDaemon().join();
    }

    public static void main(String[] args) throws Exception {
        Options options = new Options();
        options.addOption(PlayerProperties.PLAYER_CONF, true, "player properties path");
        CommandLine cmd = new DefaultParser().parse(options, args);
        new ConsoleBootstrap(cmd.getOptionValue(PlayerProperties.PLAYER_CONF)).startLoop();
    }

}
