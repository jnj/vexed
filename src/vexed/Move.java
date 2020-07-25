package vexed;

public record Move(Position position, Direction direction) {

    public Move(int column, int row, Direction direction) {
        this(new Position(column, row), direction);
    }

    Position getTargetPosition() {
        return position.getNeighborTo(direction);
    }
}
