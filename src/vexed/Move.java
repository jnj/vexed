package vexed;

public class Move {
	private final Position _position;
	private final Direction _direction;

	public Move(int column, int row, Direction direction) {
		this(new Position(column, row), direction);
	}
	
	public Move(final Position position, final Direction direction) {
		_position = position;
		_direction = direction;
	}
	
	public Position getTargetPosition() {
		return _position.getNeighborTo(_direction);
	}

	public Position getPosition() {
		return _position;
	}

	@Override
	public int hashCode() {
		int hashCode = 17;
		hashCode = 31 * hashCode + _position.hashCode();
		hashCode = 31 * hashCode + _direction.hashCode();
		return hashCode;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof Move)) {
			return false;			
		} else {
			Move other = (Move) o;
			return _position.equals(other._position) && _direction == other._direction;
		}
	}
	
	@Override
	public String toString() {
        return "[" + _position + ", " + _direction + "]";
	}
}
