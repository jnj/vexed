package vexed;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.Before;

import java.util.Arrays;
import java.util.Collections;

public abstract class SolverTest {

    private Solver _solver;
    private final PositionSupplier _positionSupplier = new CachingPositionSupplier();

    abstract Solver getSolverInstance();

    @Before
    public void setup() {
        _solver = getSolverInstance();
    }

    @Test
    public void solveTrivialBoard() {
        MapBoard.Builder boardBuilder = new MapBoard.Builder(3);
        boardBuilder.addInteriorRow(" A ");
        boardBuilder.addInteriorRow(" # ");
        boardBuilder.addInteriorRow("A  ");
        Board board = boardBuilder.build(_positionSupplier);
        MoveHistory moves = _solver.solve(board).getMoveHistory();
        assertEquals(Collections.singletonList(new Move(2, 0, Direction.Left)), moves.getMoves());
    }

    @Test
    public void solveNonTrivialBoard() {
        MapBoard.Builder boardBuilder = new MapBoard.Builder(5);
        boardBuilder.addInteriorRow(" BA  ");
        boardBuilder.addInteriorRow(" ##  ");
        boardBuilder.addInteriorRow("  A B");
        Board board = boardBuilder.build(_positionSupplier);
        MoveHistory moves = _solver.solve(board).getMoveHistory();
        assertEquals(Arrays.asList(new Move(3, 0, Direction.Right),
                new Move(2, 0, Direction.Right),
                new Move(3, 0, Direction.Right)), moves.getMoves());
    }

    @Test
    public void solveHellishBoard() {
        MapBoard.Builder boardBuilder = new MapBoard.Builder(6);
        boardBuilder.addInteriorRow("   C  ");
        boardBuilder.addInteriorRow("  BA  ");
        boardBuilder.addInteriorRow("  ##  ");
        boardBuilder.addInteriorRow("  A BC");
        Board board = boardBuilder.build(_positionSupplier);
        System.out.println(_solver.solve(board));
    }

    @Test
    public void solveEvilBoard() {
        MapBoard.Builder builder = new MapBoard.Builder(5);
        builder.addInteriorRow("  Y  ");
        builder.addInteriorRow(" ZX X");
        builder.addInteriorRow(" ## #");
        builder.addInteriorRow(" XZ  ");
        builder.addInteriorRow("###YZ");
        Board board = builder.build(_positionSupplier);
        System.out.println(_solver.solve(board));
    }

    @Test
    public void recognizeImpossibleToSolveBoard() {
        MapBoard board = MapBoard.fromString("#D#", _positionSupplier);
        try {
            _solver.solve(board);
            fail("expected exception");
        } catch (UnsolveableBoardException ignored) {
        }
    }
}
