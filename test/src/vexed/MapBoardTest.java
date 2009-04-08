package vexed;

import static vexed.Direction.Left;
import static vexed.Direction.Right;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import junit.framework.TestCase;

public class MapBoardTest extends TestCase {
	
    public void testMapBuilder() {
        MapBoard.Builder builder = new MapBoard.Builder(4);
        builder.addInteriorRow(" A  ");
        builder.addInteriorRow(" # B");
        builder.addInteriorRow("C  #");
        MapBoard board = builder.build();
        assertEquals(6, board.getWidth());
        assertEquals(4, board.getHeight());
    }
    
    public void testGetAvailableMovesReturnsAllAvailableMoves() {
        MapBoard.Builder builder = new MapBoard.Builder(4);
        builder.addInteriorRow(" A  ");
        builder.addInteriorRow(" # B");
        builder.addInteriorRow("C  #");
        MapBoard board = builder.build();
        Collection<Move> expectedMoves = Arrays.asList(new Move(2, 0, Left),
                                                       new Move(2, 0, Right), 
                                                       new Move(4, 1, Left), 
                                                       new Move(1, 2, Right));
        TestSupport.assertEqualContents(expectedMoves, board.getAvailableMoves());       
    }
    
	public void testEmptyBoardsWithEqualDimensionsAreEqual() {
		Map<Position, Block> layout = Containers.newHashMap();
		MapBoard firstBoard = new MapBoard(1, 1, layout);
		MapBoard secondBoard = new MapBoard(1, 1, layout);
		assertEquals(firstBoard, secondBoard);
	}
	
	public void testBoardsWithDifferentDimensionsAreUnequal() {
		Map<Position, Block> layout = Containers.newHashMap();
		MapBoard firstBoard = new MapBoard(5, 2, layout);
		MapBoard secondBoard = new MapBoard(5, 3, layout);
		assertFalse(firstBoard.equals(secondBoard));
	}
	
	public void testBoardsWithSameDimensionsAndContentsAreEqual() {
		Map<Position, Block> layout = Containers.newHashMap();
		layout.put(new Position(0, 0), Block.wall());
		layout.put(new Position(1, 2), new Block('A'));
		MapBoard firstBoard = new MapBoard(4, 4, layout);
		MapBoard secondBoard = new MapBoard(4, 4, layout);
		assertEquals(firstBoard, secondBoard);
	}
	
	public void testGetBlockAt() {
		Map<Position, Block> layout = Containers.newHashMap();
		Block block = Block.wall();
		Position position = new Position(1, 3);
		layout.put(position, block);		
		MapBoard board = new MapBoard(3, 4, layout);
		assertEquals(block, board.getBlockAt(position));
		assertNull(board.getBlockAt(new Position(0, 1)));
	}
	
	public void testApplyMoveWillNotMakeImpossibleMove() {
		MapBoard board = MapBoard.fromString("#A#");
		
		try {
			board.apply(new Move(1, 0, Left));
			fail("expected exception");
		} catch (IllegalMoveException e) {			
		}	
		
		try {
			board.apply(new Move(0, 0, Left));
			fail("expected exception");
		} catch (IllegalMoveException e) {			
		}
	}
	
    public void testApplyMoveRecordsMoveInHistory() {
        String layout = "#A #\n" +
                        "####";
        MapBoard board = MapBoard.fromString(layout);
        Move move = new Move(1, 0, Right);
        Board newBoard = board.apply(move);
        assertEquals(1, newBoard.getMoveHistory().size());
        assertEquals(Arrays.asList(move), newBoard.getMoveHistory().getMoves());
    }
    
	public void testApplyMoveCanMakeSimpleMove() {
		assertMoveResult("#A #", "# A#", new Move(1, 0, Right));
	}
	
	public void testApplyMoveCanMakeMoveWithFalling() {
		String layoutText = 
			"#A #\n" +
			"## #\n" +
			"####";
	
		String expectedLayoutText = 
			"#  #\n" +
			"##A#\n" +
			"####";
		
		assertMoveResult(layoutText, expectedLayoutText, new Move(1, 0, Right));
	}
	
    public void testApplyMoveWithDifferentBlocks() {
        MapBoard.Builder boardBuilder = new MapBoard.Builder(5);
        boardBuilder.addInteriorRow(" BA  ");
        boardBuilder.addInteriorRow(" ##  ");
        boardBuilder.addInteriorRow(" A  B");
        MapBoard board = boardBuilder.build();
        MapBoard newBoard = board.apply(new Move(3, 0, Right));

        boardBuilder = new MapBoard.Builder(5);
        boardBuilder.addInteriorRow(" B   ");
        boardBuilder.addInteriorRow(" ##  ");
        boardBuilder.addInteriorRow(" A AB");
        MapBoard expectedBoard = boardBuilder.build();
        
        assertEquals(expectedBoard, newBoard);
    }
    
    public void testMoveHistoryRetainsMoves() {
        MapBoard.Builder boardBuilder = new MapBoard.Builder(5);
        boardBuilder.addInteriorRow(" BA  ");
        boardBuilder.addInteriorRow(" ##  ");
        boardBuilder.addInteriorRow(" A  B");
        MapBoard board = boardBuilder.build();
        Move firstMove = new Move(3, 0, Right);
        MapBoard firstBoard = board.apply(firstMove);
        assertEquals(1, firstBoard.getMoveHistory().size());
        assertEquals(Arrays.asList(firstMove), firstBoard.getMoveHistory().getMoves());
        Move secondMove = new Move(2, 0, Left);
        MapBoard secondBoard = firstBoard.apply(secondMove);
        assertEquals(2, secondBoard.getMoveHistory().size());
        assertEquals(Arrays.asList(firstMove, secondMove), 
                     secondBoard.getMoveHistory().getMoves());
    }
    
	public void testApplyMoveWithVanishingBlocks() {
		String layoutText = 
			"#B   #\n" +
			"#C   #\n" +
			"##   #\n" +
			"# C  #\n" +
			"######";
		String expectedLayoutText = 
			"#    #\n" +
			"#B   #\n" +
			"##   #\n" +
			"#    #\n" +
			"######";		
		
		assertMoveResult(layoutText, expectedLayoutText, new Move(1, 1, Right));		
	}

	public void testFromString() {
		StringBuilder builder = new StringBuilder();
		builder.append("#...#\n");
		builder.append("#.C.#\n");
		builder.append("#ABC#\n");
		builder.append("#####");
		MapBoard boardFromString = MapBoard.fromString(builder.toString());
		
		Map<Position, Block> layout = Containers.newHashMap();
		layout.put(new Position(0, 0), Block.wall());
		layout.put(new Position(4, 0), Block.wall());
		layout.put(new Position(0, 1), Block.wall());
		layout.put(new Position(0, 2), Block.wall());
		layout.put(new Position(0, 3), Block.wall());
		layout.put(new Position(4, 1), Block.wall());
		layout.put(new Position(4, 2), Block.wall());
		layout.put(new Position(4, 3), Block.wall());
		layout.put(new Position(1, 3), Block.wall());
		layout.put(new Position(2, 3), Block.wall());
		layout.put(new Position(3, 3), Block.wall());
		layout.put(new Position(2, 1), new Block('C'));
		layout.put(new Position(2, 2), new Block('B'));
		layout.put(new Position(1, 2), new Block('A'));
		layout.put(new Position(3, 2), new Block('C'));
		
		MapBoard expectedBoard = new MapBoard(5, 4, layout);
		assertEquals(expectedBoard, boardFromString);
	}
	
	public void testIsSolved() {
		assertFalse(MapBoard.fromString("#A#").isSolved());
		assertTrue(MapBoard.fromString("# #").isSolved());		
	}
	
	private void assertMoveResult(String initialLayout, String expectedLayout, Move move) {
		MapBoard board = MapBoard.fromString(initialLayout);
		assertEquals(MapBoard.fromString(expectedLayout), board.apply(move));
	}
}
