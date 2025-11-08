package vexed;

public enum Direction {

    Left(-1, 0),
    Right(1, 0),
    Down(0, 1)
    ;

    private final int colDelta;
    private final int rowDelta;

    Direction(int colDelta, int rowDelta) {
        this.colDelta = colDelta;
        this.rowDelta = rowDelta;
    }

    public Position apply(Position position, PositionSupplier supplier) {
        return supplier.getPosition(position.row() + rowDelta, position.column() + colDelta);
    }

    @Override
    public String toString() {
        return switch (this) {
            case Left -> "L";
            case Right -> "R";
            case Down -> "D";
        };
    }
}
