package vexed;


public interface Solver {

	/**
	 * Solves the board in the fewest possible moves. 
	 * Returns the moves that solved it, in order.
	 */
	Solution solve(Board board)
		throws UnsolveableBoardException;
}
