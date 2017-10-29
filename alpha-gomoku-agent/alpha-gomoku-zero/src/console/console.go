package console

import (
	"bufio"
	"fmt"
	"os"

	"gomoku"
	"mcts"
)

type IConsole interface {
	Clear()
	Next(gomoku.ChessType) *gomoku.Location
	Reset(*gomoku.Board)
	Play(gomoku.ChessType, *gomoku.Location)
}

type ConsoleTemplate struct {
}

func (this *ConsoleTemplate) Start(console IConsole) {
	command := Command{}
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		cmd, loc := command.Parse(scanner.Text())
		if cmd == nil {
			continue
		}
		switch *cmd {
		case Clear:
			console.Clear()
		case NextBlack:
			next := console.Next(gomoku.Black)
			fmt.Println(command.Format(Put, next))
		case NextWhite:
			next := console.Next(gomoku.White)
			fmt.Println(command.Format(Put, next))
		case Reset:
			board := gomoku.NewBoard()
			for i := 0; i < loc.X && scanner.Scan(); i++ {
				row := scanner.Text()
				for j := 0; j < loc.Y; j++ {
					board.SetRune(&gomoku.Location{i, j}, rune(row[j]))
				}
			}
			console.Reset(board)
		case PlayBlack:
			console.Play(gomoku.Black, loc)
		case PlayWhite:
			console.Play(gomoku.White, loc)
		}
	}
}

type MCTSConsole struct {
	board    *gomoku.Board
	Searcher mcts.MCTS
}

func (this *MCTSConsole) Clear() {
	this.board = nil
}

func (this *MCTSConsole) Next(chessType gomoku.ChessType) *gomoku.Location {
	next, _ := this.Searcher.Next(this.board, chessType)
	return next
}

func (this *MCTSConsole) Reset(board *gomoku.Board) {
	this.board = board
}

func (this *MCTSConsole) Play(gomoku.ChessType, *gomoku.Location) {
	return
}
