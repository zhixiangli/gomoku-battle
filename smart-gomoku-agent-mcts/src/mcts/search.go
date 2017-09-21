package mcts

import (
	"gomoku"
)

type MonteCarloTreeSearch struct {
	Policy MonteCarloTreePolicy
}

func (p *MonteCarloTreeSearch) Next(board *gomoku.Board, chessType gomoku.ChessType) (gomoku.Location, float64) {
	root := &MonteCarloTreeNode{}
	for i := 0; i < 100000; i++ {
		p.Search(board, chessType, root)
	}
	index, proba := p.BestChildNode(root)
	return root.childrenLoc[index], proba
}

func (p *MonteCarloTreeSearch) BestChildNode(root *MonteCarloTreeNode) (index int, proba float64) {
	proba = -1
	for i := range root.childrenNode {
		child := &root.childrenNode[i]
		rate := float64(child.numOfWin) / float64(child.numOfGame)
		if proba < rate {
			proba = rate
			index = i
		}
	}
	return
}

func (p *MonteCarloTreeSearch) Search(board *gomoku.Board, nextType gomoku.ChessType, node *MonteCarloTreeNode) (numOfWin int, numOfGame int, winType gomoku.ChessType) {
	isLeaf := len(node.childrenLoc) == 0
	referee := gomoku.GetReferee()
	chessType := referee.NextType(nextType)
	if isLeaf && node.numOfGame == 0 {
		numOfWin, numOfGame, winType = p.Policy.Simulate(*board.Clone(), nextType)
		node.BackPropagate(numOfWin, numOfGame, chessType, winType)
		return
	}
	if isLeaf { // expand leaf
		node.Expand(board, nextType, p.Policy)
	}
	nextNode, loc := node.Select(p.Policy)
	board.SetChessType(loc, nextType)
	// check if is over
	if referee.IsDraw(board) {
		numOfWin, numOfGame, winType = 0, 1, gomoku.Empty
		nextNode.BackPropagate(numOfWin, numOfGame, nextType, winType)
	} else if referee.IsWin(board, loc) {
		numOfWin, numOfGame, winType = 1, 1, nextType
		nextNode.BackPropagate(numOfWin, numOfGame, nextType, winType)
	} else {
		if isLeaf {
			numOfWin, numOfGame, winType = p.Policy.Simulate(*board.Clone(), chessType)
			nextNode.BackPropagate(numOfWin, numOfGame, nextType, winType)
		} else {
			numOfWin, numOfGame, winType = p.Search(board, chessType, nextNode)
		}
	}
	board.SetChessType(loc, gomoku.Empty)
	node.BackPropagate(numOfWin, numOfGame, chessType, winType)
	return
}
