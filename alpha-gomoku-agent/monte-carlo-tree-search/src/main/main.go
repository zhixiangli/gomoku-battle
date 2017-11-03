package main

import (
	"flag"

	"common"
	"console"
	"mcts"
)

func startConsole() {
	mcts := console.MCTSConsole{Tree: mcts.MonteCarloTree{new(mcts.MonteCarloTreeNode), &mcts.RandomPolicy{}}}
	console.Start(&mcts)
}

func init() {
	confPath := flag.String("conf", "/Users/lizx/git/alpha-gomoku/alpha-gomoku-agent/monte-carlo-tree-search/conf/config.json", "config path")
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
