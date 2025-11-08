package vexed;

public record Block(char symbol) {
    public static final char WALL_SYMBOL = '#';
    public static final Block WALL = new Block(WALL_SYMBOL);

    static Block wall() {
        return WALL;
    }

    boolean isWall() {
        return symbol == WALL_SYMBOL;
    }
}
