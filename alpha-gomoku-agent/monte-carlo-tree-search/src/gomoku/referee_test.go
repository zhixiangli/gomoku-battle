package gomoku

import (
	"testing"
)

func TestReferee_IsWin(t *testing.T) {
	board := NewBoard()
	board.SetRune(&Location{1, 1}, 'W')
	board.SetRune(&Location{2, 1}, 'W')
	board.SetRune(&Location{3, 1}, 'W')
	board.SetRune(&Location{4, 1}, 'W')
	if Referee.IsWin(board, &Location{3, 1}) {
		t.Error()
	}
	board.SetRune(&Location{5, 1}, 'W')
	if !Referee.IsWin(board, &Location{3, 1}) {
		t.Error()
	}
}
