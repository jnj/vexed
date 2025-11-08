package vexed;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamSolver implements Solver {
    private static final int CHARACTERISTICS = Spliterator.NONNULL | Spliterator.ORDERED;

    @Override
    public Solution solve(final Board initial) throws UnsolveableBoardException {
        final var seenBoards = new HashSet<Board>();
        for (var iter = List.of(initial).iterator(); iter.hasNext(); ) {
            final var board = iter.next();

            if (!seenBoards.contains(board)) {
                seenBoards.add(board);

                if (board.isSolved()) {
                    return new Solution(board.getMoveHistory(), seenBoards.size());
                }

                final var resulting = board.applyMoves();
                final var toExplore = resulting.iterator();
                iter = Stream.concat(toStream(iter), toStream(toExplore)).iterator();
            }
        }

        throw new UnsolveableBoardException(initial.toString());
    }

    private static <T> Stream<T> toStream(Iterator<T> iter) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iter, CHARACTERISTICS), false);
    }
}
