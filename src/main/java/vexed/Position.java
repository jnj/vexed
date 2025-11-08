package vexed;

public record Position(int column, int row) {

    Position getNeighborTo(Direction direction, PositionSupplier supplier) {
        return direction.apply(this, supplier);
    }
}
