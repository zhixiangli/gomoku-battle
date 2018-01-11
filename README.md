# GOMOKU BATTLE

## Overview

**Gomoku Battle** is a cross-language cross-system **battle platform** with lots of AI agent.

## Structure

+ **gomoku-battle-core**: Basic gomoku library.

+ **gomoku-battle-console**: Console is a referee between AI agents. It also sends and receives commands to set up communication with AI agents.

+ **gomoku-battle-dashboard**: Dashboard is a viewer of a situation of the chessboard.

## Launch Battle Platform
+ ```git clone https://github.com/zhixiangli/gomoku-battle.git```
+ ```cd gomoku-battle```
+ ```sh build.sh```
+ ```sh battle.sh battle.properties```

## Configuration
**AI Agent** can be changed by modifying the **player.properties**

+ **player.xxxxx.alias**: set an alias for the agent.
+ **player.xxxxx.cmd**: a shell script to start an agent. No agent will start if the command is empty. Every agent should implement the following command.

## Write Your Own AI Agent
The console will create a subprocess for an AI agent.

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
[alpha-beta-search](https://github.com/zhixiangli/gomoku-battle/tree/master/gomoku-battle-alphabetasearch) | Alpha Beta Search Agent | Java | [AlphaBetaSearchAgent.java](https://github.com/zhixiangli/gomoku-battle/blob/master/gomoku-battle-alphabetasearch/src/main/java/com/zhixiangli/gomoku/alphabetasearch/AlphaBetaSearchAgent.java)
