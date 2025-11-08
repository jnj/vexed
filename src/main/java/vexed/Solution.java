package vexed;

public class Solution {

    private final MoveHistory moveHistory;
    private final int numBoards;

    Solution(final MoveHistory moveHistory, final int numBoards) {
        this.moveHistory = moveHistory;
        this.numBoards = numBoards;
    }

    MoveHistory getMoveHistory() {
        return moveHistory;
    }

    @Override
    public String toString() {
        return "Winning moves: " + moveHistory + "\nnumber of boards: " + numBoards;
    }
}
