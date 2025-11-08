package vexed;

public record Move(Position position, Direction direction) {

    public Move(int column, int row, Direction direction) {
        this(new Position(column, row), direction);
    }

    Position getTargetPosition(PositionSupplier supplier) {
        return position.getNeighborTo(direction, supplier);
    }

    @Override
    public String toString() {
        return "[" + position.row() + ", " + position.column() + ", " + direction + "]";
    }
}
