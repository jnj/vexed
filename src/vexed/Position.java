package vexed;

public record Position(int column, int row) {

    Position getNeighborTo(Direction direction) {
        return direction.apply(this);
    }
}
