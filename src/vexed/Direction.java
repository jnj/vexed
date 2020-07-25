package vexed;

public enum Direction {

    Left {
        Position apply(Position position) {
            return new Position(position.column() - 1, position.row());
        }
    },

    Right {
        Position apply(Position position) {
            return new Position(position.column() + 1, position.row());
        }
    },

    Down {
        Position apply(Position position) {
            return new Position(position.column(), position.row() + 1);
        }
    };

    abstract Position apply(Position position);

    @Override
    public String toString() {
        return switch (this) {
            case Left -> "L";
            case Right -> "R";
            case Down -> "D";
        };
    }
}
