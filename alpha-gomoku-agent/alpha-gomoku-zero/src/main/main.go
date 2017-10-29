package main

import (
	"common"
	"console"
	"flag"
	"mcts"
)

func startConsole() {
	policy := mcts.DNNPolicy{}
	mcts := console.MCTSConsole{Searcher: mcts.MCTS{Policy: &policy}}
	template := console.ConsoleTemplate{}
	template.Start(&mcts)
}

func main() {
	confPath := flag.String("conf", "../../conf/conf.json", "config path")
	flag.Parse()
	common.InitConfig(*confPath)

	startConsole()
}
