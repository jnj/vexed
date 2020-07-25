package vexed;

import java.util.*;

import static vexed.Direction.*;

public class MapBoard implements Board {

    private final Map<Position, Block> _contents = new HashMap<>();
    private final MoveHistory _moveHistory = new MoveHistory();
    private final int _width;
    private final int _height;
    private final PositionSupplier _positionSupplier;

    MapBoard(int width, int height, Map<Position, Block> configuration, PositionSupplier positionSupplier) {
        _width = width;
        _height = height;
        _contents.putAll(configuration);
        _positionSupplier = positionSupplier;
    }

    private MapBoard(MapBoard board) {
        this(board._width, board._height, board._contents, board._positionSupplier);
        _moveHistory.addAll(board._moveHistory);
    }

    static MapBoard fromString(String layoutText, PositionSupplier positionSupplier) {
        var lines = layoutText.split("\n");
        var height = lines.length;
        var width = lines[0].length();

        Map<Position, Block> layout = new HashMap<>();

        for (var row = 0; row < lines.length; row++)
            for (var column = 0; column < lines[row].length(); column++) {
                var symbol = lines[row].charAt(column);
                if (symbol != '.' && symbol != ' ')
                    layout.put(positionSupplier.getPosition(row, column), new Block(symbol));
            }

        return new MapBoard(width, height, layout, positionSupplier);
    }

    public int getHeight() {
        return _height;
    }

    public int getWidth() {
        return _width;
    }

    public boolean isSolved() {
        return getOccupiedPositions().isEmpty();
    }

    public MoveHistory getMoveHistory() {
        return new MoveHistory(_moveHistory);
    }

    public Block getBlockAt(Position position) {
        return _contents.get(position);
    }

    public MapBoard apply(Move move) {
        if (!movePossible(move))
            throw new IllegalMoveException();
        var resultingBoard = copy();
        resultingBoard.doRecordedMove(move);
        resultingBoard.settleAndClear();
        return resultingBoard;
    }

    private boolean movePossible(Move move) {
        return withinBoardBounds(move.getPosition()) && moveableBlockAt(move.getPosition())
               && canPutBlockAt(move.getTargetPosition());
    }

    private void doRecordedMove(Move move) {
        recordMove(move);
        doMove(move);
    }

    private void recordMove(Move move) {
        _moveHistory.add(move);
    }

    private void doMove(Move move) {
        _contents.put(move.getTargetPosition(), _contents.get(move.getPosition()));
        _contents.remove(move.getPosition());
    }

    private void settleAndClear() {
        do {
            settleBlocks();
        } while (clearBlockGroups());
    }

    private void settleBlocks() {
        for (var position : getOccupiedPositions()) {
            var currentPosition = position;

            while (blockFallingFrom(currentPosition)) {
                var move = new Move(currentPosition, Down);
                doMove(move);
                currentPosition = move.getTargetPosition();
            }
        }
    }

    private boolean blockFallingFrom(Position position) {
        return moveableBlockAt(position) && canPutBlockAt(position.getNeighborTo(Down));
    }

    private boolean clearBlockGroups() {
        return clearPositions(findBlockGroups());
    }

    private boolean clearPositions(Collection<Position> positions) {
        for (var position : positions) {
            _contents.remove(position);
        }

        return !positions.isEmpty();
    }

    private Collection<Position> findBlockGroups() {
        var groups = new GroupContainer();
        var directionsToLook = new Direction[]{Down, Right};

        for (var position : getPositionsFromBottomUp()) {
            if (!moveableBlockAt(position))
                continue;

            for (var direction : directionsToLook) {
                var neighboringPosition = position.getNeighborTo(direction);
                if (equalBlocksAt(position, neighboringPosition))
                    groups.addToGroup(position, groups.getGroup(neighboringPosition));
            }

            if (!groups.hasGroup(position))
                groups.addToGroup(position, groups.getNextAvailableGroup());
        }

        return groups.getMembersOfNonSingletonGroups();
    }

    public Collection<Move> getAvailableMoves() {
        Collection<Move> moves = new HashSet<>();
        var directions = new Direction[]{Left, Right};

        for (var position : getOccupiedPositions())
            for (var direction : directions)
                if (canPutBlockAt(position.getNeighborTo(direction)))
                    moves.add(new Move(position, direction));

        return moves;
    }

    private Collection<Position> getOccupiedPositions() {
        var positions = getPositionsFromBottomUp();
        var list = new ArrayList<Position>();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = positions.size(); i < size; i++) {
            var position = positions.get(i);
            if (moveableBlockAt(position)) {
                list.add(position);
            }
        }

        return list;
    }

    private List<Position> getPositionsFromBottomUp() {
        List<Position> positions = new ArrayList<>(_width * _height);

        for (var row = _height - 1; row >= 0; row--)
            for (var column = _width - 1; column >= 0; column--)
                positions.add(_positionSupplier.getPosition(row, column));

        return positions;
    }

    private boolean moveableBlockAt(Position position) {
        return _contents.containsKey(position) && !_contents.get(position).isWall();
    }

    private boolean canPutBlockAt(Position position) {
        return withinBoardBounds(position) && !isOccupied(position);
    }

    private boolean withinBoardBounds(Position position) {
        return position.getColumn() >= 0 && position.getColumn() < _width && position.getRow() >= 0
               && position.getRow() < _height;
    }

    private boolean equalBlocksAt(Position first, Position second) {
        return _contents.containsKey(first) && _contents.containsKey(second)
               && _contents.get(first).equals(_contents.get(second));
    }

    private boolean isOccupied(Position position) {
        return _contents.containsKey(position);
    }

    private MapBoard copy() {
        return new MapBoard(this);
    }

    private Iterable<Position> positions() {
        return new PositionSequence(_width, _height, _positionSupplier);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        else if (!(o instanceof Board))
            return false;
        else {
            var other = (Board) o;

            if (getWidth() != other.getWidth() || getHeight() != other.getHeight())
                return false;

            for (var position : positions()) {
                var thisBlock = getBlockAt(position);
                var thatBlock = other.getBlockAt(position);

                if (!java.util.Objects.equals(thisBlock, thatBlock))
                    return false;
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        var factor = 31;
        var hashCode = 17;
        hashCode = factor * hashCode + _width;
        hashCode = factor * hashCode + _height;

        for (var position : positions()) {
            var block = getBlockAt(position);
            hashCode = factor * hashCode + java.util.Objects.hash(block);
        }

        return hashCode;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();

        for (var position : positions()) {
            var block = getBlockAt(position);
            builder.append(block == null ? "." : block.symbol());

            if (position.getColumn() == _width - 1)
                builder.append("\n");
        }

        return builder.toString();
    }

    private static class GroupContainer {
        Integer currentGroup = 0;

        Map<Position, Integer> _positionsMappedToGroups = new HashMap<>();

        Map<Integer, Collection<Position>> _groupsMappedToPositions = new HashMap<>();

        Integer getNextAvailableGroup() {
            return ++currentGroup;
        }

        boolean hasGroup(Position position) {
            return _positionsMappedToGroups.containsKey(position);
        }

        Integer getGroup(Position position) {
            return _positionsMappedToGroups.get(position);
        }

        void addToGroup(Position position, Integer group) {
            _positionsMappedToGroups.put(position, group);

            if (!_groupsMappedToPositions.containsKey(group)) {
                _groupsMappedToPositions.put(group, new HashSet<>());
            }

            _groupsMappedToPositions.get(group).add(position);
        }

        Collection<Position> getMembersOfNonSingletonGroups() {
            Collection<Position> positions = new HashSet<>();

            for (var group : _groupsMappedToPositions.values())
                if (group.size() > 1)
                    positions.addAll(group);

            return positions;
        }
    }

    private static class PositionSequence implements Iterable<Position> {
        private final int _width;
        private final int _height;
        private final PositionSupplier _positionSupplier;

        PositionSequence(int width, int height, PositionSupplier positionSupplier) {
            _width = width;
            _height = height;
            _positionSupplier = positionSupplier;
        }

        public Iterator<Position> iterator() {
            return new Iterator<Position>() {
                int row;

                int column;

                public Position next() {
                    var position = _positionSupplier.getPosition(row, column);

                    if (column == _width - 1) {
                        column = 0;
                        row++;
                    } else {
                        column++;
                    }

                    return position;
                }

                public boolean hasNext() {
                    return row < _height && column < _width;
                }

                public void remove() {
                }
            };
        }
    }

    static class Builder {

        private final StringBuilder _layoutBuilder = new StringBuilder();

        private final int _interiorWidth;

        Builder(int interiorWidth) {
            _interiorWidth = interiorWidth;
        }

        void addInteriorRow(String rowText) {
            if (rowText.length() != _interiorWidth)
                throw new IllegalArgumentException("text length should be " + _interiorWidth);
            addWall();
            _layoutBuilder.append(rowText);
            addWall();
            endRow();
        }

        private void addWall() {
            _layoutBuilder.append(Block.WALL_SYMBOL);
        }

        private void endRow() {
            _layoutBuilder.append("\n");
        }

        private void addBottomWall() {
            for (var i = 0; i < _interiorWidth + 2; i++)
                addWall();
        }

        MapBoard build(PositionSupplier positionSupplier) {
            addBottomWall();
            return MapBoard.fromString(_layoutBuilder.toString(), positionSupplier);
        }
    }
}
