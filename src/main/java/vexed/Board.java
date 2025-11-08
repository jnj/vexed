package vexed;

import java.util.Collection;
import java.util.stream.Stream;

public interface Board {

    int getWidth();

    int getHeight();

    Block getBlockAt(Position position);

    Collection<Move> getAvailableMoves();

    boolean isSolved();

    Stream<Board> applyMoves();

    Board apply(Move move);

    MoveHistory getMoveHistory();
}
