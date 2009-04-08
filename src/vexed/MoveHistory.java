package vexed;

import java.util.Collections;
import java.util.List;

public class MoveHistory {
	
	private final List<Move> _moves;
	
	public MoveHistory() {
		_moves = Containers.newLinkedList();
	}
	
    public MoveHistory(MoveHistory moveHistory) {
        this();
        addAll(moveHistory);
    }
    
	public void addAll(MoveHistory moveHistory) {
		_moves.addAll(moveHistory._moves);
	}
	
    public int size() {
        return _moves.size();
    }
    
	public void add(Move move) {
		_moves.add(move);
	}
	
	public List<Move> getMoves() {
		return Collections.unmodifiableList(_moves);
	}
    
    @Override
    public String toString() {
        return _moves.toString();
    }
}
