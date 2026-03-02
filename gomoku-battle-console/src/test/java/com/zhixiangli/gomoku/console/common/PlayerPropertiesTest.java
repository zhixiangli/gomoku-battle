package com.zhixiangli.gomoku.console.common;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PlayerPropertiesTest {

    @Test
    public void testHumanPlayerDefaultAlias() {
        PlayerProperties.parse(getClass().getClassLoader().getResource("human_player.properties").getPath());
        assertEquals("Human", PlayerProperties.playerBlackAlias);
        assertEquals("Human", PlayerProperties.playerWhiteAlias);
    }

    @Test
    public void testAiPlayerAliasUnchanged() {
        PlayerProperties.parse(getClass().getClassLoader().getResource("ai_player.properties").getPath());
        assertEquals("TestBlack", PlayerProperties.playerBlackAlias);
        assertEquals("TestWhite", PlayerProperties.playerWhiteAlias);
    }

    @Test
    public void testMixedPlayerAlias() {
        PlayerProperties.parse(getClass().getClassLoader().getResource("mixed_player.properties").getPath());
        assertEquals("Human", PlayerProperties.playerBlackAlias);
        assertEquals("TestWhite", PlayerProperties.playerWhiteAlias);
    }
}
