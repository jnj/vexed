package vexed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.Before;

import java.util.Arrays;
import java.util.Collections;

public abstract class SolverTest {

    private Solver solver;
    private final PositionSupplier positionSupplier = new CachingPositionSupplier();

    abstract Solver getSolverInstance();

    @Before
    public void setup() {
        solver = getSolverInstance();
    }

    @Test
    public void solveTrivialBoard() {
        var boardBuilder = new MapBoard.Builder(3);
        boardBuilder.addInteriorRow(" A ");
        boardBuilder.addInteriorRow(" # ");
        boardBuilder.addInteriorRow("A  ");
        Board board = boardBuilder.build(positionSupplier);
        var moves = solver.solve(board).getMoveHistory();
        assertEquals(Collections.singletonList(new Move(2, 0, Direction.Left)), moves.getMoves());
    }

    @Test
    public void solveNonTrivialBoard() {
        var boardBuilder = new MapBoard.Builder(5);
        boardBuilder.addInteriorRow(" BA  ");
        boardBuilder.addInteriorRow(" ##  ");
        boardBuilder.addInteriorRow("  A B");
        Board board = boardBuilder.build(positionSupplier);
        var moves = solver.solve(board).getMoveHistory();
        assertEquals(Arrays.asList(new Move(3, 0, Direction.Right),
                new Move(2, 0, Direction.Right),
                new Move(3, 0, Direction.Right)), moves.getMoves());
    }

    @Test
    public void solveHellishBoard() {
        var boardBuilder = new MapBoard.Builder(6);
        boardBuilder.addInteriorRow("   C  ");
        boardBuilder.addInteriorRow("  BA  ");
        boardBuilder.addInteriorRow("  ##  ");
        boardBuilder.addInteriorRow("  A BC");
        Board board = boardBuilder.build(positionSupplier);
        System.out.println(solver.solve(board));
    }

    @Test
    public void solveEvilBoard() {
        var builder = new MapBoard.Builder(5);
        builder.addInteriorRow("  Y  ");
        builder.addInteriorRow(" ZX X");
        builder.addInteriorRow(" ## #");
        builder.addInteriorRow(" XZ  ");
        builder.addInteriorRow("###YZ");
        Board board = builder.build(positionSupplier);
        System.out.println(solver.solve(board));
    }

    @Test
    public void solveBenchmarkBoard() {
        var builder = new MapBoard.Builder(8);
        builder.addInteriorRow("D      B");
        builder.addInteriorRow("#      #");
        builder.addInteriorRow("  #A    ");
        builder.addInteriorRow("   H#BE ");
        builder.addInteriorRow("   C ## ");
        builder.addInteriorRow("   #    ");
        builder.addInteriorRow(" # #    ");
        builder.addInteriorRow("H  D#EAC");
        var board = builder.build(positionSupplier);
        System.out.println(solver.solve(board));
    }

    @Test
    public void recognizeImpossibleToSolveBoard() {
        var board = MapBoard.fromString("#D#", positionSupplier);
        try {
            solver.solve(board);
            fail("expected exception");
        } catch (UnsolveableBoardException ignored) {
        }
    }
}
