package console

import (
	"bufio"
	"fmt"
	"log"
	"os"
	"sync"
	"time"

	"common"
	"gomoku"
	"mcts"
)

type IConsole interface {
	Init()
	Clear()
	Next(gomoku.ChessType) *gomoku.Location
	Reset(*gomoku.Board)
	Play(gomoku.ChessType, *gomoku.Location)
}

type MCTSConsole struct {
	Locker        sync.Mutex
	Tree          mcts.MonteCarloTree
	Board         *gomoku.Board
	ResetBoard    *gomoku.Board
	NextChessType gomoku.ChessType
}

func (this *MCTSConsole) Init() {
	this.Clear()
	go func() {
		for {
			this.Locker.Lock()
			for i := 0; i < common.Conf.SearchStep; i++ {
				this.Tree.Search(this.Board, this.NextChessType, this.Tree.Root)
			}
			this.Locker.Unlock()
		}
	}()
}

func (this *MCTSConsole) Clear() {
	this.Locker.Lock()
	defer this.Locker.Unlock()

	this.Tree.Root = new(mcts.MonteCarloTreeNode)
	this.Board = gomoku.NewBoard()
	this.ResetBoard = nil
	this.NextChessType = gomoku.Black
}

func (this *MCTSConsole) Next(chessType gomoku.ChessType) *gomoku.Location {
	this.Locker.Lock()
	this.NextChessType = chessType
	this.Board = this.ResetBoard
	this.Locker.Unlock()

	time.Sleep(time.Second * time.Duration(common.Conf.SearchSecond))

	this.Locker.Lock()
	loc, bestProba := this.Tree.GetBestMove()
	this.Locker.Unlock()
	log.Printf("MCTS_PROBA|%s|%c|%f", this.Board.ToString(), gomoku.ChessTypeToRune(chessType), bestProba)

	this.Play(chessType, loc)

	return loc
}

func (this *MCTSConsole) Reset(board *gomoku.Board) {
	this.ResetBoard = board

	this.Locker.Lock()
	defer this.Locker.Unlock()

	if !this.Board.Equals(board) {
		log.Printf("boards are not equal when reset")
		log.Printf("old: %s", this.Board.ToString())
		log.Printf("new: %s", board.ToString())
	}
}

func (this *MCTSConsole) Play(chessType gomoku.ChessType, loc *gomoku.Location) {
	this.Locker.Lock()
	defer this.Locker.Unlock()

	this.Board.SetChessType(loc, chessType)
	this.NextChessType = gomoku.Referee.NextType(chessType)
	this.Tree.NextRoot(loc)
}

func Start(console IConsole) {
	console.Init()
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
