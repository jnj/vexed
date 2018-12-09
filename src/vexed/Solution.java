package vexed;

public class Solution {

    private final MoveHistory _moveHistory;
    private final int _numBoards;

    Solution(final MoveHistory moveHistory, final int numBoards) {
        _moveHistory = moveHistory;
        _numBoards = numBoards;
    }

    MoveHistory getMoveHistory() {
        return _moveHistory;
    }

    @Override
    public String toString() {
        return "Winning moves: " + _moveHistory + "\nnumber of boards: " + _numBoards;
    }
}
