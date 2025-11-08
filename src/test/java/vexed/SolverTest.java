package vexed;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public abstract class SolverTest {

    private Solver solver;
    private final MoveCache moveCache = new MoveCache(10);
    private final BlockCache blockCache = new BlockCache();
    private final PositionSupplier positionSupplier = new CachingPositionSupplier();

    abstract Solver getSolverInstance();

    @Before
    public void setup() {
        solver = getSolverInstance();
    }

    @Test
    public void solveTrivialBoard() {
        final var boardBuilder = new MapBoard.Builder(3);
        boardBuilder.addInteriorRow(" A ");
        boardBuilder.addInteriorRow(" # ");
        boardBuilder.addInteriorRow("A  ");
        final Board board = boardBuilder.build(positionSupplier, blockCache, moveCache);
        final var moves = solver.solve(board).getMoveHistory();
        assertEquals(Collections.singletonList(new Move(2, 0, Direction.Left)), moves.getMoves());
    }

    @Test
    public void solveNonTrivialBoard() {
        final var boardBuilder = new MapBoard.Builder(5);
        boardBuilder.addInteriorRow(" BA  ");
        boardBuilder.addInteriorRow(" ##  ");
        boardBuilder.addInteriorRow("  A B");
        final Board board = boardBuilder.build(positionSupplier, blockCache, moveCache);
        final var moves = solver.solve(board).getMoveHistory();
        assertEquals(Arrays.asList(new Move(3, 0, Direction.Right),
                new Move(2, 0, Direction.Right),
                new Move(3, 0, Direction.Right)), moves.getMoves());
    }

    @Test
    public void solveHellishBoard() {
        final var boardBuilder = new MapBoard.Builder(6);
        boardBuilder.addInteriorRow("   C  ");
        boardBuilder.addInteriorRow("  BA  ");
        boardBuilder.addInteriorRow("  ##  ");
        boardBuilder.addInteriorRow("  A BC");
        final Board board = boardBuilder.build(positionSupplier, blockCache, moveCache);
        System.out.println(solver.solve(board));
    }

    @Test
    public void solveEvilBoard() {
        final var builder = new MapBoard.Builder(5);
        builder.addInteriorRow("  Y  ");
        builder.addInteriorRow(" ZX X");
        builder.addInteriorRow(" ## #");
        builder.addInteriorRow(" XZ  ");
        builder.addInteriorRow("###YZ");
        final Board board = builder.build(positionSupplier, blockCache, moveCache);
        System.out.println(solver.solve(board));
    }

    @Test
    @Ignore
    public void solveBenchmarkBoard() {
        final var builder = new MapBoard.Builder(8);
        builder.addInteriorRow("D      B");
        builder.addInteriorRow("#      #");
        builder.addInteriorRow("  #A    ");
        builder.addInteriorRow("   H#BE ");
        builder.addInteriorRow("   C ## ");
        builder.addInteriorRow("   #    ");
        builder.addInteriorRow(" # #    ");
        builder.addInteriorRow("H  D#EAC");
        final var board = builder.build(positionSupplier, blockCache, moveCache);
        System.out.println(solver.solve(board));
    }

    @Test(expected = UnsolveableBoardException.class)
    public void recognizeImpossibleToSolveBoard() {
        final var board = MapBoard.fromString("#D#", positionSupplier, blockCache, moveCache);
        solver.solve(board);
    }

    @Test(expected = UnsolveableBoardException.class)
    public void recognizeImpossibleToSolveBoard2() {
        final var board = MapBoard.fromString("""
                #A  #
                ##  #
                #  B#
                #####""",
                positionSupplier, blockCache, moveCache);
        solver.solve(board);
    }
}
