/**
 * 
 */
package com.zhixiangli.gomoku.core.console.common;

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
public class PlayerProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerProperties.class);

    public static String playerBlackCommand;

    public static String playerBlackAlias;

    public static String playerWhiteCommand;

    public static String playerWhiteAlias;

    static {
        try {
            PropertiesConfiguration playerConfig = new Configurations()
                    .properties(GomokuConst.class.getResource("/player.properties"));
            playerBlackCommand = playerConfig.getString("player.black.cmd");
            playerBlackAlias = playerConfig.getString("player.black.alias");
            playerWhiteCommand = playerConfig.getString("player.white.cmd");
            playerWhiteAlias = playerConfig.getString("player.white.alias");
        } catch (ConfigurationException e) {
            LOGGER.error("load player config error {}", e);
        }
    }

}
