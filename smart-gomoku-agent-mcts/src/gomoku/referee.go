package gomoku

import "sync"

const (
	ConsecutiveNum = 5
)

type referee struct {
	direction [][]int
}

var instance *referee
var once sync.Once

func GetReferee() *referee {
	once.Do(func() {
		instance = &referee{[][]int{{1, 0}, {0, 1}, {1, 1}, {-1, 1}}}
	})
	return instance
}

func (p *referee) NextType(chessType ChessType) ChessType {
	if chessType == Black {
		return White
	} else if chessType == White {
		return Black
	}
	return Empty
}

func (p *referee) IsDraw(board *Board) bool {
	return board.nonEmptyCount == board.Row*board.Column
}

func (p *referee) IsWin(board *Board, location Location) bool {
	for i := range p.direction {
		if p.consecutiveCount(board, location, p.direction[i]) >= ConsecutiveNum {
			return true
		}
	}
	return false
}

func (p *referee) IsInBoard(board *Board, row int, column int) bool {
	return 0 <= row && 0 <= column && row < board.Row && column < board.Column
}

func (p *referee) consecutiveCount(board *Board, loc Location, direction []int) int {
	count := 1
	for x, y := loc.X+direction[0], loc.Y+direction[1]; p.IsInBoard(board, x, y) && board.GetByLocation(loc) == board.Get(x, y); {
		x += direction[0]
		y += direction[1]
		count++
	}
	for x, y := loc.X-direction[0], loc.Y-direction[1]; p.IsInBoard(board, x, y) && board.GetByLocation(loc) == board.Get(x, y); {
		x -= direction[0]
		y -= direction[1]
		count++
	}
	return count
}
