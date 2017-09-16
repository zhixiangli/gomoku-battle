package gomoku

import (
	"testing"
)

func TestBoard_Get(t *testing.T) {
	board := NewBoard(12, 13)
	board.SetRune(Location{2, 4}, 'B')
	if board.Get(2, 4) != board.GetByLocation(Location{2, 4}) || board.Get(2, 4) != Black {
		t.Error()
	}
	if board.nonEmptyCount != 1 {
		t.Error()
	}
}
