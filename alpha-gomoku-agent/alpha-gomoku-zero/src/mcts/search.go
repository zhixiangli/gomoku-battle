package mcts

import (
	"common"
	"gomoku"
)

type MCTS struct {
	Policy MCTSPolicy
}

func (this *MCTS) Next(board *gomoku.Board, chessType gomoku.ChessType) (gomoku.Location, float64) {
	root := new(MonteCarloTreeNode)
	for i := 0; i < common.Conf.SearchCount; i++ {
		this.Search(board, chessType, root)
	}
	index, proba := this.BestChildNode(root)
	return root.childrenLoc[index], proba
}

func (this *MCTS) BestChildNode(root *MonteCarloTreeNode) (index int, proba float64) {
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

func (this *MCTS) Search(board *gomoku.Board, nextType gomoku.ChessType, node *MonteCarloTreeNode) (numOfWin int, numOfGame int, winType gomoku.ChessType) {
	isLeaf := len(node.childrenLoc) == 0
	chessType := gomoku.Referee.NextType(nextType)
	if isLeaf && node.numOfGame == 0 {
		numOfWin, numOfGame, winType = this.Policy.Simulate(*board.Clone(), nextType)
		node.BackPropagate(numOfWin, numOfGame, chessType, winType)
		return
	}
	if isLeaf { // expand leaf
		node.Expand(board, nextType, this.Policy)
	}
	nextNode, loc := node.Select(this.Policy)
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
			numOfWin, numOfGame, winType = this.Policy.Simulate(*board.Clone(), chessType)
			nextNode.BackPropagate(numOfWin, numOfGame, nextType, winType)
		} else {
			numOfWin, numOfGame, winType = this.Search(board, chessType, nextNode)
		}
	}
	board.SetChessType(loc, gomoku.Empty)
	node.BackPropagate(numOfWin, numOfGame, chessType, winType)
	return
}
