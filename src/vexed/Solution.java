package vexed;

public class Solution {

    private final MoveHistory _moveHistory;
    private final int _numBoards;
    
    public Solution(final MoveHistory moveHistory, final int numBoards) {
        _moveHistory = moveHistory;
        _numBoards = numBoards;
    }    
    
    public MoveHistory getMoveHistory() {
        return _moveHistory;
    }

    public int getNumBoards() {
        return _numBoards;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Winning moves: ").append(_moveHistory);
        builder.append("\nnumber of boards: ").append(_numBoards);
        return builder.toString();
    }   
}
