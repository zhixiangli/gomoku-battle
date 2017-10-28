package mcts

import (
	"gomoku"
)

type MonteCarloTreePolicy interface {
	Evaluate(root *MonteCarloTreeNode, childIndex int) float64
	Around(*gomoku.Board) []gomoku.Location
	Simulate(gomoku.Board, gomoku.ChessType) (numOfWin int, numOfGame int, winner gomoku.ChessType)
}
