package vexed;

import static vexed.Direction.*;

import java.util.*;
import java.util.stream.Collectors;

public class MapBoard implements Board {

    private final Map<Position, Block> _contents = new HashMap<>();
    private final MoveHistory _moveHistory = new MoveHistory();
    private final int _width;
    private final int _height;
    private final PositionSupplier _positionSupplier;

    public static MapBoard fromString(String layoutText, PositionSupplier positionSupplier) {
        String[] lines = layoutText.split("\n");
        int height = lines.length;
        int width = lines[0].length();

        Map<Position, Block> layout = new HashMap<>();

        for (int row = 0; row < lines.length; row++)
            for (int column = 0; column < lines[row].length(); column++) {
                char symbol = lines[row].charAt(column);
                if (symbol != '.' && symbol != ' ')
                    layout.put(positionSupplier.getPosition(row, column), new Block(symbol));
            }

        return new MapBoard(width, height, layout, positionSupplier);
    }

    public MapBoard(int width, int height, Map<Position, Block> configuration, PositionSupplier positionSupplier) {
        _width = width;
        _height = height;
        _contents.putAll(configuration);
        _positionSupplier = positionSupplier;
    }

    private MapBoard(MapBoard board) {
        this(board._width, board._height, board._contents, board._positionSupplier);
        _moveHistory.addAll(board._moveHistory);
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
        MapBoard resultingBoard = copy();
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
        for (Position position : getOccupiedPositions()) {
            Position currentPosition = position;

            while (blockFallingFrom(currentPosition)) {
                Move move = new Move(currentPosition, Down);
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
        for (Position position : positions) {
            _contents.remove(position);
        }

        return !positions.isEmpty();
    }

    private Collection<Position> findBlockGroups() {
        GroupContainer groups = new GroupContainer();
        Direction[] directionsToLook = new Direction[]{Down, Right};

        for (Position position : getPositionsFromBottomUp()) {
            if (!moveableBlockAt(position))
                continue;

            for (Direction direction : directionsToLook) {
                Position neighboringPosition = position.getNeighborTo(direction);
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
        Direction[] directions = new Direction[]{Left, Right};

        for (Position position : getOccupiedPositions())
            for (Direction direction : directions)
                if (canPutBlockAt(position.getNeighborTo(direction)))
                    moves.add(new Move(position, direction));

        return moves;
    }

    private Collection<Position> getOccupiedPositions() {
        return getPositionsFromBottomUp().stream()
                .filter(this::moveableBlockAt)
                .collect(Collectors.toList());
    }

    private List<Position> getPositionsFromBottomUp() {
        List<Position> positions = new ArrayList<>(_width * _height);

        for (int row = _height - 1; row >= 0; row--)
            for (int column = _width - 1; column >= 0; column--)
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
            Board other = (Board) o;

            if (getWidth() != other.getWidth() || getHeight() != other.getHeight())
                return false;

            for (Position position : positions()) {
                Block thisBlock = getBlockAt(position);
                Block thatBlock = other.getBlockAt(position);

                if (!Objects.areEqual(thisBlock, thatBlock))
                    return false;
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        int factor = 31;
        int hashCode = 17;
        hashCode = factor * hashCode + _width;
        hashCode = factor * hashCode + _height;

        for (Position position : positions()) {
            Block block = getBlockAt(position);
            hashCode = factor * hashCode + Objects.hash(block);
        }

        return hashCode;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        for (Position position : positions()) {
            Block block = getBlockAt(position);
            builder.append(block == null ? "." : block.getSymbol());

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

            for (Collection<Position> group : _groupsMappedToPositions.values())
                if (group.size() > 1)
                    positions.addAll(group);

            return positions;
        }
    }

    private static class PositionSequence implements Iterable<Position> {
        private final int _width;
        private final int _height;
        private final PositionSupplier _positionSupplier;

        public PositionSequence(int width, int height, PositionSupplier positionSupplier) {
            _width = width;
            _height = height;
            _positionSupplier = positionSupplier;
        }

        public Iterator<Position> iterator() {
            return new Iterator<Position>() {
                int row;

                int column;

                public Position next() {
                    Position position = _positionSupplier.getPosition(row, column);

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

    public static class Builder {

        private final StringBuilder _layoutBuilder = new StringBuilder();

        private final int _interiorWidth;

        public Builder(int interiorWidth) {
            _interiorWidth = interiorWidth;
        }

        public Builder addInteriorRow(String rowText) {
            if (rowText.length() != _interiorWidth)
                throw new IllegalArgumentException("text length should be " + _interiorWidth);
            addWall();
            _layoutBuilder.append(rowText);
            addWall();
            endRow();
            return this;
        }

        private void addWall() {
            _layoutBuilder.append(Block.WALL_SYMBOL);
        }

        private void endRow() {
            _layoutBuilder.append("\n");
        }

        private void addBottomWall() {
            for (int i = 0; i < _interiorWidth + 2; i++)
                addWall();
        }

        public MapBoard build(PositionSupplier positionSupplier) {
            addBottomWall();
            return MapBoard.fromString(_layoutBuilder.toString(), positionSupplier);
        }
    }
}
