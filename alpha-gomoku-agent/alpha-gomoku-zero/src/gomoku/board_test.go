package gomoku

import (
	"common"
	"testing"
)

func init() {
	common.InitConfig("../../conf/conf.json")
}

func TestBoard_Get(t *testing.T) {
	board := NewBoard()
	board.SetRune(&Location{2, 4}, 'B')
	if board.Get(2, 4) != board.GetByLocation(&Location{2, 4}) || board.Get(2, 4) != Black {
		t.Error()
	}
	if board.nonEmptyCount != 1 {
		t.Error()
	}
}
