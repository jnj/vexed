package vexed;

import java.util.Map;
import java.util.HashMap;

public class CachingPositionSupplier implements PositionSupplier {

    private final Map<Integer, Map<Integer, Position>> _cache = new HashMap<>();

    @Override
    public Position getPosition(int row, int col) {
        if (_cache.containsKey(row)) {
            return getFromSubCache(row, col);
        } else {
            return addSubCache(row, col);
        }
    }

    private Position addSubCache(int row, int col) {
        Map<Integer, Position> subCache = new HashMap<>();
        Position position = new Position(col, row);
        subCache.put(col, position);
        _cache.put(row, subCache);
        return position;
    }

    private Position getFromSubCache(int row, int col) {
        Map<Integer, Position> subCache = _cache.get(row);
        if (subCache.containsKey(col)) {
            return subCache.get(col);
        } else {
            Position position = new Position(col, row);
            subCache.put(col, position);
            return position;
        }
    }
}
