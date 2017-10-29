package mcts

import (
	"math"
	"math/rand"

	"common"
	"gomoku"
)

type MCTSPolicy interface {
	Evaluate(root *MonteCarloTreeNode, childIndex int) float64
	Around(*gomoku.Board, int) []*gomoku.Location
	Simulate(gomoku.Board, gomoku.ChessType) (numOfWin int, numOfGame int, winner gomoku.ChessType)
	NewNode(*gomoku.Board) *MonteCarloTreeNode
}

type DNNPolicy struct {
}

func (this *DNNPolicy) Simulate(board gomoku.Board, chessType gomoku.ChessType) (numOfWin int, numOfGame int, winner gomoku.ChessType) {
	locations := this.Around(&board, common.Conf.AroundRange)
	referee := gomoku.Referee
	for len(locations) > 0 {
		// choose a location
		nextIndex := rand.Intn(len(locations))
		loc := locations[nextIndex]
		// remove the chosen location
		locations = append(locations[:nextIndex], locations[nextIndex+1:]...)
		// make a move
		if (&board).GetByLocation(loc) != gomoku.Empty {
			continue
		}
		(&board).SetChessType(loc, chessType)
		// game over
		if referee.IsWin(&board, loc) {
			return 1, 1, chessType
		}
		// game on
		chessType = referee.NextType(chessType)
		locations = append(locations, this.AroundLocation(&board, loc, common.Conf.AroundRange)...)
	}
	return 0, 1, gomoku.Empty
}

func (this *DNNPolicy) Evaluate(root *MonteCarloTreeNode, childIndex int) float64 {
	child := root.childrenNode[childIndex]
	exploitation := float64(child.numOfWin) / float64(1+child.numOfGame)
	exploration := math.Sqrt(math.Log2(float64(1+root.numOfGame)) * 2 / float64(1+child.numOfGame))
	return exploitation + exploration + child.estimatedValue + rand.Float64()/1e6
}

func (this *DNNPolicy) AroundLocation(board *gomoku.Board, loc *gomoku.Location, arroundRange int) []*gomoku.Location {
	locs := make([]*gomoku.Location, 0, 4*arroundRange*(arroundRange+1))
	rowStart := loc.X - arroundRange
	if rowStart < 0 {
		rowStart = 0
	}
	for i := rowStart; i < common.Conf.Row && i <= loc.X+arroundRange; i++ {
		columnStart := loc.Y - arroundRange
		if columnStart < 0 {
			columnStart = 0
		}
		for j := columnStart; j < common.Conf.Column && j <= loc.Y+arroundRange; j++ {
			if board.Get(i, j) == gomoku.Empty {
				locs = append(locs, &gomoku.Location{X: i, Y: j})
			}
		}
	}
	return locs
}

func (this *DNNPolicy) Around(board *gomoku.Board, arroundRange int) []*gomoku.Location {
	visited := make(map[int]bool)
	locs := make([]*gomoku.Location, 0, common.Conf.Row*common.Conf.Column-1)
	for i := 0; i < common.Conf.Row; i++ {
		for j := 0; j < common.Conf.Column; j++ {
			if board.Get(i, j) == gomoku.Empty {
				continue
			}
			around := this.AroundLocation(board, &gomoku.Location{X: i, Y: j}, arroundRange)
			for k := range around {
				h := around[k].HashCode()
				if _, found := visited[h]; !found {
					locs = append(locs, around[k])
					visited[h] = true
				}
			}
		}
	}
	if len(locs) == 0 {
		locs = append(locs, &gomoku.Location{X: common.Conf.Row / 2, Y: common.Conf.Column / 2})
	}
	return locs
}

func (this *DNNPolicy) NewNode(*gomoku.Board) *MonteCarloTreeNode {
	return &MonteCarloTreeNode{estimatedValue: 0}
}
