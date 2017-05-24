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
        char symbol = 'A';
        Block a = new Block(symbol);
        Block b = new Block(symbol);
        assertEquals(a, b);
    }

    @Test
    public void testEqualBlocksHaveEqualHashCodes() {
        char symbol = 'A';
        Block a = new Block(symbol);
        Block b = new Block(symbol);
        assertEquals(a.hashCode(), b.hashCode());
    }

}
