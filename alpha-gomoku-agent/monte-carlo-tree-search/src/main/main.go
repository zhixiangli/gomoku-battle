package main

import (
	"common"
	"console"
	"flag"
	"mcts"
)

func startConsole() {
	policy := mcts.RandomPolicy{}
	mcts := console.MCTSConsole{Searcher: mcts.MCTS{Policy: &policy}}
	template := console.ConsoleTemplate{}
	template.Start(&mcts)
}

func init() {
	confPath := flag.String("conf", "../../conf/config.json", "config path")
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
