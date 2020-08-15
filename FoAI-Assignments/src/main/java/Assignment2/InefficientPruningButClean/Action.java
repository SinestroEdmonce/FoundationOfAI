package Assignment2.InefficientPruningButClean;

/**
 * @projectName FoAI-Assignments
 * @fileName Action
 * @auther Qiaoyi Yin
 * @time 2019-10-18 15:24
 * @function Action to use, including move and jump
 */

public class Action {
    // Type of action
    protected ActionType typeOfAction;

    // Type of pieces to be moved
    protected PlayerType piecesToMove;

    // Type of pieces to be coverd
    protected PlayerType piecesToCover;

    // Old position
    protected int []oldCoord;

    // New position
    protected int []newCoord;

    // Pieces to Move
    protected int piecesID;

    // Default construction
    Action() {
        this.piecesToMove = PlayerType.UNKNOWN;
        this.piecesToCover = PlayerType.UNKNOWN;

        this.typeOfAction = ActionType.UNKNOWN;

        this.oldCoord = null;
        this.newCoord = null;

        this.piecesID = -1;
    }

    public void setPiecesToCover(PlayerType piecesToCover) {
        this.piecesToCover = piecesToCover;
    }

    public void setPiecesToMove(PlayerType piecesToMove) {
        this.piecesToMove = piecesToMove;
    }

    public void setTypeOfAction(ActionType typeOfAction) {
        this.typeOfAction = typeOfAction;
    }

    public void setNewCoord(int[] newCoord) {
       this.newCoord = newCoord;
    }

    public void setOldCoord(int[] oldCoord) {
        this.oldCoord = oldCoord;
    }

    public void setPiecesID(int index) {
        assert (index >= 0);

        this.piecesID = index;
    }

    public PlayerType getPiecesToCover() {
        return this.piecesToCover;
    }

    public PlayerType getPiecesToMove() {
        return this.piecesToMove;
    }

    public ActionType getTypeOfAction() {
        return this.typeOfAction;
    }

    public int[] getOldCoord() {
        return this.oldCoord;
    }

    public int[] getNewCoord() {
        return this.newCoord;
    }

    public int getPiecesID() {
        return this.piecesID;
    }
}
