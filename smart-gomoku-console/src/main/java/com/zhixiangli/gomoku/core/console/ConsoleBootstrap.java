/**
 * 
 */
package com.zhixiangli.gomoku.core.console;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.zhixiangli.gomoku.core.chessboard.ChessState;
import com.zhixiangli.gomoku.core.service.ChessboardService;

/**
 * @author zhixiangli
 *
 */
public class ConsoleBootstrap extends ConsoleMaster implements Runnable {

    private ChessboardService chessboardService = ChessboardService.getInstance();

    public ConsoleBootstrap() throws FileNotFoundException, IOException {
        super();
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

}
