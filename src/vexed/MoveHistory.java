package vexed;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MoveHistory {

    private final List<Move> moves;

    MoveHistory() {
        moves = new LinkedList<>();
    }

    MoveHistory(MoveHistory moveHistory) {
        this();
        addAll(moveHistory);
    }

    void addAll(MoveHistory moveHistory) {
        moves.addAll(moveHistory.moves);
    }

    int size() {
        return moves.size();
    }

    void add(Move move) {
        moves.add(move);
    }

    List<Move> getMoves() {
        return Collections.unmodifiableList(moves);
    }

    @Override
    public String toString() {
        return moves.toString();
    }
}
