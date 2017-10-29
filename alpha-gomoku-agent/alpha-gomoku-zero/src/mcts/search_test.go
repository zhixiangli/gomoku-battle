package mcts

import (
	"testing"

	"gomoku"
)

func TestMCTS_Search(t *testing.T) {
	policy := DNNPolicy{}
	searcher := &MCTS{&policy}
	root := new(MonteCarloTreeNode)
	board := gomoku.NewBoard()
	board.SetChessType(&gomoku.Location{7, 7}, gomoku.Black)
	board.SetChessType(&gomoku.Location{7, 8}, gomoku.Black)
	board.SetChessType(&gomoku.Location{7, 9}, gomoku.Black)
	newBoard := board.Clone()
	for i := 0; i < 10000; i++ {
		searcher.Search(newBoard, gomoku.White, root)
		if !board.Equals(newBoard) {
			t.Error()
		}
	}
	index, proba := searcher.BestChildNode(root)
	t.Logf("%d %d: %f\n", root.childrenLoc[index].X, root.childrenLoc[index].Y, proba)
	if proba >= 0.5 {
		t.Error()
	}
}

func TestMCTS_Next(t *testing.T) {
	board := gomoku.NewBoard()
	board.SetChessType(&gomoku.Location{7, 7}, gomoku.Black)
	board.SetChessType(&gomoku.Location{7, 8}, gomoku.Black)
	board.SetChessType(&gomoku.Location{7, 9}, gomoku.Black)
	policy := DNNPolicy{}
	searcher := &MCTS{&policy}
	loc, _ := searcher.Next(board, gomoku.White)
	first, second := gomoku.Location{7, 10}, gomoku.Location{7, 6}
	if *loc != first && *loc != second {
		t.Error()
	}
}

func BenchmarkMCTS_Search(b *testing.B) {
	policy := DNNPolicy{}
	searcher := &MCTS{&policy}
	root := new(MonteCarloTreeNode)
	board := gomoku.NewBoard()
	for i := 0; i < b.N; i++ {
		searcher.Search(board, gomoku.Black, root)
	}
}
