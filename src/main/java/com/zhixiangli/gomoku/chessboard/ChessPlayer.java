/**
 * 
 */
package com.zhixiangli.gomoku.chessboard;

import com.zhixiangli.gomoku.agent.GomokuAgent;
import com.zhixiangli.gomoku.agent.alphabeta.GomokuAlphaBetaPruning;
import com.zhixiangli.gomoku.agent.mcts.GomokuMCTS;

/**
 * @author zhixiangli
 *
 */
public enum ChessPlayer {

	HUMAN(null),

	ALPHA_BETA_SEARCH(GomokuAlphaBetaPruning.class),

	MCTS_SEARCH(GomokuMCTS.class),

	;

	private Class<? extends GomokuAgent> agent;

	private ChessPlayer(Class<? extends GomokuAgent> agent) {
		this.agent = agent;
	}

	/**
	 * @return the agent
	 */
	public Class<? extends GomokuAgent> getAgent() {
		return agent;
	}

}
