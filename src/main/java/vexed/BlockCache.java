package vexed;

import java.util.ArrayList;
import java.util.List;

public class BlockCache {
    private final List<Character> chars = new ArrayList<>();
    private final List<Block> blocks = new ArrayList<>();

    public Block blockFor(char symbol) {
        var i = chars.indexOf(symbol);
        if (i < 0) {
            i = chars.size();
            chars.add(symbol);
            blocks.add(new Block(symbol));
        }
        return blocks.get(i);
    }
}
