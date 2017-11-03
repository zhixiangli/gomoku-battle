package gomoku

import (
	"bytes"
	"common"
)

// chess type
type ChessType int

const (
	Empty ChessType = iota
	Black
	White
)

func RuneToChessType(ch rune) ChessType {
	switch ch {
	case 'B':
		return Black
	case 'W':
		return White
	case '.':
		return Empty
	default:
		return Empty
	}
}

func ChessTypeToRune(t ChessType) rune {
	switch t {
	case Black:
		return 'B'
	case White:
		return 'W'
	default:
		return '.'
	}
}

// location
type Location struct {
	X int
	Y int
}

func (this *Location) HashCode() int {
	return this.X*19 + this.Y
}

// board
type Board struct {
	board         [][]ChessType
	nonEmptyCount int
}

func NewBoard() *Board {
	newBoard := Board{}
	newBoard.board = make([][]ChessType, common.Conf.Row)
	for i := 0; i < common.Conf.Row; i++ {
		newBoard.board[i] = make([]ChessType, common.Conf.Column)
		for j := 0; j < common.Conf.Column; j++ {
			newBoard.board[i][j] = Empty
		}
	}
	return &newBoard
}

func (this *Board) SetRune(loc *Location, ch rune) {
	newType := RuneToChessType(ch)
	this.UpdateEmptyCount(loc, newType)
	this.board[loc.X][loc.Y] = newType
}

func (this *Board) SetChessType(loc *Location, chessType ChessType) {
	this.UpdateEmptyCount(loc, chessType)
	this.board[loc.X][loc.Y] = chessType
}

func (this *Board) GetByLocation(loc *Location) ChessType {
	return this.board[loc.X][loc.Y]
}

func (this *Board) Get(row int, column int) ChessType {
	return this.board[row][column]
}

func (this *Board) Clone() (newBoard *Board) {
	newBoard = NewBoard()
	for i := 0; i < common.Conf.Row; i++ {
		for j := 0; j < common.Conf.Column; j++ {
			newBoard.board[i][j] = this.board[i][j]
		}
	}
	return
}

func (this *Board) Equals(other *Board) bool {
	for i := 0; i < common.Conf.Row; i++ {
		for j := 0; j < common.Conf.Column; j++ {
			if this.board[i][j] != other.board[i][j] {
				return false
			}
		}
	}
	return true
}

func (this *Board) ToString() string {
	var buffer bytes.Buffer
	for i := 0; i < common.Conf.Row; i++ {
		for j := 0; j < common.Conf.Column; j++ {
			buffer.WriteRune(ChessTypeToRune(this.board[i][j]))
		}
	}
	return buffer.String()
}

func (this *Board) UpdateEmptyCount(loc *Location, newType ChessType) {
	oldType := this.board[loc.X][loc.Y]
	if oldType == Empty && newType != Empty {
		this.nonEmptyCount++
	} else if oldType != Empty && newType == Empty {
		this.nonEmptyCount--
	}
}
