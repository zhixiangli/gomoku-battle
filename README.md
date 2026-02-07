# Gomoku Battle

> A cross-language, cross-platform battle arena for Gomoku AI agents — build your own player and match it against others on a standardized protocol.

<!---
{"command":"NEXT_WHITE","rows":15,"columns":15,"chessboard":"B[76];W[65];B[55];W[66];B[67];W[58];B[74];W[75];B[57];W[64];B[53];W[54];B[63];W[85];B[95];W[86];B[87];W[97];B[a8];W[43];B[47];W[77];B[46];W[32];B[21];W[37];B[48];W[45];B[4a];W[49];B[35];W[24];B[79];W[68];B[44];W[62];B[26];W[17];B[52];W[99];B[88];W[98];B[9a];W[89];B[41];W[30];B[11];W[31];B[33];W[22];B[16];W[36];B[18];W[7a];B[6b];W[5b];B[29];W[1a];B[3a];W[a7];B[b6];W[5c];B[5a];W[2a];B[4b];W[07];B[3c];W[69];B[3b];W[8b];B[9c];W[3d];B[94];W[83];B[84];W[b7];B[38];W[39];B[c7];W[a4];B[a5];W[d8];B[96];W[b4];B[8d];W[ab];B[d6];W[c6];B[28];W[08];B[93];W[92];B[9e];W[7c];B[2d];W[1e];B[25];W[27];B[9b];W[9d];B[1d];W[2c];B[4e];W[09];B[4c];W[4d];B[a9];W[b8];B[0a];W[a2];B[b2];W[c9];B[a3];W[ba];B[e7];W[bb];B[b9];W[eb];B[da];W[cb];B[db];W[82];B[72];W[06];B[05];W[ca];B[d0];W[c1];B[8e];W[cc];B[6e]"}
-->

![An example game between Alpha-Beta-Search agents](https://github.com/zhixiangli/gomoku-battle/blob/master/example-game.gif)

## Overview

[**Gomoku**](https://en.wikipedia.org/wiki/Gomoku) (also known as Gobang or Five in a Row) is an abstract strategy board game. Players alternate turns placing a stone of their color on an empty intersection, and the first player to form an unbroken chain of five stones horizontally, vertically, or diagonally wins.

**Gomoku Battle** provides a language-agnostic battle platform that lets any AI agent — written in any language on any operating system — compete through a simple JSON-based communication protocol over standard I/O.

## Highlights

- **gomoku-battle-core** — Shared Gomoku library containing board representation, game rules, and move validation.
- **gomoku-battle-console** — The referee process that manages agent life cycles, relays commands via stdin/stdout, and enforces game rules.
- **gomoku-battle-dashboard** — A real-time JavaFX dashboard for visualizing games as they unfold.

![Dashboard](dashboard.png)

## Getting Started

### Prerequisites

- **Java 17+**
- **Apache Maven 3.6+**

### Build and Run

```bash
git clone https://github.com/zhixiangli/gomoku-battle.git
cd gomoku-battle
sh build.sh
sh battle.sh -c battle.properties
```

To run in headless mode (without the dashboard UI), add the `-d` flag:

```bash
sh battle.sh -c battle.properties -d
```

## Configuration

Agent players are configured in a properties file (e.g., `battle.properties`). Each player has two settings:

| Property | Description |
|---|---|
| `player.<color>.cmd` | Shell command to launch the agent process. Leave empty to skip. |
| `player.<color>.alias` | A human-readable alias displayed in the dashboard. |

### Example

```properties
player.black.cmd=java -jar bin/gomoku-battle-alphabetasearch-0.0.1-SNAPSHOT-jar-with-dependencies.jar
player.black.alias=alpha-beta-search
player.white.cmd=java -jar bin/gomoku-battle-alphabetasearch-0.0.1-SNAPSHOT-jar-with-dependencies.jar
player.white.alias=alpha-beta-search
```

## Agent Communication Protocol

The console spawns each AI agent as a subprocess and communicates through **stdin** (requests) and **stdout** (responses) using JSON.

### Request

The console sends a single-line JSON object with the following fields:

| Field | Description |
|---|---|
| `command` | `NEXT_BLACK` (next move is black) or `NEXT_WHITE` (next move is white) |
| `rows` | Number of rows on the board |
| `columns` | Number of columns on the board |
| `chessboard` | Current board state encoded in SGF notation |

**Example request:**

```json
{"command":"NEXT_BLACK","rows":15,"columns":15,"chessboard":"B[96];W[a5];B[a4];W[95]"}
```

### Response

The agent must reply with a single-line JSON object indicating the chosen move:

| Field | Description |
|---|---|
| `rowIndex` | Zero-based row index of the move |
| `columnIndex` | Zero-based column index of the move |

**Example response:**

```json
{"rowIndex":3,"columnIndex":10}
```

## Built-in Agents

| Agent | Description | Language | Entry Point |
|---|---|---|---|
| [alpha-beta-search](https://github.com/zhixiangli/gomoku-battle/tree/master/gomoku-battle-alphabetasearch) | Minimax agent with alpha-beta pruning | Java | [AlphaBetaSearchAgent.java](https://github.com/zhixiangli/gomoku-battle/blob/master/gomoku-battle-alphabetasearch/src/main/java/com/zhixiangli/gomoku/alphabetasearch/AlphaBetaSearchAgent.java) |