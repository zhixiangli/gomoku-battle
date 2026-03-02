package com.zhixiangli.gomoku.core.service;

import com.zhixiangli.gomoku.core.chessboard.ChessState;
import com.zhixiangli.gomoku.core.chessboard.ChessType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests for ChessboardService state transitions.
 */
public class ChessboardServiceTest {

    private ChessboardService service;

    @Before
    public void setUp() {
        service = ChessboardService.getInstance();
        service.restart();
    }

    @Test
    public void testRestartSetsGameOn() {
        Assert.assertEquals(ChessState.GAME_ON, service.getChessState());
        Assert.assertEquals(ChessType.BLACK, service.getCurrentChessType());
    }

    @Test
    public void testStateChangesToBlackWin() {
        List<ChessState> states = new ArrayList<>();
        service.addChessStateChangeListener((obs, oldVal, newVal) -> states.add(newVal));

        // Black: row 0, columns 0-4 (5 in a row)
        // White: row 1, columns 0-3
        service.takeMove(new Point(0, 0)); // Black
        service.takeMove(new Point(1, 0)); // White
        service.takeMove(new Point(0, 1)); // Black
        service.takeMove(new Point(1, 1)); // White
        service.takeMove(new Point(0, 2)); // Black
        service.takeMove(new Point(1, 2)); // White
        service.takeMove(new Point(0, 3)); // Black
        service.takeMove(new Point(1, 3)); // White
        service.takeMove(new Point(0, 4)); // Black wins

        Assert.assertEquals(ChessState.BLACK_WIN, service.getChessState());
        Assert.assertEquals(ChessType.EMPTY, service.getCurrentChessType());
        Assert.assertTrue(states.contains(ChessState.BLACK_WIN));
    }

    @Test
    public void testStateChangesToWhiteWin() {
        // Black makes a non-winning move first, then White builds 5 in a row
        service.takeMove(new Point(0, 0)); // Black
        service.takeMove(new Point(1, 0)); // White
        service.takeMove(new Point(0, 1)); // Black
        service.takeMove(new Point(1, 1)); // White
        service.takeMove(new Point(0, 2)); // Black
        service.takeMove(new Point(1, 2)); // White
        service.takeMove(new Point(0, 3)); // Black
        service.takeMove(new Point(1, 3)); // White
        service.takeMove(new Point(2, 5)); // Black (non-winning)
        service.takeMove(new Point(1, 4)); // White wins

        Assert.assertEquals(ChessState.WHITE_WIN, service.getChessState());
        Assert.assertEquals(ChessType.EMPTY, service.getCurrentChessType());
    }

    @Test
    public void testCurrentChessTypeEmptyAfterWin() {
        // After a win, currentChessType should be EMPTY
        service.takeMove(new Point(0, 0)); // Black
        service.takeMove(new Point(1, 0)); // White
        service.takeMove(new Point(0, 1)); // Black
        service.takeMove(new Point(1, 1)); // White
        service.takeMove(new Point(0, 2)); // Black
        service.takeMove(new Point(1, 2)); // White
        service.takeMove(new Point(0, 3)); // Black
        service.takeMove(new Point(1, 3)); // White
        service.takeMove(new Point(0, 4)); // Black wins

        Assert.assertEquals(ChessType.EMPTY, service.getCurrentChessType());
    }

    @Test
    public void testRestartAfterWin() {
        // Play a game to completion
        service.takeMove(new Point(0, 0)); // Black
        service.takeMove(new Point(1, 0)); // White
        service.takeMove(new Point(0, 1)); // Black
        service.takeMove(new Point(1, 1)); // White
        service.takeMove(new Point(0, 2)); // Black
        service.takeMove(new Point(1, 2)); // White
        service.takeMove(new Point(0, 3)); // Black
        service.takeMove(new Point(1, 3)); // White
        service.takeMove(new Point(0, 4)); // Black wins

        Assert.assertEquals(ChessState.BLACK_WIN, service.getChessState());

        // Restart should reset to GAME_ON
        service.restart();
        Assert.assertEquals(ChessState.GAME_ON, service.getChessState());
        Assert.assertEquals(ChessType.BLACK, service.getCurrentChessType());
    }
}
