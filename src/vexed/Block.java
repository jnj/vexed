package vexed;

public class Block {
	public static final char WALL_SYMBOL = '#';
	
	private final char _symbol;

	public static Block wall() {
		return new Block(WALL_SYMBOL);
	}
	
	public Block(char symbol) {
		_symbol = symbol;
	}

	public char getSymbol() {
		return _symbol;
	}
	
	public boolean isWall() {
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
