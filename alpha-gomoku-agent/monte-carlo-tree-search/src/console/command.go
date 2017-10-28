package console

import (
	"fmt"
	"gomoku"
	"strings"
)

const (
	Clear     = "CLEAR"
	NextBlack = "NEXT_BLACK"
	NextWhite = "NEXT_WHITE"
	Reset     = "RESET"
	PlayBlack = "PLAY_BLACK"
	PlayWhite = "PLAY_WHITE"
	Put       = "PUT"
)

type Command struct {
}

func (p *Command) Parse(line string) (*string, *gomoku.Location) {
	r := strings.NewReader(line)
	var row, column int
	var cmd string
	n, _ := fmt.Fscanf(r, "%s%d%d", &cmd, &row, &column)
	if n == 1 {
		switch cmd {
		case Clear, NextBlack, NextWhite:
			return &cmd, nil
		}
	} else if n == 3 {
		switch cmd {
		case Reset, PlayWhite, PlayBlack, Put:
			return &cmd, &gomoku.Location{row, column}
		}
	}
	return nil, nil
}

func (p *Command) Format(cmd string, loc gomoku.Location) string {
	return fmt.Sprintf("%s %d %d", cmd, loc.X, loc.Y)
}
