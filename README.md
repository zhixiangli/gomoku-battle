# GOMOKU BATTLE

## Gomoku
Gomoku, also called Gobang or Five in a Row, is an abstract strategy board game.

Players alternate turns placing a stone of their color on an empty intersection. The winner is the first player to form an unbroken chain of five stones horizontally, vertically, or diagonally.

More details see [wikipedia](https://en.wikipedia.org/wiki/Gomoku).

## Overview

**Gomoku Battle** is a cross-language cross-system **battle platform** with lots of AI agent.

+ **gomoku-battle-core**: Basic Gomoku library.

+ **gomoku-battle-console**: Console is a referee between AI agents. It also sends and receives commands to set up communication with AI agents.

+ **gomoku-battle-dashboard**: Dashboard is a viewer of a situation of the chessboard.

## Launch Battle Platform
```
git clone https://github.com/zhixiangli/gomoku-battle.git
cd gomoku-battle
sh build.sh
sh battle.sh -c battle.properties
```

## Configuration
**AI Agent** can be changed by modifying the **player.properties**

+ **player.xxxxx.alias**: set an alias for the agent.
+ **player.xxxxx.cmd**: a shell script to start an agent. No agent will start if the command is empty. Every agent should implement the following command.

### Example
```
player.black.cmd=java -jar bin/gomoku-battle-alphabetasearch-0.0.1-SNAPSHOT-jar-with-dependencies.jar
player.black.alias=alpha-beta-search
player.white.cmd=java -jar bin/gomoku-battle-alphabetasearch-0.0.1-SNAPSHOT-jar-with-dependencies.jar
player.white.alias=alpha-beta-search
```

## AI Agent API
The console will create a subprocess for an AI agent.

The communication commands between console and AI agent is by **stdin** and **stdout**

The following is the definition of commands.

### Request
Field | Description
------|------------
command | NEXT\_BLACK (next color is black) or NEXT\_WHITE (next color is white)
rows | the number of rows in a chessboard
columns | the number of columns in a chessboard
chessboard | SGF

### Sample Request
{"command":"NEXT_BLACK","rows":15,"columns":15,"chessboard":"B[96];W[a5];B[a4];W[95]"}

### Response
return the position to make a move

### Sample Response
{"rowIndex":3,"columnIndex":10}

## AI Agent Example

AI | Description | Language | Command Processor
---|---|---|---
[alpha-beta-search](https://github.com/zhixiangli/gomoku-battle/tree/master/gomoku-battle-alphabetasearch) | Alpha Beta Search Agent | Java | [AlphaBetaSearchAgent.java](https://github.com/zhixiangli/gomoku-battle/blob/master/gomoku-battle-alphabetasearch/src/main/java/com/zhixiangli/gomoku/alphabetasearch/AlphaBetaSearchAgent.java)

## Evaluation
```
git clone https://github.com/zhixiangli/gomoku-battle.git
cd gomoku-battle
sh build.sh
sh battle.sh -c battle.properties -d
```