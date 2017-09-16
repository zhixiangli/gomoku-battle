package main

import (
	"console"
	"mcts"
)

func main() {
	policy := mcts.RandomMonteCarloTreePolicy{Range: 2}
	agent := console.MonteCarloTreeAgent{Searcher: mcts.MonteCarloTreeSearch{Policy: &policy}}
	template := console.AgentTemplate{Agent: &agent}
	template.Run()
}
