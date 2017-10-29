package mcts

import (
	"math"

	"common"
	"gomoku"
)

type MonteCarloTreeNode struct {
	numOfWin     int
	numOfGame    int
	childrenNode []MonteCarloTreeNode
	childrenLoc  []gomoku.Location
	value        float64
}

func (p *MonteCarloTreeNode) Select(policy MCTSPolicy) (selected *MonteCarloTreeNode, loc *gomoku.Location) {
	bestValue := -math.MaxFloat64
	for i := range p.childrenNode {
		currValue := policy.Evaluate(p, i)
		if bestValue < currValue {
			bestValue = currValue
			selected = &p.childrenNode[i]
			loc = &p.childrenLoc[i]
		}
	}
	return
}

func (p *MonteCarloTreeNode) Expand(board *gomoku.Board, chessType gomoku.ChessType, policy MCTSPolicy) {
	p.childrenLoc = policy.Around(board, common.Conf.AroundRange)
	p.childrenNode = make([]MonteCarloTreeNode, len(p.childrenLoc))
	for i := range p.childrenLoc {
		p.childrenNode[i] = MonteCarloTreeNode{}
	}
}

func (p *MonteCarloTreeNode) BackPropagate(numOfWin int, numOfGame int, selfType gomoku.ChessType, winType gomoku.ChessType) {
	p.numOfGame += numOfGame
	if selfType == winType {
		p.numOfWin += numOfWin
	}
}
