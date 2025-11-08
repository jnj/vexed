package vexed;

public class Vexed {

    public static void main(String[] args) {
        final var solver = new ConcurrentSolver();
        final var positionSupplier = new CachingPositionSupplier();
        final var boardDesc = """
                #A   #
                #B   #
                #A   #
                ##   #
                #B   #
                ######""";
        final var board = MapBoard.fromString(boardDesc, positionSupplier, new BlockCache(), new MoveCache(6));
        System.out.println(solver.solve(board));
    }

}
