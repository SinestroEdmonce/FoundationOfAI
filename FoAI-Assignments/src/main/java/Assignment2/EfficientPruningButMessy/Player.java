package Assignment2.EfficientPruningButMessy;

import java.util.*;

/**
 * @projectName FoAI-Assignments
 * @fileName Player
 * @auther Qiaoyi Yin
 * @time 2019-10-18 15:00
 * @function Player, min or max
 */

public class Player {
    // Type of player
    private PlayerType playerType;

    // List of all own pieces
    private List<Pieces> piecesList;
    private List<Integer> piecesIdx;

    // Camps
    private Camp ownCamp;
    private Camp oppositeCamp;

    // Directions
    private int [][] diAct = null;

    // Auxiliary counter
    private int counterMeInOwnCamp;
    private int counterMeInOppositeCamp;

    Player() {
        this.playerType = PlayerType.UNKNOWN;
        this.counterMeInOppositeCamp = 0;
        this.counterMeInOwnCamp = 0;

        this.piecesList = null;
        this.ownCamp = null;
        this.oppositeCamp = null;
    }

    Player(PlayerType type, Camp ownCamp, Camp oppositeCamp) {
        this.playerType = type;

        // Directions
        if (type == PlayerType.WHITE) {
            this.diAct = new int [][] {{-1, -1}, {0, -1}, {-1, 0}, {1, -1}, {-1, 1}, {1, 0}, {0, 1}, {1, 1}};
        }
        else {
            this.diAct = new int [][] {{1, 1}, {1, 0}, {0, 1}, {-1, 1}, {1, -1}, {0, -1}, {-1, 0}, {-1, -1}};
        }

        this.counterMeInOppositeCamp = 0;
        this.counterMeInOwnCamp = 0;

        this.piecesList = new ArrayList<>();
        this.piecesIdx= new ArrayList<>();

        this.ownCamp = ownCamp;
        this.oppositeCamp = oppositeCamp;
    }

    public void initilizeCounter(PiecesStatus status) {
        if (status == PiecesStatus.IN_OWN_CAMP) {
            ++this.counterMeInOwnCamp;
        }
        else if (status == PiecesStatus.IN_OPPOSITE_CAMP) {
            ++this.counterMeInOppositeCamp;
        }
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    public void setPiecesList(List<Pieces> piecesList) {
        this.piecesList = piecesList;
    }

    public void addPieces(Pieces pieces) {
        this.piecesList.add(pieces);
        this.piecesIdx.add(this.piecesList.size()-1);
    }

    public void addPieces(int row, int col, PlayerType playerType, PiecesStatus status) {
        this.piecesList.add(new Pieces(row, col, playerType, status));
        this.piecesIdx.add(this.piecesList.size()-1);
    }

    public PlayerType getPlayerType() {
        return this.playerType;
    }

    public Queue<Action> generateInsideCampActions(Board board, int index, boolean isOut) {
        // Heap to obtain move-furthest action
        Queue<Action> actions = new LinkedList<>();

        if (this.piecesList.get(index).getStatusOfPieces() == PiecesStatus.IN_OWN_CAMP) {
            this.generateAllActions(actions, board, index);

            // Firstly we need all the actions that can move a piece directly out of the camp
            // If we need 'Out', which the variable is false,
            // we will filter all the actions by checking whether it satisfies the constraint
            if (isOut) {
                actions = this.obtainAllOutCampActions(actions, board);
            }
        }

        return actions;
    }

    private Queue<Action> obtainAllOutCampActions(Queue<Action> actions, Board board) {
        Queue<Action> remainingActions = new LinkedList<>();
        Action act = null;
        while ((act = actions.poll()) != null) {
            int []newCoords = act.getNewCoord();
            if (this.whereIsCurrentPieces(newCoords[0], newCoords[1], board) != PiecesStatus.IN_OWN_CAMP) {
                remainingActions.add(act);
            }
        }

        return remainingActions;
    }

    public Queue<Action> generateOutsideCampActions(Board board, int index) {
        // Action queue to return
        Queue<Action> actions = new LinkedList<>();

        if (this.piecesList.get(index).getStatusOfPieces() != PiecesStatus.IN_OWN_CAMP) {
            this.generateAllActions(actions, board, index);
        }

        return actions;
    }

    private void generateAllActions(Queue<Action> actions, Board board, int index) {
        Pieces currPieces = this.piecesList.get(index);
        boolean [][]isVisited = new boolean [board.getHeight()][board.getWidth()];
        // Generate Single Move
        for (int []di: this.diAct) {
            int []oldCoord = new int []{currPieces.getRow(), currPieces.getCol()};
            int []newCoord = new int []{oldCoord[0]+di[0], oldCoord[1]+di[1]};
            // Check whether is valid and obey the rules
            if (this.isValid(newCoord[0], newCoord[1], board)
                    && this.checkRules(newCoord[0], newCoord[1], currPieces, board)) {

                actions.add(new ActionMove(oldCoord, newCoord,
                        currPieces.getTypeOfPieces(), PlayerType.EMPTY, index));
                isVisited[newCoord[0]][newCoord[1]] = true;
            }
        }

        // Generate Jump
        this.generateJump(actions, board, index, isVisited);

    }

    private void generateJump(Queue<Action> actions, Board board, int idx, boolean [][]isVisited) {
        Pieces currPieces = this.piecesList.get(idx);
        ActionJump jump = new ActionJump(currPieces.getRow(), currPieces.getCol(),
                currPieces.getTypeOfPieces(), idx);

        isVisited[currPieces.getRow()][currPieces.getCol()] = true;
        this.dfsJump(actions, board, jump, currPieces, isVisited);
    }

    private void dfsJump(Queue<Action> actions, Board board,
                         ActionJump prev, Pieces pieces, boolean [][]isVisited) {

        for (int []di: this.diAct) {
            int []oldCoord = prev.getNewCoord();
            int []midCoord = new int []{oldCoord[0]+di[0], oldCoord[1]+di[1]};
            int []newCoord = new int []{oldCoord[0]+di[0]*2, oldCoord[1]+di[1]*2};
            // Have not visited before and also the jump is valid
            if (this.isValid(newCoord[0], newCoord[1], board)
                    && this.isJumpValid(midCoord, board) && !isVisited[newCoord[0]][newCoord[1]]) {

                ActionJump nextJump = new ActionJump(prev);
                nextJump.expandPath(newCoord[0], newCoord[1]);
                nextJump.setPiecesToCover(PlayerType.EMPTY);
                if (this.checkRules(newCoord[0], newCoord[1], pieces, board)) {
                    actions.add(nextJump);
                }

                // Update info.
                this.updateJump(nextJump, board);
                isVisited[newCoord[0]][newCoord[1]] = true;

                // Continue to search
                this.dfsJump(actions, board, nextJump, pieces, isVisited);

                // Restore info.
                this.restoreJump(nextJump, board);
            }
        }
    }

    private void updateJump(ActionJump action, Board board) {
        int []oldCoord = action.getIndex(action.getLengthOfPath()-2);
        int []newCoord = action.getNewCoord();

        board.setBoard(oldCoord[0], oldCoord[1], PlayerType.EMPTY);
        board.setBoard(newCoord[0], newCoord[1], this.playerType);
    }

    private void restoreJump(ActionJump action, Board board) {
        // Previous location
        int []oldCoord = action.getIndex(action.getLengthOfPath()-2);
        int []newCoord = action.getNewCoord();

        board.setBoard(oldCoord[0], oldCoord[1], this.playerType);
        board.setBoard(newCoord[0], newCoord[1], PlayerType.EMPTY);
    }

    private boolean isJumpValid(int []midCoord, Board board) {
        return (board.getBoard(midCoord[0], midCoord[1]) != PlayerType.EMPTY);
    }

    private boolean checkRules(int nextRow, int nextCol, Pieces pieces, Board board) {
        try {
            switch (pieces.getStatusOfPieces()) {
                case IN_OWN_CAMP: {
                    int []center = this.ownCamp.getCenter();
                    int distOld = Math.abs(pieces.getRow()-center[0])+Math.abs(pieces.getCol()-center[1]);
                    int distNew = Math.abs(nextRow-center[0])+Math.abs(nextCol-center[1]);

                    return (distNew > distOld);
                }
                case IN_OPPOSITE_CAMP: {
                    return (board.getAttributes(nextRow, nextCol) != PlayerType.EMPTY)
                            && (board.getAttributes(nextRow, nextCol) != this.playerType);
                }
                case NOT_IN_CAMPS: {
                    return (board.getAttributes(nextRow, nextCol) != this.playerType);
                }
                default: {
                    throw new Exception("Invalid status of the current pieces.");
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean isValid(int nextRow, int nextCol, Board board) {
        if (nextRow < 0 || nextRow >= board.getHeight()
                || nextCol < 0 || nextCol >= board.getWidth()
                || board.getBoard(nextRow, nextCol) != PlayerType.EMPTY) {
            return false;
        }
        else {
            return true;
        }
    }

    public PiecesStatus whereIsCurrentPieces(int row, int col, Board board) {
        assert (row >= 0 && col >= 0);

        // In own camp
        if (board.getAttributes(row, col) == this.playerType) {
            return PiecesStatus.IN_OWN_CAMP;
        }
        // Not in camp
        else if (board.getAttributes(row, col) == PlayerType.EMPTY) {
            return PiecesStatus.NOT_IN_CAMPS;
        }
        // In opposite camp
        else {
            return PiecesStatus.IN_OPPOSITE_CAMP;
        }

    }

    public boolean isOppositeCampOccupied(Board board) {
        // Avoid the errors
        assert (this.oppositeCamp != null && this.piecesList != null);

        boolean isMe = false;
        for (int i1=this.oppositeCamp.getCampTop(); i1<=this.oppositeCamp.getCampDown(); ++i1) {
            int idx = i1-this.oppositeCamp.getCampTop();
            for (int j2=this.oppositeCamp.getCampLeft(idx); j2<=this.oppositeCamp.getCampRight(idx); ++j2) {
                if (board.getBoard(i1, j2) == PlayerType.EMPTY) {
                    return false;
                }
                // Whether there exists at least one my own pieces in the opposite camp
                else if (board.getBoard(i1, j2) == this.playerType) {
                    isMe = true;
                }
            }
        }

        return isMe;
    }

    public boolean isSomePiecesInOwnCamp() {
        // Avoid the errors
        assert (this.ownCamp != null && this.piecesList != null);

        for (Pieces pieces: this.piecesList) {
            if (pieces.getStatusOfPieces() == PiecesStatus.IN_OWN_CAMP) {
                return true;
            }
        }
        return false;
    }

    private int update(Pieces pieces, Action action, Board board) {
        // Record the old status
        PiecesStatus oldStatus = pieces.getStatusOfPieces();
        int oldMeInOppositeCamp = this.counterMeInOppositeCamp;

        // Update the pieces
        int []newCoord = action.getNewCoord();
        pieces.setCoord(newCoord[0], newCoord[1]);
        pieces.setStatusOfPieces(this.whereIsCurrentPieces(pieces.getRow(), pieces.getCol(), board));

        // Update the counter
        this.updateCounter(oldStatus, pieces.getStatusOfPieces());

        // Update the board
        try {
            board.updateBoard(action);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return this.counterMeInOppositeCamp-oldMeInOppositeCamp;
    }

    public int update(int index, Action action, Board board) {
        return this.update(this.piecesList.get(index), action, board);
    }

    private void restore(Pieces pieces, Action action, Board board) {
        // Record new status
        PiecesStatus newStatus = pieces.getStatusOfPieces();

        // Restore the pieces
        int []oldCoord = action.getOldCoord();
        pieces.setCoord(oldCoord[0], oldCoord[1]);
        pieces.setStatusOfPieces(this.whereIsCurrentPieces(pieces.getRow(), pieces.getCol(), board));

        // Restore the counter
        this.restoreCounter(pieces.getStatusOfPieces(), newStatus);

        // Restore the board
        try {
            board.restoreBoard(action);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void restore(int index, Action action, Board board) {
        this.restore(this.piecesList.get(index), action, board);
    }

    public void updateCounter(PiecesStatus oldStatus, PiecesStatus newStatus) {
        if (oldStatus == PiecesStatus.IN_OWN_CAMP && newStatus != PiecesStatus.IN_OWN_CAMP) {
            --this.counterMeInOwnCamp;
        }

        if (oldStatus != PiecesStatus.IN_OPPOSITE_CAMP && newStatus == PiecesStatus.IN_OPPOSITE_CAMP) {
            ++this.counterMeInOppositeCamp;
        }
    }

    private void restoreCounter(PiecesStatus oldStatus, PiecesStatus newStatus) {
        if (oldStatus == PiecesStatus.IN_OWN_CAMP && newStatus != PiecesStatus.IN_OWN_CAMP) {
            ++this.counterMeInOwnCamp;
        }

        if (oldStatus != PiecesStatus.IN_OPPOSITE_CAMP && newStatus == PiecesStatus.IN_OPPOSITE_CAMP) {
            --this.counterMeInOppositeCamp;
        }
    }

    public int getCounterMeInOwnCamp() {
        return this.counterMeInOwnCamp;
    }

    public int getCounterMeInOppositeCamp() {
        return this.counterMeInOppositeCamp;
    }

    public List<Pieces> getPiecesList() {
        return this.piecesList;
    }

    public int evaluate() {
        int totalDist = 0;
        int totalPieces = 0;
        int []center = this.oppositeCamp.getCenter();
        for (Pieces pieces: this.piecesList) {
            // Count pieces in opposite camp
            if (pieces.getStatusOfPieces() == PiecesStatus.IN_OPPOSITE_CAMP) {
                ++totalPieces;
            }

            // Count distance to opposite camp
            int xDist = Math.abs(pieces.getRow()-center[0]);
            int yDist = Math.abs(pieces.getCol()-center[1]);
            totalDist += (xDist+yDist);
        }

        return (7*totalPieces-10*totalDist);
    }

    public double countDelta(Action action, int deltaMeInOppositeCamp, double depth) {
        int []oldCoord = action.getOldCoord();
        int []newCoord = action.getNewCoord();
        int []center = this.oppositeCamp.getCenter();

        // Count distance to opposite camp
        int oldDist = Math.abs(oldCoord[0]-center[0])+Math.abs(oldCoord[1]-center[1]);
        int newDist = Math.abs(newCoord[0]-center[0])+Math.abs(newCoord[1]-center[1]);

        int deltaDist = newDist-oldDist;
        return (double)(7*deltaMeInOppositeCamp-10*deltaDist)*(1.0-depth/10000.0);
    }
}
