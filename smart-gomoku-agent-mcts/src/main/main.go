package main

import (
	"bufio"
	"console"
	"flag"
	"fmt"
	"os"

	"gomoku"
	"mcts"
)

func bootstrapAgent() {
	policy := mcts.RandomMonteCarloTreePolicy{Range: 2}
	agent := console.MonteCarloTreeAgent{Searcher: mcts.MonteCarloTreeSearch{Policy: &policy}}
	template := console.AgentTemplate{Agent: &agent}
	template.Run()
}

func generateSample(row int, column int) {
	scanner := bufio.NewScanner(os.Stdin)
	for scanner.Scan() {
		str := scanner.Text()
		if len(str) != row*column {
			continue
		}
		board := parseBoard(str, row, column)
		chessType := nextChessType(calcSteps(board))
		fmt.Printf("%s\t%d\t%f\n", str, chessType, evaluate(board, chessType))
	}
}

func main() {
	mode := flag.Int("mode", 0, "0: bootstrap agent; 1: generate sample")
	row := flag.Int("row", 15, "the number of rows of chessboard")
	column := flag.Int("column", 15, "the number of columns of chessboard")
	flag.Parse()

	if *mode == 0 {
		bootstrapAgent()
	} else {
		generateSample(*row, *column)
	}
}

func parseBoard(str string, row int, column int) (board *gomoku.Board) {
	board = gomoku.NewBoard(row, column)
	for i := 0; i < row; i++ {
		for j := 0; j < column; j++ {
			board.SetRune(gomoku.Location{i, j}, rune(str[i*row+j]))
		}
	}
	return
}

func calcSteps(board *gomoku.Board) (black int, white int) {
	black, white = 0, 0
	for i := 0; i < board.Row; i++ {
		for j := 0; j < board.Column; j++ {
			switch board.Get(i, j) {
			case gomoku.Black:
				black++
			case gomoku.White:
				white++
			}
		}
	}
	return
}

func nextChessType(black int, white int) gomoku.ChessType {
	if black == white {
		return gomoku.Black
	} else if black == white+1 {
		return gomoku.White
	} else {
		return gomoku.Empty
	}
}

func evaluate(board *gomoku.Board, chessType gomoku.ChessType) float64 {
	policy := mcts.RandomMonteCarloTreePolicy{Range: 2}
	searcher := &mcts.MonteCarloTreeSearch{&policy}
	_, proba := searcher.Next(board, chessType)
	return proba
}
