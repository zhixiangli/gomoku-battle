package com.zhixiangli.gomoku.console.common;

import com.zhixiangli.gomoku.core.chessboard.ChessType;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * @author zhixiangli
 */
public class PlayerProperties {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerProperties.class);

    public static final String PLAYER_CONF = "player";

    private static final String HUMAN_ALIAS = "Human";
    private static final String ALPHAZERO_ALIAS = "AlphaZero";
    private static final String ALPHABETA_ALIAS = "Alpha-Beta Search";

    private static PlayerConfiguration configuration;

    private static PlayerType blackPlayerType = PlayerType.ALPHABETA;
    private static PlayerType whitePlayerType = PlayerType.ALPHAZERO;

    private PlayerProperties() {
    }

    public static synchronized void parse(final String configPath) {
        if (StringUtils.isBlank(configPath)) {
            throw new IllegalArgumentException("Player configuration path is required");
        }
        final File configFile = new File(configPath);
        if (!configFile.isFile()) {
            throw new IllegalArgumentException("Player configuration file does not exist: " + configPath);
        }
        try {
            final PropertiesConfiguration playerConfig = new Configurations().properties(configFile);
            final String alphaZeroCommand = requiredCommand(playerConfig, "agent.alphazero.cmd");
            final String alphaBetaCommand = requiredCommand(playerConfig, "agent.alphabeta.cmd");
            configuration = new PlayerConfiguration(alphaZeroCommand, alphaBetaCommand);
            blackPlayerType = PlayerType.ALPHABETA;
            whitePlayerType = PlayerType.ALPHAZERO;
        } catch (final ConfigurationException e) {
            throw new IllegalArgumentException("Unable to load player configuration: " + configPath, e);
        }
    }

    private static String requiredCommand(final PropertiesConfiguration playerConfig, final String propertyName) {
        final String command = StringUtils.trimToNull(playerConfig.getString(propertyName));
        if (command == null) {
            throw new IllegalArgumentException("Missing required property: " + propertyName);
        }
        return command;
    }

    public static synchronized void setPlayerType(final ChessType chessType, final PlayerType playerType) {
        final PlayerType newType = playerType == null ? PlayerType.HUMAN : playerType;
        if (chessType == ChessType.BLACK) {
            blackPlayerType = newType;
        } else if (chessType == ChessType.WHITE) {
            whitePlayerType = newType;
        }
    }

    public static synchronized PlayerType getPlayerType(final ChessType chessType) {
        if (chessType == ChessType.BLACK) {
            return blackPlayerType;
        }
        if (chessType == ChessType.WHITE) {
            return whitePlayerType;
        }
        return PlayerType.HUMAN;
    }

    public static synchronized String getPlayerAlias(final ChessType chessType) {
        return getPlayerType(chessType).getAlias();
    }

    public static synchronized String getPlayerCommand(final ChessType chessType) {
        if (configuration == null) {
            throw new IllegalStateException("Player configuration has not been loaded");
        }
        return getPlayerType(chessType).getCommand(configuration.alphaZeroCommand(), configuration.alphaBetaCommand());
    }

    private record PlayerConfiguration(String alphaZeroCommand, String alphaBetaCommand) {
    }

    public enum PlayerType {
        HUMAN(HUMAN_ALIAS),
        ALPHAZERO(ALPHAZERO_ALIAS),
        ALPHABETA(ALPHABETA_ALIAS);

        private final String alias;

        PlayerType(final String alias) {
            this.alias = alias;
        }

        public String getAlias() {
            return alias;
        }

        public String getCommand(final String alphazeroCmd, final String alphabetaCmd) {
            switch (this) {
                case ALPHAZERO:
                    return StringUtils.trimToEmpty(alphazeroCmd);
                case ALPHABETA:
                    return StringUtils.trimToEmpty(alphabetaCmd);
                case HUMAN:
                default:
                    return StringUtils.EMPTY;
            }
        }

        @Override
        public String toString() {
            return alias;
        }
    }

}
