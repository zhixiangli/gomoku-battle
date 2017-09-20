package main

import (
	"bufio"
	"console"
	"flag"
	"fmt"
	"os"

	"gomoku"
	"mcts"
	"strings"
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
		input_str := scanner.Text()
		input := strings.Split(input_str, "\t")
		board_str := input[0]
		if len(board_str) != row*column {
			continue
		}
		chessType := gomoku.ToChessType(rune(input[1][0]))
		fmt.Printf("%s\t%f\n", input_str, evaluate(parseBoard(board_str, row, column), chessType))
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

func evaluate(board *gomoku.Board, chessType gomoku.ChessType) float64 {
	policy := mcts.RandomMonteCarloTreePolicy{Range: 2}
	searcher := &mcts.MonteCarloTreeSearch{&policy}
	_, proba := searcher.Next(board, chessType)
	return proba
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
