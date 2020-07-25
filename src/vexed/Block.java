package vexed;

public record Block(char symbol) {
    static final char WALL_SYMBOL = '#';

    static Block wall() {
        return new Block(WALL_SYMBOL);
    }

    boolean isWall() {
        return symbol == WALL_SYMBOL;
    }
}
