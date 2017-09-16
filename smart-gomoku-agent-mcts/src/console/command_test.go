package console

import (
	"gomoku"
	"testing"
)

func TestCommand_Format(t *testing.T) {
	command := Command{}
	if "PLAY_BLACK 2 32" != command.Format(PlayBlack, gomoku.Location{2, 32}) {
		t.Error()
	}
}

func TestCommand_Parse(t *testing.T) {
	command := Command{}
	cmd, loc := command.Parse("PLAY_BLACK 2 32")
	if (*cmd != PlayBlack || *loc != gomoku.Location{2, 32}) {
		t.Error()
	}
	cmd, loc = command.Parse("NEXT_BLACK")
	if *cmd != NextBlack || nil != loc {
		t.Error()
	}
}
