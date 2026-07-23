package com.zhixiangli.gomoku.console.common;

import com.zhixiangli.gomoku.console.common.PlayerProperties.PlayerType;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import org.junit.Test;

import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PlayerPropertiesTest {

    @Test
    public void testDefaultPlayerTypesAndAlias() {
        PlayerProperties.parse(getClass().getClassLoader().getResource("human_player.properties").getPath());
        assertEquals(PlayerType.ALPHABETA, PlayerProperties.getPlayerType(ChessType.BLACK));
        assertEquals(PlayerType.ALPHAZERO, PlayerProperties.getPlayerType(ChessType.WHITE));
        assertEquals("Alpha-Beta Search", PlayerProperties.getPlayerAlias(ChessType.BLACK));
        assertEquals("AlphaZero", PlayerProperties.getPlayerAlias(ChessType.WHITE));
    }

    @Test
    public void testPlayerTypeCanBeUpdated() {
        PlayerProperties.parse(getClass().getClassLoader().getResource("ai_player.properties").getPath());
        PlayerProperties.setPlayerType(ChessType.BLACK, PlayerType.HUMAN);
        PlayerProperties.setPlayerType(ChessType.WHITE, PlayerType.ALPHABETA);

        assertEquals("", PlayerProperties.getPlayerCommand(ChessType.BLACK));
        assertEquals("echo alpha-beta", PlayerProperties.getPlayerCommand(ChessType.WHITE));
        assertEquals("Human", PlayerProperties.getPlayerAlias(ChessType.BLACK));
        assertEquals("Alpha-Beta Search", PlayerProperties.getPlayerAlias(ChessType.WHITE));
    }

    @Test
    public void testAlphaZeroCommand() {
        PlayerProperties.parse(getClass().getClassLoader().getResource("mixed_player.properties").getPath());
        PlayerProperties.setPlayerType(ChessType.BLACK, PlayerType.ALPHAZERO);
        assertEquals("echo alpha-zero", PlayerProperties.getPlayerCommand(ChessType.BLACK));
    }

    @Test
    public void parseRejectsMissingConfigurationFile() {
        final Path missingFile = Path.of("target", "missing-player.properties");

        try {
            PlayerProperties.parse(missingFile.toString());
            fail("Expected PlayerProperties.parse to reject a missing file");
        } catch (final IllegalArgumentException expected) {
            assertEquals("Player configuration file does not exist: " + missingFile, expected.getMessage());
        }
    }

    @Test
    public void parseRejectsMissingAgentCommand() {
        final String configPath = getClass().getClassLoader().getResource("invalid_player.properties").getPath();

        try {
            PlayerProperties.parse(configPath);
            fail("Expected PlayerProperties.parse to reject an incomplete configuration");
        } catch (final IllegalArgumentException expected) {
            assertEquals("Missing required property: agent.alphazero.cmd", expected.getMessage());
        }
    }
}
