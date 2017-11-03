package mcts

import (
	"testing"

	"gomoku"
)

func TestMonteCarloTree_Search(t *testing.T) {
	policy := RandomPolicy{}
	tree := &MonteCarloTree{new(MonteCarloTreeNode), &policy}
	board := gomoku.NewBoard()
	board.SetChessType(&gomoku.Location{7, 7}, gomoku.Black)
	board.SetChessType(&gomoku.Location{7, 8}, gomoku.Black)
	board.SetChessType(&gomoku.Location{7, 9}, gomoku.Black)
	newBoard := board.Clone()
	for i := 0; i < 10000; i++ {
		tree.Search(newBoard, gomoku.White, tree.Root)
		if !board.Equals(newBoard) {
			t.Error()
		}
	}
	loc, proba := tree.GetBestMove()
	t.Logf("%d %d: %f\n", loc.X, loc.Y, proba)
	if proba >= 0.5 {
		t.Error()
	}
}

func BenchmarkMonteCarloTree_Search(b *testing.B) {
	policy := RandomPolicy{}
	tree := &MonteCarloTree{new(MonteCarloTreeNode), &policy}
	board := gomoku.NewBoard()
	for i := 0; i < b.N; i++ {
		tree.Search(board, gomoku.Black, tree.Root)
	}
}
