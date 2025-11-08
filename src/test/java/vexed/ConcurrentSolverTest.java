package vexed;

public class ConcurrentSolverTest extends SolverTest {
    @Override
    Solver getSolverInstance() {
        return new ConcurrentSolver();
    }
}
