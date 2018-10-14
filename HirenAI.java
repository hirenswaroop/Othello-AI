import java.util.List;

import com.briansea.cabinet.GameState;
import com.briansea.cabinet.Plugin;
import com.briansea.cabinet.PluginInfo;
import com.briansea.game.Player;
import com.briansea.game.Move;

/**
 * AI for the game Othello
 * 
 * @author Hiren
 */
public class HirenAI extends Player {
	/**
	 * Class for the nodes that 
	 * make up the k-ary tree
	 */
	public class MMNode {
		private int alpha;
		private int beta;
		private GameState gs;
		private boolean max;
		private Move bestMove;
		
		/**
		 * Constructor to create a Node
		 * 
		 * @param gs
		 * @param alpha
		 * @param beta
		 * @param max
		 */
		public MMNode (GameState gs, int alpha, int beta, boolean max) {
			this.alpha = alpha;
			this.beta = beta;
			this.gs = gs;
			this.max = max;
		}
		
		/**
		 * @return best move
		 */
		public Move getBestMove() {
			return this.bestMove;
		}

		/**
		 * Minimax algorithm with Alpha-Beta pruning 
		 * 
		 * @param depth
		 * @return integer value of the best move
		 */
		public int minMax(int depth) {
			List<Move> valids = gs.getValidMoves();
			if (valids.size() > 0) {
				bestMove = valids.get(0);
			} else {
				if (gs.isGameOver()) {
					gs.stop();
					gs.shutdown();
					gs.removeAllPlayers();
				}
				return Integer.MAX_VALUE - 1;
			}
			if (depth == 0) {
				return getHeuristic(bestMove);
			}
			for (Move m : valids) {
				MMNode child = new MMNode(gs.copyInstance(), alpha, beta, !max);
				int val = child.minMax(depth - 1);
				if (max) {
					if(val > alpha) {
						alpha = val;
						bestMove = m;
					}
					
				} else {
					if(val < beta) {
						beta = val;
						bestMove = m;
					}
				}
				if (alpha > beta) {
					break;
				}
			}
			if (max) {
				return alpha;
			} else {
				return beta;
			}
		}
	}
	
	/**
	 * Gets the numerical value of a given move
	 * 
	 * @param bestMove
	 * @return integer value of a move
	 */
	public int getHeuristic(Move bestMove) {
		int k = 0;
		int bm = 0;
		int board[][] = new int[8][8];
		int nums[] = {50, -5, 10, 8, 8, 10, -5, 50,
				-5, -10, -6, 2, 2, -6, -10, -5,
				10, -6, 5, 5, 5, 5, -6, 10, 
				8, 2, 5, -5, -5, 5, 2, 8, 
				8, 2, 5, -5, -5, 5, 2, 8, 
				10, -6, 5, 5, 5, 5, -6, 10,
				-5, -10, -6, 2, 2, -6, -10, -5,
				50, -5, 10, 8, 8, 10, -5, 50};
		
		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				board[i][j] = nums[k];
				if ((bestMove.to.x == i) && (bestMove.to.y == j)) {
					bm = board[i][j];
				}
				k++;
			}
		}
		return bm;
	}
	
	public static PluginInfo getInfo() {
		return new PluginInfo() {
			@Override
			public String description() {
				return "Hiren's AI";
			}

			@Override
			public String name() {
				return "HirenAI";
			}

			@Override
			public List<Class<? extends GameState>> supportedGames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Class<? extends Plugin> type() {
				return Player.class;
			}
		};
	}
	
	@Override
	/**
	 * Adds the best moves to a given list of moves
	 * 
	 * @param gs
	 * @param m
	 */
	public void makeMove(GameState gs, List<Move> m) {
		MMNode root = new MMNode(gs, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
		root.minMax(4);
		if(root.getBestMove() != null) {
			m.add(root.getBestMove());
		}
	}
}