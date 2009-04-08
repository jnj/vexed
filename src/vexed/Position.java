package vexed;

public class Position {
	private final int _column;
	private final int _row;
	
	public Position(final int column, final int row) {
		_column = column;
		_row = row;
	}

	public int getColumn() {
		return _column;
	}

	public int getRow() {
		return _row;
	}
	
	public Position getNeighborTo(Direction direction) {
		return direction.apply(this);
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof Position)) {
			return false;
		} else {
			Position other = (Position) o;
			return other._row == _row && other._column == _column;
		}
	}
	
	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode = 31 * hashCode + _column;
		hashCode = 31 * hashCode + _row;
		return hashCode;
	}
	
	@Override
	public String toString() {
		return "(" + _column + ", " + _row + ")";
	}
}
