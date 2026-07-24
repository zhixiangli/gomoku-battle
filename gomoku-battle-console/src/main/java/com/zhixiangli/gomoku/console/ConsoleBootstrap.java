package com.zhixiangli.gomoku.console;

import com.zhixiangli.gomoku.console.common.PlayerProperties;
import com.zhixiangli.gomoku.core.chessboard.ChessState;
import com.zhixiangli.gomoku.core.service.ChessboardService;
import javafx.beans.value.ChangeListener;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author zhixiangli
 */
public class ConsoleBootstrap extends ConsoleMaster {

    private final ChessboardService chessboardService = ChessboardService.getInstance();

    public ConsoleBootstrap(final String playProperties) throws IOException {
        super(playProperties);
    }

    public void startLoop() throws InterruptedException {
        final ChangeListener<ChessState> restartListener = (observable, oldValue, newValue) -> {
            if ((newValue == ChessState.GAME_DRAW) || (newValue == ChessState.WHITE_WIN)
                    || (newValue == ChessState.BLACK_WIN)) {
                chessboardService.restart();
            }
        };
        chessboardService.addChessStateChangeListener(restartListener);
        try {
            chessboardService.restart();
            new CountDownLatch(1).await();
        } finally {
            chessboardService.removeChessStateChangeListener(restartListener);
        }
    }

    public static Options createOptions() {
        final Options options = new Options();
        options.addOption(org.apache.commons.cli.Option.builder(PlayerProperties.PLAYER_CONF)
                .hasArg()
                .required()
                .desc("player properties path")
                .build());
        return options;
    }

    public static void main(final String[] args) throws ParseException, IOException, InterruptedException {
        final CommandLine cmd = new DefaultParser().parse(createOptions(), args);
        try (ConsoleBootstrap bootstrap = new ConsoleBootstrap(cmd.getOptionValue(PlayerProperties.PLAYER_CONF))) {
            bootstrap.startLoop();
        }
    }

}
