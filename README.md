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

### Request
Field | Description
------|------------
command | NEXT\_BLACK or NEXT\_WHITE
rows | the number of rows in chessboard
columns | the number of columns in chessboard
chessboard | similar to SGF

#### Example
{"command":"NEXT_BLACK","rows":15,"columns":15,"chessboard":"B[96];W[a5];B[a4];W[95]"}

### Response
return the position to make a move

#### Example
{"rowIndex":3,"columnIndex":10}

## AI Agent Example

AI | Description | Language | Command Processor
---|---|---|---
[alpha-beta-search](https://github.com/zhixiangli/gomoku-battle/tree/master/gomoku-battle-alphabetasearch) | Alpha Beta Search Agent | Java | [AlphaBetaSearchAgent.java](https://github.com/zhixiangli/gomoku-battle/blob/master/gomoku-battle-alphabetasearch/src/main/java/com/zhixiangli/gomoku/alphabetasearch/AlphaBetaSearchAgent.java)
