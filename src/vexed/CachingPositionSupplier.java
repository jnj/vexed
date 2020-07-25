package vexed;

import java.util.HashMap;
import java.util.Map;

public class CachingPositionSupplier implements PositionSupplier {

    private final Map<Integer, Map<Integer, Position>> cache = new HashMap<>();

    @Override
    public Position getPosition(int row, int col) {
        final var rowCache = cache.computeIfAbsent(row, r -> new HashMap<>());
        return rowCache.computeIfAbsent(col, c -> new Position(col, row));
    }
}
