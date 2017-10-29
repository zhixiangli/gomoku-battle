package mcts

import (
	"testing"

	"common"
	"gomoku"
)

func init() {
	common.InitConfig("../../conf/conf.json")
}

func TestDNNPolicy_Around(t *testing.T) {
	board := gomoku.NewBoard()
	board.SetChessType(&gomoku.Location{9, 0}, gomoku.White)
	board.SetChessType(&gomoku.Location{9, 1}, gomoku.Black)
	board.SetChessType(&gomoku.Location{8, 1}, gomoku.Black)
	policy := DNNPolicy{}
	actual := policy.Around(board, 1)
	expected := []gomoku.Location{gomoku.Location{7, 0}, gomoku.Location{7, 1}, gomoku.Location{7, 2}, gomoku.Location{8, 0}, gomoku.Location{8, 2}, gomoku.Location{9, 2}, gomoku.Location{10, 0}, gomoku.Location{10, 1}, gomoku.Location{10, 2}}
	if len(actual) != len(expected) {
		t.Error()
		for i := range actual {
			if actual[i] != expected[i] {
				t.Error()
			}
		}
	}
}

func TestDNNPolicy_Evaluate(t *testing.T) {
	root := MonteCarloTreeNode{}
	root.numOfGame, root.numOfWin = 1, 1
	root.childrenLoc = []gomoku.Location{gomoku.Location{1, 2}, gomoku.Location{3, 4}}
	root.childrenNode = make([]MonteCarloTreeNode, 2)
	policy := DNNPolicy{}
	value := policy.Evaluate(&root, 0)
	if value <= 0 {
		t.Error()
	}
}

func TestDNNPolicy_Simulate(t *testing.T) {
	policy := DNNPolicy{}
	black, white, empty := 0, 0, 0
	board := gomoku.NewBoard()
	board.SetChessType(&gomoku.Location{7, 7}, gomoku.Black)
	board.SetChessType(&gomoku.Location{7, 8}, gomoku.Black)
	board.SetChessType(&gomoku.Location{7, 9}, gomoku.Black)
	for i := 0; i < 100; i++ {
		_, _, winType := policy.Simulate(*board.Clone(), gomoku.Black)
		switch winType {
		case gomoku.Black:
			black++
		case gomoku.Empty:
			empty++
		case gomoku.White:
			white++
		default:
			t.Error()
		}
	}
	t.Logf("black: %d, white: %d, empty: %d", black, white, empty)
	if black == 0 || white == 0 {
		t.Error()
	}
}
