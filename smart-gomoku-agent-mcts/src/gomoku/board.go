package gomoku

type ChessType int

const (
	Empty ChessType = iota
	Black
	White
)

type Location struct {
	X int
	Y int
}

func (p *Location) HashCode() int {
	return p.X*19 + p.Y
}

type Board struct {
	Row    int
	Column int

	board         [][]ChessType
	nonEmptyCount int
}

func NewBoard(row int, column int) *Board {
	newBoard := Board{Row: row, Column: column}
	newBoard.board = make([][]ChessType, row)
	for i := 0; i < row; i++ {
		newBoard.board[i] = make([]ChessType, column)
		for j := 0; j < column; j++ {
			newBoard.board[i][j] = Empty
		}
	}
	return &newBoard
}

func (p *Board) updateEmptyCount(loc Location, newType ChessType) {
	oldType := p.board[loc.X][loc.Y]
	if oldType == Empty && newType != Empty {
		p.nonEmptyCount++
	} else if oldType != Empty && newType == Empty {
		p.nonEmptyCount--
	}
}

func (p *Board) SetRune(loc Location, ch rune) {
	newType := toChessType(ch)
	p.updateEmptyCount(loc, newType)
	p.board[loc.X][loc.Y] = newType
}

func (p *Board) SetChessType(loc Location, chessType ChessType) {
	p.updateEmptyCount(loc, chessType)
	p.board[loc.X][loc.Y] = chessType
}

func (p *Board) GetByLocation(loc Location) ChessType {
	return p.board[loc.X][loc.Y]
}

func (p *Board) Get(row int, column int) ChessType {
	return p.board[row][column]
}

func (p *Board) Clone() (newBoard *Board) {
	newBoard = NewBoard(p.Row, p.Column)
	for i := 0; i < p.Row; i++ {
		for j := 0; j < p.Column; j++ {
			newBoard.board[i][j] = p.board[i][j]
		}
	}
	return
}

func (p *Board) Equals(other *Board) bool {
	if p.Row != other.Row || p.Column != other.Column {
		return false
	}
	for i := 0; i < p.Row; i++ {
		for j := 0; j < p.Column; j++ {
			if p.board[i][j] != other.board[i][j] {
				return false
			}
		}
	}
	return true
}

func toChessType(ch rune) ChessType {
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
