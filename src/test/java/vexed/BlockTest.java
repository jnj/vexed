package vexed;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BlockTest {

    @Test
    public void isWall() {
        assertTrue(Block.wall().isWall());
    }

    @Test
    public void sameSymbolsImplyEqualBlocks() {
        final var symbol = 'A';
        assertEquals(new Block(symbol), new Block(symbol));
    }

    @Test
    public void equalBlocksHaveEqualHashCodes() {
        final var symbol = 'A';
        assertEquals(new Block(symbol).hashCode(), new Block(symbol).hashCode());
    }
}
