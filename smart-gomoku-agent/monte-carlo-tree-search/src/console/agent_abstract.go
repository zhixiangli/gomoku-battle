package console

import (
	"bufio"
	"fmt"
	"os"

	"gomoku"
)

type IConsoleAgent interface {
	Clear()
	Next(gomoku.ChessType) gomoku.Location
	Reset(*gomoku.Board)
	Play(gomoku.ChessType, gomoku.Location)
}

type AgentTemplate struct {
	Agent IConsoleAgent
}

func (p *AgentTemplate) Run() {
	command := Command{}
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		cmd, loc := command.Parse(scanner.Text())
		if cmd == nil {
			continue
		}
		switch *cmd {
		case Clear:
			p.Agent.Clear()
		case NextBlack:
			next := p.Agent.Next(gomoku.Black)
			fmt.Println(command.Format(Put, next))
		case NextWhite:
			next := p.Agent.Next(gomoku.White)
			fmt.Println(command.Format(Put, next))
		case Reset:
			board := gomoku.NewBoard(loc.X, loc.Y)
			for i := 0; i < loc.X && scanner.Scan(); i++ {
				row := scanner.Text()
				for j := 0; j < loc.Y; j++ {
					board.SetRune(gomoku.Location{i, j}, rune(row[j]))
				}
			}
			p.Agent.Reset(board)
		case PlayBlack:
			p.Agent.Play(gomoku.Black, *loc)
		case PlayWhite:
			p.Agent.Play(gomoku.White, *loc)
		}
	}
}
