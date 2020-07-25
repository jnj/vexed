package vexed;

public class CachingPositionSupplier implements PositionSupplier {

    private Position[][] positions = new Position[10][10];

    CachingPositionSupplier() {
        populate();
    }

    @Override
    public Position getPosition(int row, int col) {
        if (row >= positions.length || col >= positions[row].length) {
            grow(row, col);
        }

        return positions[row][col];
    }

    private void populate() {
        for (var row = 0; row < positions.length; row++) {
            final var position = positions[row];

            for (var col = 0; col < position.length; col++) {
                position[col] = new Position(col, row);
            }
        }
    }

    private void grow(int desiredRow, int desiredCol) {
        final var rowCount = Math.max(desiredRow, positions.length);
        final var colCount = Math.max(positions[0].length, desiredCol);
        positions = new Position[rowCount][colCount];
        populate();
    }
}
