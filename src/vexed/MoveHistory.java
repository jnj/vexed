package vexed;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MoveHistory {

    private final List<Move> _moves;

    MoveHistory() {
        _moves = new LinkedList<>();
    }

    MoveHistory(MoveHistory moveHistory) {
        this();
        addAll(moveHistory);
    }

    void addAll(MoveHistory moveHistory) {
        _moves.addAll(moveHistory._moves);
    }

    int size() {
        return _moves.size();
    }

    void add(Move move) {
        _moves.add(move);
    }

    List<Move> getMoves() {
        return Collections.unmodifiableList(_moves);
    }

    @Override
    public String toString() {
        return _moves.toString();
    }
}
