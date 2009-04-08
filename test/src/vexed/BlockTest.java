package vexed;

import junit.framework.TestCase;

public class BlockTest extends TestCase {

    public void testIsWall() {
        assertTrue(new Block(Block.WALL_SYMBOL).isWall());
    }

    public void testSameSymbolsImplyEqualBlocks() {
        char symbol = 'A';
        Block a = new Block(symbol);
        Block b = new Block(symbol);
        assertEquals(a, b);
    }

    public void testEqualBlocksHaveEqualHashCodes() {
        char symbol = 'A';
        Block a = new Block(symbol);
        Block b = new Block(symbol);
        assertEquals(a.hashCode(), b.hashCode());
    }

}
