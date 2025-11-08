package vexed;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static vexed.Direction.Left;
import static vexed.Direction.Right;

public class MapBoardTest {

    private final BlockCache blockCache = new BlockCache();
    private final MoveCache moveCache = new MoveCache(10);
    private final PositionSupplier positionSupplier = new CachingPositionSupplier();

	@Test
    public void mapBuilder() {
		final var builder = new MapBoard.Builder(4);
        builder.addInteriorRow(" A  ");
        builder.addInteriorRow(" # B");
        builder.addInteriorRow("C  #");
		final var board = builder.build(positionSupplier, blockCache, moveCache);
        assertEquals(6, board.getWidth());
        assertEquals(4, board.getHeight());
    }

	@Test
    public void getAvailableMovesReturnsAllAvailableMoves() {
		final var builder = new MapBoard.Builder(4);
        builder.addInteriorRow(" A  ");
        builder.addInteriorRow(" # B");
        builder.addInteriorRow("C  #");
		final var board = builder.build(positionSupplier, blockCache, moveCache);
        final Collection<Move> expectedMoves = List.of(new Move(2, 0, Left),
                                                       new Move(2, 0, Right), 
                                                       new Move(4, 1, Left), 
                                                       new Move(1, 2, Right));
        TestSupport.assertEqualContents(expectedMoves, board.getAvailableMoves());       
    }

	@Test
	public void emptyBoardsWithEqualDimensionsAreEqual() {
		final var layout = new HashMap<Position, Block>();
		final var firstBoard = new MapBoard(1, 1, layout, positionSupplier, moveCache);
		final var secondBoard = new MapBoard(1, 1, layout, positionSupplier, moveCache);
		assertEquals(firstBoard, secondBoard);
	}

	@Test
	public void boardsWithDifferentDimensionsAreUnequal() {
		final var layout = new HashMap<Position, Block>();
		final var firstBoard = new MapBoard(5, 2, layout, positionSupplier, moveCache);
		final var secondBoard = new MapBoard(5, 3, layout, positionSupplier, moveCache);
		assertNotEquals(firstBoard, secondBoard);
	}

	@Test
	public void boardsWithSameDimensionsAndContentsAreEqual() {
		final var layout = new HashMap<Position, Block>();
		layout.put(new Position(0, 0), Block.wall());
		layout.put(new Position(1, 2), new Block('A'));
		final var firstBoard = new MapBoard(4, 4, layout, positionSupplier, moveCache);
		final var secondBoard = new MapBoard(4, 4, layout, positionSupplier, moveCache);
		assertEquals(firstBoard, secondBoard);
	}

	@Test
	public void getBlockAt() {
		final var layout = new HashMap<Position, Block>();
		final var block = Block.wall();
		final var position = new Position(1, 3);
		layout.put(position, block);
		final var board = new MapBoard(3, 4, layout, positionSupplier, moveCache);
		assertEquals(block, board.getBlockAt(position));
		assertNull(board.getBlockAt(new Position(0, 1)));
	}

	@Test
	public void applyMoveWillNotMakeImpossibleMove() {
		final var board = MapBoard.fromString("#A#", positionSupplier, blockCache, moveCache);
		
		try {
			board.apply(new Move(1, 0, Left));
			fail("expected exception");
		} catch (final IllegalMoveException ignored) {
		}	
		
		try {
			board.apply(new Move(0, 0, Left));
			fail("expected exception");
		} catch (final IllegalMoveException ignored) {
		}
	}

	@Test
    public void applyMoveRecordsMoveInHistory() {
		final var layout = "#A #\n" +
			               "####";
		final var board = MapBoard.fromString(layout, positionSupplier, blockCache, moveCache);
		final var move = new Move(1, 0, Right);
        final Board newBoard = board.apply(move);
        assertEquals(1, newBoard.getMoveHistory().size());
        assertEquals(Collections.singletonList(move), newBoard.getMoveHistory().getMoves());
    }

    @Test
	public void applyMoveCanMakeSimpleMove() {
		assertMoveResult("#A #", "# A#", new Move(1, 0, Right));
	}

	@Test
	public void applyMoveCanMakeMoveWithFalling() {
		final var layoutText =
                """
                #A #
                ## #
                ####""";

		final var expectedLayoutText =
                """
                #  #
                ##A#
                ####""";
		
		assertMoveResult(layoutText, expectedLayoutText, new Move(1, 0, Right));
	}

	@Test
    public void applyMoveWithDifferentBlocks() {
		var boardBuilder = new MapBoard.Builder(5);
        boardBuilder.addInteriorRow(" BA  ");
        boardBuilder.addInteriorRow(" ##  ");
        boardBuilder.addInteriorRow(" A  B");
		final var board = boardBuilder.build(positionSupplier, blockCache, moveCache);
		final var newBoard = board.apply(new Move(3, 0, Right));

        boardBuilder = new MapBoard.Builder(5);
        boardBuilder.addInteriorRow(" B   ");
        boardBuilder.addInteriorRow(" ##  ");
        boardBuilder.addInteriorRow(" A AB");
		final var expectedBoard = boardBuilder.build(positionSupplier, blockCache, moveCache);
        
        assertEquals(expectedBoard, newBoard);
    }

	@Test
    public void moveHistoryRetainsMoves() {
		final var boardBuilder = new MapBoard.Builder(5);
        boardBuilder.addInteriorRow(" BA  ");
        boardBuilder.addInteriorRow(" ##  ");
        boardBuilder.addInteriorRow(" A  B");
		final var board = boardBuilder.build(positionSupplier, blockCache, moveCache);
		final var firstMove = new Move(3, 0, Right);
		final var firstBoard = board.apply(firstMove);
        assertEquals(1, firstBoard.getMoveHistory().size());
        assertEquals(Collections.singletonList(firstMove), firstBoard.getMoveHistory().getMoves());
		final var secondMove = new Move(2, 0, Left);
		final var secondBoard = firstBoard.apply(secondMove);
        assertEquals(2, secondBoard.getMoveHistory().size());
        assertEquals(List.of(firstMove, secondMove), secondBoard.getMoveHistory().getMoves());
    }

	@Test
	public void applyMoveWithVanishingBlocks() {
		final var layoutText =
                """
                #B   #
                #C   #
                ##   #
                # C  #
                ######""";
		final var expectedLayoutText =
                """
                #    #
                #B   #
                ##   #
                #    #
                ######""";
		
		assertMoveResult(layoutText, expectedLayoutText, new Move(1, 1, Right));		
	}

	@Test
	public void fromString() {
		final var builder =
                """
                #...#
                #.C.#
                #ABC#
                #####""";
		final var boardFromString = MapBoard.fromString(builder, positionSupplier, blockCache, moveCache);
        final var layout = new HashMap<Position, Block>();
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

		final var expectedBoard = new MapBoard(5, 4, layout, positionSupplier, moveCache);
		assertEquals(expectedBoard, boardFromString);
	}

	@Test
	public void isSolved() {
		assertFalse(MapBoard.fromString("#A#", positionSupplier, blockCache, moveCache).isSolved());
		assertTrue(MapBoard.fromString("# #", positionSupplier, blockCache, moveCache).isSolved());
	}
	
	private void assertMoveResult(String initialLayout, String expectedLayout, Move move) {
		final var board = MapBoard.fromString(initialLayout, positionSupplier, blockCache, moveCache);
		assertEquals(MapBoard.fromString(expectedLayout, positionSupplier, blockCache, moveCache), board.apply(move));
	}
}
