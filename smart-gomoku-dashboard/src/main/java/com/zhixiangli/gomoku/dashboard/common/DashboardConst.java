/**
 * 
 */
package com.zhixiangli.gomoku.dashboard.common;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zhixiangli.gomoku.core.common.GomokuConst;

/**
 * @author zhixiangli
 *
 */
public class DashboardConst {

    private static final Logger LOGGER = LoggerFactory.getLogger(DashboardConst.class);

    public static class Player {

        public static String playerBlackCommand;

        public static String playerBlackAlias;

        public static String playerWhiteCommand;

        public static String playerWhiteAlias;

        static {
            try {
                PropertiesConfiguration playerConfig = new Configurations()
                        .properties(DashboardConst.class.getResource("/gomoku_player.properties"));
                Player.playerBlackCommand = playerConfig.getString("player.black.cmd");
                Player.playerBlackAlias = playerConfig.getString("player.black.alias");
                Player.playerWhiteCommand = playerConfig.getString("player.white.cmd");
                Player.playerWhiteAlias = playerConfig.getString("player.white.alias");
            } catch (ConfigurationException e) {
                LOGGER.error("load player config error {}", e);
            }
        }
    }

    /**
     * length of each cell.
     */
    public static final double GRID_LENGTH = 800.0 / GomokuConst.CHESSBOARD_SIZE;

}
