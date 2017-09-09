/**
 * 
 */
package com.zhixiangli.gomoku.core.common;

import java.awt.Point;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * constant.
 * 
 * @author lizhixiang
 * @date 2015年6月6日
 */
public class GomokuConst {

    private static final Logger LOGGER = LoggerFactory.getLogger(GomokuConst.class);

    /**
     * four directions.
     */
    public static final Point[] DIRECTIONS = { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(-1, 1) };

    /**
     * continuous number to win the game.
     */
    public static final int CONSECUTIVE_NUM = 5;

    /**
     * the number of grid in the chessboard.
     */
    public static final int CHESSBOARD_SIZE = 15;

    public class ChessChar {
        public static final char EMPTY = '.';
        public static final char BLACK = 'B';
        public static final char WHITE = 'W';
    }

    public static class Player {

        public static String playerBlackCommand;

        public static String playerBlackAlias;

        public static String playerWhiteCommand;

        public static String playerWhiteAlias;

        static {
            try {
                PropertiesConfiguration playerConfig = new Configurations()
                        .properties(GomokuConst.class.getResource("/gomoku_player.properties"));
                Player.playerBlackCommand = playerConfig.getString("player.black.cmd");
                Player.playerBlackAlias = playerConfig.getString("player.black.alias");
                Player.playerWhiteCommand = playerConfig.getString("player.white.cmd");
                Player.playerWhiteAlias = playerConfig.getString("player.white.alias");
            } catch (ConfigurationException e) {
                LOGGER.error("load player config error {}", e);
            }
        }
    }
}
