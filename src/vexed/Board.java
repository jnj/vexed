package vexed;

import java.util.Collection;

public interface Board {

    int getWidth();

    int getHeight();

    Block getBlockAt(Position position);

    Collection<Move> getAvailableMoves();

    boolean isSolved();

    Board apply(Move move);

    MoveHistory getMoveHistory();
}
