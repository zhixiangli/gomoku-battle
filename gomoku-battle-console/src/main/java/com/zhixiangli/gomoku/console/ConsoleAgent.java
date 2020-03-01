package com.zhixiangli.gomoku.console;

import com.google.gson.Gson;
import com.zhixiangli.gomoku.console.common.ConsoleCommand;
import com.zhixiangli.gomoku.console.common.ConsoleRequest;
import com.zhixiangli.gomoku.console.common.ConsoleResponse;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.util.Scanner;

/**
 * @author zhixiangli
 */
public abstract class ConsoleAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConsoleAgent.class);

    public void start() {
        LOGGER.info("agent started");
        try (final Scanner reader = new Scanner(System.in)) {
            while (reader.hasNext()) {
                final String command = StringUtils.strip(reader.nextLine());
                LOGGER.info("received command {}", command);

                final ConsoleRequest req = new Gson().fromJson(command, ConsoleRequest.class);
                Point p = null;
                if (ConsoleCommand.NEXT_BLACK.getText().equals(req.getCommand().getText())) {
                    p = next(req.getChessboard(), ChessType.BLACK);
                } else if (ConsoleCommand.NEXT_WHITE.getText().equals(req.getCommand().getText())) {
                    p = next(req.getChessboard(), ChessType.WHITE);
                }
                if (null != p) {
                    final ConsoleResponse resp = new ConsoleResponse(p.x, p.y);
                    System.out.println(new Gson().toJson(resp));
                }
            }
        }
    }

    protected abstract Point next(String sgf, ChessType chessType);

}
