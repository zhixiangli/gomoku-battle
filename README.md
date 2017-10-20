# Smart Gomoku

## Overview

**Smart Gomoku** is a cross-language cross-system **battle platform** with lots of AI agent.

A number of agent algorithms have been implemented, such as **Alpha-Beta Search** (Alpha-Beta Pruning), **MCTS** (Monte Carlo Tree Search), **DNN** (Deep Neural Network), and so on.

## Structure

+ **smart-gomoku-core**: Basic gomoku library.

+ **smart-gomoku-console**: Console is a referee between AI agents. It also sends and receives commands to set up communication with AI agents.

+ **smart-gomoku-dashboard**: Dashboard is a viewer of a situation of chessboard.

+ **smart-gomoku-agent**: Implemented AI agents in arbitrary algorithms in any language.

## Launch Battle Platform
// TODO

## Write Your Own AI Agent
The console will create a subprocess for a AI agent.

The communication commands between console and AI agent is by **stdin** and **stdout**

The following is the definition of commands.

Command | Data Flow | Description
--------|-----------|------------
CLEAR | console -> agent | ask the agent to clear the chessboard
NEXT_WHITE | console -> agent | ask the agent to calculate the next move where the white go
NEXT_BLACK | console -> agent | ask the agent to calculate the next move where the black go
RESET ROW COLUMN | console -> agent | ask the agent to reset the chessboard with \$ROW row and \$COLUMN column. There will be \$ROW line following, and each line contains \$COLUMN characters (white: 'W', black: 'B', empty: '.'). **This command is always sent before NEXT_XXXXX command.**
PLAY_BLACK ROW COLUMN | console -> agent | ask agent to put black piece on the position(\$ROW, \$COLUMN) of the chessboard
PLAY_WHITE ROW COLUMN | console -> agent | ask agent to put white piece on the position(\$ROW, \$COLUMN) of the chessboard
PUT ROW COLUMN | agent -> console | ask console to put piece on the position(\$ROW, \$COLUMN) of the chessboard

## AI Agent Example

AI | Description | Language | Command Processor
---|---|---|---
[smart-gomoku-agent-alphabetasearch](https://github.com/zhixiangli/smart-gomoku/tree/master/smart-gomoku-agent-alphabetasearch) | Alpha-Beta Search Agent | Java | [AlphaBetaSearchAgent.java](https://github.com/zhixiangli/smart-gomoku/blob/master/smart-gomoku-agent-alphabetasearch/src/main/java/com/zhixiangli/gomoku/agent/alphabetasearch/AlphaBetaSearchAgent.java)
[smart-gomoku-agent-mcts](https://github.com/zhixiangli/smart-gomoku/tree/master/smart-gomoku-agent-mcts) | Monte Carlo Tree Search Agent | Golang | [agent_abstract.go](https://github.com/zhixiangli/smart-gomoku/blob/master/smart-gomoku-agent-mcts/src/console/agent_abstract.go)
[smart-gomoku-agent-dnn](https://github.com/zhixiangli/smart-gomoku/tree/master/smart-gomoku-agent-dnn) | Deep Neural Network Agent | Python | [abstract_agent.py](https://github.com/zhixiangli/smart-gomoku/blob/master/smart-gomoku-agent-dnn/src/abstract_agent.py)
[gomoku-ai](https://github.com/ghnjk/gomoku-ai) | | C++ | 
