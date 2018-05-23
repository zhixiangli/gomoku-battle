# GOMOKU BATTLE

## Overview
**Gomoku**, also called Gobang or Five in a Row, is an abstract strategy board game.

Players alternate turns placing a stone of their color on an empty intersection. The winner is the first player to form an unbroken chain of five stones horizontally, vertically, or diagonally.

More details see [wikipedia](https://en.wikipedia.org/wiki/Gomoku).

**Gomoku Battle** is a cross-language cross-system **battle platform** with lots of AI agent.

+ **gomoku-battle-core**: Basic Gomoku library.

+ **gomoku-battle-console**: A referee between AI agents. It also sends and receives commands to set up communication with AI agents.

+ **gomoku-battle-dashboard**: Dashboard is a viewer of a situation of the chessboard.

## Launch Battle Platform
```
git clone https://github.com/zhixiangli/gomoku-battle.git
cd gomoku-battle
sh build.sh
sh battle.sh -c battle.properties
```

## An Example Game between Alpha-Beta Agents
`
{"command":"NEXT_WHITE","rows":15,"columns":15,"chessboard":"B[76];W[65];B[55];W[66];B[67];W[58];B[74];W[75];B[57];W[64];B[53];W[54];B[63];W[85];B[95];W[86];B[87];W[97];B[a8];W[43];B[47];W[77];B[46];W[32];B[21];W[37];B[48];W[45];B[4a];W[49];B[35];W[24];B[79];W[68];B[44];W[62];B[26];W[17];B[52];W[99];B[88];W[98];B[9a];W[89];B[41];W[30];B[11];W[31];B[33];W[22];B[16];W[36];B[18];W[7a];B[6b];W[5b];B[29];W[1a];B[3a];W[a7];B[b6];W[5c];B[5a];W[2a];B[4b];W[07];B[3c];W[69];B[3b];W[8b];B[9c];W[3d];B[94];W[83];B[84];W[b7];B[38];W[39];B[c7];W[a4];B[a5];W[d8];B[96];W[b4];B[8d];W[ab];B[d6];W[c6];B[28];W[08];B[93];W[92];B[9e];W[7c];B[2d];W[1e];B[25];W[27];B[9b];W[9d];B[1d];W[2c];B[4e];W[09];B[4c];W[4d];B[a9];W[b8];B[0a];W[a2];B[b2];W[c9];B[a3];W[ba];B[e7];W[bb];B[b9];W[eb];B[da];W[cb];B[db];W[82];B[72];W[06];B[05];W[ca];B[d0];W[c1];B[8e];W[cc];B[6e]"}
`
![an example game between alpha-beta-pruning agents](https://github.com/zhixiangli/gomoku-battle/blob/master/example-game.gif)

## Configuration
**AI Agent** can be changed by modifying the **player.properties**

+ **player.xxxxx.alias**: set an alias for the agent.
+ **player.xxxxx.cmd**: a shell script to start an agent. No agent will start if the command is empty. Every agent should implement the following command.

## A Configuration Example
```
player.black.cmd=java -jar bin/gomoku-battle-alphabetasearch-0.0.1-SNAPSHOT-jar-with-dependencies.jar
player.black.alias=alpha-beta-search
player.white.cmd=java -jar bin/gomoku-battle-alphabetasearch-0.0.1-SNAPSHOT-jar-with-dependencies.jar
player.white.alias=alpha-beta-search
```

## AI Agent API
The console will create a subprocess for an AI agent.

The communication commands between console and AI agent is by **stdin** and **stdout**

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