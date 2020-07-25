package vexed;

public enum Direction {

    Left {
        Position apply(Position position) {
            return new Position(position.getColumn() - 1, position.getRow());
        }
    },

    Right {
        Position apply(Position position) {
            return new Position(position.getColumn() + 1, position.getRow());
        }
    },

    Down {
        Position apply(Position position) {
            return new Position(position.getColumn(), position.getRow() + 1);
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
