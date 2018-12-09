package vexed;

public class Vexed {

    public static void main(String[] args) {
        Solver solver = new ConcurrentSolver();
        PositionSupplier positionSupplier = new CachingPositionSupplier();

        String boardDesc = "#A   #\n" +
                           "#B   #\n" +
                           "#A   #\n" +
                           "##   #\n" +
                           "#B   #\n" +
                           "######";
        Board board = MapBoard.fromString(boardDesc, positionSupplier);
        System.out.println(solver.solve(board));
    }

}
