package Assignment2.InefficientPruningButClean;

import java.util.Queue;

/**
 * @projectName FoAI-Assignments
 * @fileName MinimaxSearch
 * @auther Qiaoyi Yin
 * @time 2019-10-18 17:27
 * @function Minimax Search with alpha-beta pruning
 */

public class MinimaxSearch {
    // Mode of Search
    private SearchMode mode;

    // Me
    private Player playerMax;
    private boolean isMeInside;

    // Opponent
    private Player playerMin;
    private boolean isOpponentInside;

    // Board
    private Board board;

    // Final action
    private Action optimalAct;

    // Depth limitation
    private int limitOfDepth;
    // Beginning time of the minimax Search
    private double beginOfSearch;
    // Maximum time limit
    private double limitOfTime;

    // Default construction
    public MinimaxSearch(SearchMode mode, Player playerMax, Player playerMin, Board board,
                         double limitOfTime, int depth) {
        this.mode = mode;
        this.playerMax = playerMax;
        this.playerMin = playerMin;
        this.board = board;
        this.limitOfTime = limitOfTime;

        this.isMeInside = this.playerMax.isSomePiecesInOwnCamp();
        this.isOpponentInside = this.playerMin.isSomePiecesInOwnCamp();

        /**
         * TODO: Depth
         */
        this.limitOfDepth = depth;

        this.optimalAct = null;
        this.beginOfSearch = System.currentTimeMillis();
    }

    public void alphaBetaSearch() {
        this.maxTurn(0, Integer.MIN_VALUE, Integer.MAX_VALUE, this.evaluate());
    }

    private double maxTurn(int depth, double alpha, double beta, double eval) {
        if (depth == this.limitOfDepth || this.isTerminal()) {
            return eval;
        }

        Queue<Action> actions;
        // Inside camp
        if (this.isMeInside) {
            actions = this.playerMax.generateInsideCampActions(this.board);
            if (actions.isEmpty()) {
                // No valid move inside camp, so Outside Camp
                actions = this.playerMax.generateOutsideCampActions(this.board);
            }
        }
        else {
            actions = this.playerMax.generateOutsideCampActions(this.board);
        }

        double estimated = Integer.MIN_VALUE;
        Action act = null;
        while ((act = actions.poll()) != null) {
            // Update pieces and then update the board
            int deltaMeInOpposite = this.doAction(this.playerMax, act, this.board);

            // Count delta evaluation
            double delta = this.playerMax.countDelta(act, deltaMeInOpposite, depth);

            // Obtain heuristic evaluation
            estimated = this.maxValue(estimated, this.minTurn(depth+1, alpha, beta, eval+delta), act, depth);

            // Restore the pieces and then restore the board
            this.undoAction(this.playerMax, act, this.board);

            // Pruning
            if (estimated >= beta) {
                return estimated;
            }

            alpha = Math.max(estimated, alpha);
        }

        return estimated;
    }

    private double maxValue(double oldVal, double newVal, Action action, int depth) {
        if (oldVal < newVal) {
            // Root node can update the final action
            if (depth == 0) {
                this.optimalAct = action;
            }

            return newVal;
        }

        return oldVal;
    }

    private double minTurn(int depth, double alpha, double beta, double eval) {
        if (depth == this.limitOfDepth || this.isTerminal()) {
            return eval;
        }

        Queue<Action> actions;
        // Inside camp
        if (this.isOpponentInside) {
            actions = this.playerMin.generateInsideCampActions(this.board);
            if (actions.isEmpty()) {
                // No valid move inside camp, so Outside Camp
                actions = this.playerMin.generateOutsideCampActions(this.board);
            }
        }
        else {
            actions = this.playerMin.generateOutsideCampActions(this.board);
        }

        double estimated = Integer.MAX_VALUE;
        Action act = null;
        while ((act = actions.poll()) != null) {
            // Update pieces and then update the board
            int deltaMeInOpposite = this.doAction(this.playerMin, act, this.board);

            // Count delta evaluation
            double delta = this.playerMin.countDelta(act, deltaMeInOpposite, depth);

            // Obtain heuristic evaluation
            estimated = Math.min(estimated, this.maxTurn(depth+1, alpha, beta, eval-delta));

            // Restore the pieces and then restore the board
            this.undoAction(this.playerMin, act, this.board);

            // Pruning
            if (estimated <= alpha) {
                return estimated;
            }

            beta = Math.min(estimated, beta);
        }

        return estimated;
    }

    private double evaluate() {
        int value4Me = this.playerMax.evaluate();
        int value4Opponent = this.playerMin.evaluate();

        return (value4Me-value4Opponent);
    }

    // Slow terminal Assignment2.test
    private boolean isEnded() {
        // Either one wins
        return (this.playerMin.isOppositeCampOccupied(this.board)
                || this.playerMax.isOppositeCampOccupied(this.board));
    }

    // Fast teminal Assignment2.test
    private boolean isTerminal() {
        // Either one wins
        int meInOwn = this.playerMax.getCounterMeInOwnCamp();
        int meInOpposite = this.playerMax.getCounterMeInOppositeCamp();

        int opponentInOwn = this.playerMin.getCounterMeInOwnCamp();
        int opponentInOpposite = this.playerMin.getCounterMeInOppositeCamp();

        // Initial state
        if (meInOwn == 19 || opponentInOwn == 19) {
            return false;
        }

        return ((meInOwn+opponentInOpposite == 19) || (meInOpposite+opponentInOwn == 19));
    }

    private int doAction(Player player, Action action, Board currBoard) {
        return player.update(action.getPiecesID(), action, currBoard);
    }

    private void undoAction(Player player, Action action, Board currBoard) {
        player.restore(action.getPiecesID(), action, currBoard);
    }

    public Action getOptimalAct() {
        return this.optimalAct;
    }

    public double getBeginOfSearch() {
        return this.beginOfSearch;
    }

    public SearchMode getMode() {
        return this.mode;
    }

    public void debug() {
        // Check the board & Check the players
        this.board.debug();
    }

}
