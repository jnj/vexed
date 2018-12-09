package vexed;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * Solves using breadth-first search.
 */
public class BfsSolver implements Solver {

    private Queue<Board> _queue = new LinkedList<>();
    private Set<Board> _seenBoards = new HashSet<>();

    public Solution solve(Board initialBoard) {
        push(initialBoard);

        while (!_queue.isEmpty()) {
            Board board = pop();

            if (board.isSolved()) {
                return new Solution(board.getMoveHistory(), countSeenBoards());
            } else {
                if (!alreadySaw(board)) {
                    explore(board);
                }
            }
        }

        throw new UnsolveableBoardException("cannot solve board");
    }

    private int countSeenBoards() {
        return _seenBoards.size();
    }

    private void explore(Board board) {
        _seenBoards.add(board);
        for (Move move : board.getAvailableMoves()) {
            Board newBoard = board.apply(move);
            push(newBoard);
        }
    }

    private boolean alreadySaw(Board board) {
        return _seenBoards.contains(board);
    }

    private void push(Board board) {
        _queue.add(board);
    }

    private Board pop() {
        return _queue.poll();
    }
}
