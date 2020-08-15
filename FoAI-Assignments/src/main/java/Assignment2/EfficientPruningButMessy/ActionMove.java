package Assignment2.EfficientPruningButMessy;

/**
 * @projectName FoAI-Assignments
 * @fileName ActionMove
 * @auther Qiaoyi Yin
 * @time 2019-10-18 18:11
 * @function Action Move
 */

public class ActionMove extends Action {

    // Default construct
    ActionMove() {
        this.typeOfAction = ActionType.MOVE;
    }

    ActionMove(int []oldCoord, int []newCoord, PlayerType p2Move, PlayerType p2Cover, int index) {
        this.oldCoord = oldCoord;
        this.newCoord = newCoord;

        this.typeOfAction = ActionType.MOVE;

        this.piecesToCover = p2Cover;
        this.piecesToMove = p2Move;

        this.piecesID = index;
    }

    ActionMove(ActionMove other) {
        this.typeOfAction = other.getTypeOfAction();
        this.piecesToCover = other.getPiecesToCover();
        this.piecesToMove = other.getPiecesToMove();
        this.piecesID = other.getPiecesID();

        this.oldCoord = other.getOldCoord();
        this.newCoord = other.getNewCoord();
    }

    @Override
    public int[] getOldCoord() {
        return new int [] {this.oldCoord[0], this.oldCoord[1]};
    }

    @Override
    public int[] getNewCoord() {
        return new int [] {this.newCoord[0], this.newCoord[1]};
    }
}
