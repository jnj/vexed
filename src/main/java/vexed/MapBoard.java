package vexed;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static vexed.Direction.*;

public class MapBoard implements Board {
    private static final List<Direction> DIRECTION_LIST = List.of(Left, Right);
    private static final Direction[] DIRECTIONS = {Left, Right};

    private final Map<Position, Block> contents = new HashMap<>();
    private final MoveHistory moveHistory = new MoveHistory();
    private final PositionSupplier positionSupplier;
    private final MoveCache moveCache;
    private final int width;
    private final int height;

    MapBoard(int width, int height, Map<Position, Block> configuration, PositionSupplier positionSupplier, MoveCache moveCache) {
        this.width = width;
        this.height = height;
        this.positionSupplier = positionSupplier;
        this.moveCache = moveCache;
        contents.putAll(configuration);
    }

    private MapBoard(MapBoard board) {
        this(board.width, board.height, board.contents, board.positionSupplier, board.moveCache);
        moveHistory.addAll(board.moveHistory);
    }

    static MapBoard fromString(String layoutText, PositionSupplier positionSupplier, BlockCache blockCache, MoveCache moveCache) {
        final var lines = layoutText.split("\n");
        final var height = lines.length;
        final var width = lines[0].length();
        final Map<Position, Block> layout = new HashMap<>();

        for (var row = 0; row < lines.length; row++) {
            for (var column = 0; column < lines[row].length(); column++) {
                final var symbol = lines[row].charAt(column);
                if (symbol != '.' && symbol != ' ') {
                    layout.put(positionSupplier.getPosition(row, column), blockCache.blockFor(symbol));
                }
            }
        }

        return new MapBoard(width, height, layout, positionSupplier, moveCache);
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public boolean isSolved() {
        return getOccupiedPositions().isEmpty();
    }

    @Override
    public MoveHistory getMoveHistory() {
        return new MoveHistory(moveHistory);
    }

    @Override
    public Block getBlockAt(Position position) {
        return contents.get(position);
    }

    @Override
    public MapBoard apply(Move move) {
        if (!movePossible(move)) {
            throw new IllegalMoveException();
        }
        final var resultingBoard = copy();
        resultingBoard.doRecordedMove(move);
        resultingBoard.settleAndClear();
        return resultingBoard;
    }

    @Override
    public Collection<Move> getAvailableMoves() {
        final Collection<Move> moves = new HashSet<>();

        for (final var position : getOccupiedPositions()) {
            for (final var direction : DIRECTIONS) {
                if (canPutBlockAt(position.getNeighborTo(direction, positionSupplier))) {
                    moves.add(new Move(position, direction));
                }
            }
        }

        return moves;
    }

    @Override
    public Stream<Board> applyMoves() {
        return availableMovesStream().map(this::apply);
    }

    private Stream<Move> availableMovesStream() {
        return getOccupiedPositionsStream().flatMap(pos ->
                DIRECTION_LIST.stream()
                        .filter(dir -> canPutBlockAt(pos.getNeighborTo(dir, positionSupplier)))
                        .map(dir -> new Move(pos, dir)));
    }

    private boolean movePossible(Move move) {
        return withinBoardBounds(move.position()) && moveableBlockAt(move.position())
               && canPutBlockAt(move.getTargetPosition(positionSupplier));
    }

    private void doRecordedMove(Move move) {
        recordMove(move);
        doMove(move);
    }

    private void recordMove(Move move) {
        moveHistory.add(move);
    }

    private void doMove(Move move) {
        contents.put(move.getTargetPosition(positionSupplier), contents.get(move.position()));
        contents.remove(move.position());
    }

    private void settleAndClear() {
        do {
            settleBlocks();
        } while (clearBlockGroups());
    }

    private void settleBlocks() {
        for (final var position : getOccupiedPositions()) {
            var currentPosition = position;

            while (blockFallingFrom(currentPosition)) {
                final var move = new Move(currentPosition, Down);
                doMove(move);
                currentPosition = move.getTargetPosition(positionSupplier);
            }
        }
    }

    private boolean blockFallingFrom(Position position) {
        return moveableBlockAt(position) && canPutBlockAt(position.getNeighborTo(Down, positionSupplier));
    }

    private boolean clearBlockGroups() {
        return clearPositions(findBlockGroups());
    }

    private boolean clearPositions(Collection<Position> positions) {
        for (final var position : positions) {
            contents.remove(position);
        }

        return !positions.isEmpty();
    }

    private Collection<Position> findBlockGroups() {
        final var groups = new GroupContainer();
        final var directionsToLook = new Direction[]{Down, Right};

        for (final var position : getPositionsFromBottomUp()) {
            if (!moveableBlockAt(position)) {
                continue;
            }

            for (final var direction : directionsToLook) {
                final var neighboringPosition = position.getNeighborTo(direction, positionSupplier);
                if (equalBlocksAt(position, neighboringPosition)) {
                    groups.addToGroup(position, groups.getGroup(neighboringPosition));
                }
            }

            if (!groups.hasGroup(position)) {
                groups.addToGroup(position, groups.getNextAvailableGroup());
            }
        }

        return groups.getMembersOfNonSingletonGroups();
    }

    private Collection<Position> getOccupiedPositions() {
        final var positions = getPositionsFromBottomUp();
        final var list = new ArrayList<Position>();

        //noinspection ForLoopReplaceableByForEach
        for (int i = 0, size = positions.size(); i < size; i++) {
            final var position = positions.get(i);
            if (moveableBlockAt(position)) {
                list.add(position);
            }
        }

        return list;
    }

    private Stream<Position> getOccupiedPositionsStream() {
        return getPositionsFromBottomUpStream().filter(this::moveableBlockAt);
    }

    private List<Position> getPositionsFromBottomUp() {
        final var positions = new ArrayList<Position>(width * height);

        for (var row = height - 1; row >= 0; row--) {
            for (var column = width - 1; column >= 0; column--) {
                positions.add(positionSupplier.getPosition(row, column));
            }
        }

        return positions;
    }

    private Stream<Position> getPositionsFromBottomUpStream() {
        final var iter = new Iterator<Position>() {
            int row = height - 1;
            int col = width - 1;

            @Override
            public boolean hasNext() {
                return row >= 0 && col >= 0;
            }

            @Override
            public Position next() {
                final var pos = positionSupplier.getPosition(row, col);
                if (--col < 0) {
                    row--;
                    col = width - 1;
                }
                return pos;
            }
        };

        final var spliter = Spliterators.spliteratorUnknownSize(iter, Spliterator.ORDERED);
        return StreamSupport.stream(spliter, false);
    }

    private boolean moveableBlockAt(Position position) {
        return contents.containsKey(position) && !contents.get(position).isWall();
    }

    private boolean canPutBlockAt(Position position) {
        return withinBoardBounds(position) && !isOccupied(position);
    }

    private boolean withinBoardBounds(Position position) {
        return position.column() >= 0 && position.column() < width && position.row() >= 0
               && position.row() < height;
    }

    private boolean equalBlocksAt(Position first, Position second) {
        return contents.containsKey(first) && contents.containsKey(second)
               && contents.get(first).equals(contents.get(second));
    }

    private boolean isOccupied(Position position) {
        return contents.containsKey(position);
    }

    private MapBoard copy() {
        return new MapBoard(this);
    }

    private Iterable<Position> positions() {
        return new PositionSequence(width, height, positionSupplier);
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof Board other)) {
            return false;
        } else {
            if (getWidth() != other.getWidth() || getHeight() != other.getHeight()) {
                return false;
            }

            for (final var position : positions()) {
                final var thisBlock = getBlockAt(position);
                final var thatBlock = other.getBlockAt(position);

                if (!Objects.equals(thisBlock, thatBlock)) {
                    return false;
                }
            }

            return true;
        }
    }

    @Override
    public int hashCode() {
        final var factor = 31;
        var hashCode = 17;
        hashCode = factor * hashCode + width;
        hashCode = factor * hashCode + height;

        for (final var position : getOccupiedPositions()) {
            final var block = getBlockAt(position);
            hashCode = factor * hashCode + Objects.hash(block);
        }

        return hashCode;
    }

    @Override
    public String toString() {
        final var builder = new StringBuilder();

        for (final var position : positions()) {
            final var block = getBlockAt(position);
            builder.append(block == null ? "." : block.symbol());

            if (position.column() == width - 1)
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
            final Collection<Position> positions = new HashSet<>();

            for (final var group : _groupsMappedToPositions.values())
                if (group.size() > 1)
                    positions.addAll(group);

            return positions;
        }
    }

    private record PositionSequence(int width, int height, PositionSupplier positionSupplier)
            implements Iterable<Position> {

        public Iterator<Position> iterator() {
                return new Iterator<>() {
                    int row;
                    int column;

                    public Position next() {
                        final var position = positionSupplier.getPosition(row, column);

                        if (column == width - 1) {
                            column = 0;
                            row++;
                        } else {
                            column++;
                        }

                        return position;
                    }

                    public boolean hasNext() {
                        return row < height && column < width;
                    }

                    public void remove() {
                    }
                };
            }
        }

    static class Builder {
        private final StringBuilder layoutBuilder = new StringBuilder();
        private final int interiorWidth;

        Builder(int interiorWidth) {
            this.interiorWidth = interiorWidth;
        }

        void addInteriorRow(String rowText) {
            if (rowText.length() != interiorWidth)
                throw new IllegalArgumentException("text length should be " + interiorWidth);
            addWall();
            layoutBuilder.append(rowText);
            addWall();
            endRow();
        }

        private void addWall() {
            layoutBuilder.append(Block.WALL_SYMBOL);
        }

        private void endRow() {
            layoutBuilder.append("\n");
        }

        private void addBottomWall() {
            for (var i = 0; i < interiorWidth + 2; i++)
                addWall();
        }

        MapBoard build(PositionSupplier positionSupplier, BlockCache blockCache, MoveCache moveCache) {
            addBottomWall();
            return MapBoard.fromString(layoutBuilder.toString(), positionSupplier, blockCache, moveCache);
        }
    }
}
