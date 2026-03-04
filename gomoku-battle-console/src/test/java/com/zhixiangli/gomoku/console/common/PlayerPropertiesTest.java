package com.zhixiangli.gomoku.console.common;

import com.zhixiangli.gomoku.console.common.PlayerProperties.PlayerType;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}
