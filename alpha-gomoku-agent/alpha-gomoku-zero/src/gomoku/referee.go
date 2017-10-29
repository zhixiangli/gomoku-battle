package gomoku

import "common"

type referee struct {
}

var (
	Direction = [][]int{{1, 0}, {0, 1}, {1, 1}, {-1, 1}}
	Referee   = &referee{}
)

func (this *referee) NextType(chessType ChessType) ChessType {
	if chessType == Black {
		return White
	} else if chessType == White {
		return Black
	}
	return Empty
}

func (this *referee) IsDraw(board *Board) bool {
	return board.nonEmptyCount == common.Conf.Column*common.Conf.Row
}

func (this *referee) IsWin(board *Board, location *Location) bool {
	for i := range Direction {
		if this.consecutiveCount(board, location, Direction[i]) >= common.Conf.ConsecutiveNum {
			return true
		}
	}
	return false
}

func (this *referee) IsInBoard(board *Board, row int, column int) bool {
	return 0 <= row && 0 <= column && row < common.Conf.Row && column < common.Conf.Column
}

func (this *referee) consecutiveCount(board *Board, loc *Location, direction []int) int {
	count := 1
	chessType := board.GetByLocation(loc)
	for x, y := loc.X+direction[0], loc.Y+direction[1]; this.IsInBoard(board, x, y) && chessType == board.Get(x, y); {
		x += direction[0]
		y += direction[1]
		count++
	}
	for x, y := loc.X-direction[0], loc.Y-direction[1]; this.IsInBoard(board, x, y) && chessType == board.Get(x, y); {
		x -= direction[0]
		y -= direction[1]
		count++
	}
	return count
}
