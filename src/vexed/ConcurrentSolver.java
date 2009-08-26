package vexed;

import java.util.concurrent.*;
import java.util.*;

public class ConcurrentSolver implements Solver {

    private final ExecutorService _executorService = Executors.newFixedThreadPool(64);
    private final Queue<Collection<Board>> _queue = new LinkedBlockingQueue<Collection<Board>>();
    private final Set<Board> _seenBoards = Collections.synchronizedSet(new HashSet<Board>()); 

    @Override
    public Solution solve(Board rootBoard) throws UnsolveableBoardException {
        _queue.add(Collections.singleton(rootBoard));

        while (!_queue.isEmpty()) {
            Collection<Board> explorationLevel = _queue.poll();
            Collection<Future<Collection<Board>>> futures = new ArrayList<Future<Collection<Board>>>();
            
            for (Board board : explorationLevel) {
                if (board.isSolved()) {
                    _executorService.shutdownNow();
                    return new Solution(board.getMoveHistory(), _seenBoards.size());
                } else if (!_seenBoards.contains(board)) {
                    _seenBoards.add(board);
                    BoardExploreTask task = new BoardExploreTask(board);
                    futures.add(_executorService.submit(task));                    
                }
            }

            Collection<Board> nextLevel = mergeResultingBoards(futures);
            if (nextLevel.isEmpty()) {
                break;
            }
            _queue.add(nextLevel);
        }

        throw new UnsolveableBoardException();
    }

    private Collection<Board> mergeResultingBoards(Collection<Future<Collection<Board>>> futures) {
        // merge results
        Collection<Board> nextLevel = new ArrayList<Board>();
        for (Future<Collection<Board>> future : futures) {
            try {
                nextLevel.addAll(future.get());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
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
        public Collection<Board> call() throws Exception {            
            Collection<Move> moves = parentBoard.getAvailableMoves();
            Collection<Board> boards = new ArrayList<Board>();

            for (Move move : moves) {
                boards.add(parentBoard.apply(move));
            }

            return boards;
        }
    }
}


