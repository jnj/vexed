package vexed;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlockTest {

    @Test
    public void testIsWall() {
        assertTrue(new Block(Block.WALL_SYMBOL).isWall());
    }

    @Test
    public void testSameSymbolsImplyEqualBlocks() {
        final var symbol = 'A';
        assertEquals(new Block(symbol), new Block(symbol));
    }

    @Test
    public void testEqualBlocksHaveEqualHashCodes() {
        final var symbol = 'A';
        assertEquals(new Block(symbol).hashCode(), new Block(symbol).hashCode());
    }
}
