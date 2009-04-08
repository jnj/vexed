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
        // TODO add String code
        switch (this) {
        case Left:
            return "L";
        case Right:
            return "R";
        case Down:
            return "D";
        default:
            return "?";
        }
    }
}
