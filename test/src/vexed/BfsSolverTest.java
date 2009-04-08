package vexed;

import java.util.Arrays;

import junit.framework.TestCase;

public class BfsSolverTest extends TestCase {

    private BfsSolver _solver;
        
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _solver = new BfsSolver();
    }

    public void testSolveTrivialBoard() {
        MapBoard.Builder boardBuilder = new MapBoard.Builder(3);
        boardBuilder.addInteriorRow(" A ");
        boardBuilder.addInteriorRow(" # ");
        boardBuilder.addInteriorRow("A  ");
        Board board = boardBuilder.build();
        MoveHistory moves = _solver.solve(board).getMoveHistory();
        assertEquals(Arrays.asList(new Move(2, 0, Direction.Left)), moves.getMoves());
    }
    
    public void testSolveNonTrivialBoard() {
        MapBoard.Builder boardBuilder = new MapBoard.Builder(5);
        boardBuilder.addInteriorRow(" BA  ");
        boardBuilder.addInteriorRow(" ##  ");
        boardBuilder.addInteriorRow("  A B");
        Board board = boardBuilder.build();
        MoveHistory moves = _solver.solve(board).getMoveHistory();        
        assertEquals(Arrays.asList(new Move(3, 0, Direction.Right),
                                   new Move(2, 0, Direction.Right),
                                   new Move(3, 0, Direction.Right)), moves.getMoves()); 
    }
    
    public void testSolveHellishBoard() {
        MapBoard.Builder boardBuilder = new MapBoard.Builder(6);
        boardBuilder.addInteriorRow("   C  ");
        boardBuilder.addInteriorRow("  BA  ");
        boardBuilder.addInteriorRow("  ##  ");
        boardBuilder.addInteriorRow("  A BC");        
        Board board = boardBuilder.build();
        System.out.println(_solver.solve(board));
    }
    
    public void testSolveEvilBoard() {
        MapBoard.Builder builder = new MapBoard.Builder(5);
        builder.addInteriorRow("  Y  ");
        builder.addInteriorRow(" ZX X");
        builder.addInteriorRow(" ## #");
        builder.addInteriorRow(" XZ  ");
        builder.addInteriorRow("###YZ");
        Board board = builder.build();
        System.out.println(_solver.solve(board));
    }
    
    public void testImpossibleBoard() {
        MapBoard board = MapBoard.fromString("#D#");
        try {
            _solver.solve(board);
            fail("expected exception");
        } catch (UnsolveableBoardException e) {            
        }
    }
}
