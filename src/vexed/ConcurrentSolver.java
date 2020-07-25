package vexed;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class ConcurrentSolver implements Solver {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final Queue<Collection<Board>> queue = new LinkedBlockingQueue<>();
    private final Set<Board> seenBoards = Collections.synchronizedSet(new HashSet<>());

    @Override
    public Solution solve(Board rootBoard) throws UnsolveableBoardException {
        queue.add(Collections.singleton(rootBoard));

        while (!queue.isEmpty()) {
            final var explorationLevel = queue.poll();
            Collection<Future<Collection<Board>>> futures = new ArrayList<>();

            for (var board : explorationLevel) {
                if (board.isSolved()) {
                    executorService.shutdownNow();
                    return new Solution(board.getMoveHistory(), seenBoards.size());
                } else if (!seenBoards.contains(board)) {
                    seenBoards.add(board);
                    final var task = new BoardExploreTask(board);
                    futures.add(executorService.submit(task));
                }
            }

            final var nextLevel = mergeResultingBoards(futures);

            if (nextLevel.isEmpty()) {
                break;
            }

            queue.add(nextLevel);
        }

        throw new UnsolveableBoardException();
    }

    private Collection<Board> mergeResultingBoards(Collection<Future<Collection<Board>>> futures) {
        // merge results
        Collection<Board> nextLevel = new ArrayList<>();

        for (var future : futures) {
            try {
                nextLevel.addAll(future.get());
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        return nextLevel;
    }

    /**
     * Gets all possible moves from one board, applies them
     */
    private static class BoardExploreTask implements Callable<Collection<Board>> {
        private final Board parentBoard;

        private BoardExploreTask(Board parentBoard) {
            this.parentBoard = parentBoard;
        }

        @Override
        public Collection<Board> call() {
            return parentBoard.getAvailableMoves().stream().map(parentBoard::apply).collect(Collectors.toList());
        }
    }
}


