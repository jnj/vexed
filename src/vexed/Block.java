package vexed;

public class Block {
    static final char WALL_SYMBOL = '#';

    private final char _symbol;

    Block(char symbol) {
        _symbol = symbol;
    }

    static Block wall() {
        return new Block(WALL_SYMBOL);
    }

    char getSymbol() {
        return _symbol;
    }

    boolean isWall() {
        return _symbol == WALL_SYMBOL;
    }

    @Override
    public String toString() {
        return String.valueOf(_symbol);
    }

    @Override
    public int hashCode() {
        return _symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Block)) {
            return false;
        } else {
            Block other = (Block) o;
            return other._symbol == _symbol;
        }
    }
}
