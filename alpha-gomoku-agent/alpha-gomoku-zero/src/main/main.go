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

func init() {
	confPath := flag.String("conf", "../../conf/alpha-gomoku-zero.json", "config path")
	flag.Parse()
	common.InitConfig(*confPath)
	common.InitLogger(common.Conf.LogPath)
}

func del() {
	common.DelLogger()
}

func main() {
	defer del()

	startConsole()
}
