package vexed;

public class Vexed {

	public static void main(String[] args) {		
		Solver solver = new BfsSolver();
		
		String boardDesc = "#A   #\n" +
						   "#B   #\n" +
						   "#A   #\n" +
						   "##   #\n" +
						   "#B   #\n" +
						   "######";
		Board board = MapBoard.fromString(boardDesc);
		solver.solve(board);
	}

}
