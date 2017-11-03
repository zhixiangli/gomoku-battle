package mcts

import (
	"math"

	"common"
	"gomoku"
	"log"
)

type MonteCarloTreeNode struct {
	numOfWin       int
	numOfGame      int
	estimatedValue float64
	childrenNode   []*MonteCarloTreeNode
	childrenLoc    []*gomoku.Location
}

func (p *MonteCarloTreeNode) Select(policy MCTSPolicy) (selected *MonteCarloTreeNode, loc *gomoku.Location) {
	bestValue := -math.MaxFloat64
	for i := range p.childrenNode {
		currValue := policy.Evaluate(p, i)
		if bestValue < currValue {
			bestValue = currValue
			selected = p.childrenNode[i]
			loc = p.childrenLoc[i]
		}
	}
	return
}

func (p *MonteCarloTreeNode) Expand(board *gomoku.Board, chessType gomoku.ChessType, policy MCTSPolicy) {
	p.childrenLoc = policy.Around(board, common.Conf.AroundRange)
	p.childrenNode = make([]*MonteCarloTreeNode, len(p.childrenLoc))
	for i := range p.childrenLoc {
		p.childrenNode[i] = policy.NewNode(board)
	}
}

func (p *MonteCarloTreeNode) BackPropagate(numOfWin int, numOfGame int, selfType gomoku.ChessType, winType gomoku.ChessType) {
	p.numOfGame += numOfGame
	if selfType == winType {
		p.numOfWin += numOfWin
	}
}

type MonteCarloTree struct {
	Root         *MonteCarloTreeNode
	SearchPolicy MCTSPolicy
}

func (this *MonteCarloTree) GetBestMove() (*gomoku.Location, float64) {
	index, proba := 0, 0.0
	for i := range this.Root.childrenNode {
		child := this.Root.childrenNode[i]
		rate := float64(child.numOfWin) / float64(child.numOfGame)
		if proba < rate {
			proba = rate
			index = i
		}
	}
	bestChild := this.Root.childrenNode[index]
	loc := this.Root.childrenLoc[index]
	log.Printf("root state: %d/%d=%f", this.Root.numOfWin, this.Root.numOfGame, float64(this.Root.numOfWin)/float64(this.Root.numOfGame))
	log.Printf("best child: %d/%d=%f", bestChild.numOfWin, bestChild.numOfGame, proba)
	return loc, proba
}

func (this *MonteCarloTree) NextRoot(loc *gomoku.Location) {
	for i := range this.Root.childrenLoc {
		if *this.Root.childrenLoc[i] == *loc {
			this.Root = this.Root.childrenNode[i]
			return
		}
	}
	this.Root = new(MonteCarloTreeNode)
}

func (this *MonteCarloTree) Search(board *gomoku.Board, nextType gomoku.ChessType, node *MonteCarloTreeNode) (numOfWin int, numOfGame int, winType gomoku.ChessType) {
	isLeaf := len(node.childrenLoc) == 0
	chessType := gomoku.Referee.NextType(nextType)
	if isLeaf && node.numOfGame == 0 {
		numOfWin, numOfGame, winType = this.SearchPolicy.Simulate(*board.Clone(), nextType)
		node.BackPropagate(numOfWin, numOfGame, chessType, winType)
		return
	}
	if isLeaf { // expand leaf
		node.Expand(board, nextType, this.SearchPolicy)
	}
	nextNode, loc := node.Select(this.SearchPolicy)
	board.SetChessType(loc, nextType)
	// check if is over
	if gomoku.Referee.IsDraw(board) {
		numOfWin, numOfGame, winType = 0, 1, gomoku.Empty
		nextNode.BackPropagate(numOfWin, numOfGame, nextType, winType)
	} else if gomoku.Referee.IsWin(board, loc) {
		numOfWin, numOfGame, winType = 1, 1, nextType
		nextNode.BackPropagate(numOfWin, numOfGame, nextType, winType)
	} else {
		if isLeaf {
			numOfWin, numOfGame, winType = this.SearchPolicy.Simulate(*board.Clone(), chessType)
			nextNode.BackPropagate(numOfWin, numOfGame, nextType, winType)
		} else {
			numOfWin, numOfGame, winType = this.Search(board, chessType, nextNode)
		}
	}
	board.SetChessType(loc, gomoku.Empty)
	node.BackPropagate(numOfWin, numOfGame, chessType, winType)
	return
}
