package vexed;

import java.util.EnumMap;
import java.util.Map;

public class MoveCache {
    private final Map<Direction, Move[][]> byDirection = new EnumMap<>(Direction.class);

    public MoveCache(int dimension) {
        for (final var dir : Direction.values()) {
            final var moves = new Move[dimension][dimension];
            for (var i = 0; i < dimension; i++) {
                for (var j = 0; j < dimension; j++) {
                    moves[i][j] = new Move(j, i, dir);
                }
            }
            byDirection.put(dir, moves);
        }
    }

    public Move moveFor(int row, int col, Direction dir) {
        return byDirection.get(dir)[row][col];
    }
}
