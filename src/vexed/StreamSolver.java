package vexed;

import java.util.HashSet;
import java.util.Iterator;
import java.util.stream.Stream;

public class StreamSolver implements Solver {

    @Override
    public Solution solve(Board initial) throws UnsolveableBoardException {
        final var seenBoards = new HashSet<Board>();
        var iterator = Stream.of(initial).iterator();

        while (iterator.hasNext()) {
            var board = iterator.next();

            if (!seenBoards.contains(board)) {
                seenBoards.add(board);

                if (board.isSolved()) {
                    return new Solution(board.getMoveHistory(), seenBoards.size());
                }

                var resulting = board.applyMoves();
                var toExplore = resulting.iterator();
                iterator = concat(iterator, toExplore);
            }
        }

        throw new UnsolveableBoardException(initial.toString());
    }

    private static <T> Iterator<T> concat(Iterator<T> first, Iterator<T> second) {
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return first.hasNext() || second.hasNext();
            }

            @Override
            public T next() {
                if (first.hasNext()) {
                    return first.next();
                } else {
                    return second.next();
                }
            }
        };
    }
}
