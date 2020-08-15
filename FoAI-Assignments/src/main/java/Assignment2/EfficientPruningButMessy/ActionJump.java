package Assignment2.EfficientPruningButMessy;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName FoAI-Assignments
 * @fileName ActionJump
 * @auther Qiaoyi Yin
 * @time 2019-10-18 18:11
 * @function Action Jump
 */

public class ActionJump extends Action {
    // List of Coordinates
    private List<int[]> pathOfJump;

    // Length of path
    private int lengthOfPath;

    ActionJump() {
        this.pathOfJump = new ArrayList<>();
        this.lengthOfPath = 0;

        this.oldCoord = new int [2];
        this.newCoord = new int [2];

        this.typeOfAction = ActionType.JUMP;
        this.piecesToMove = null;
        this.piecesToCover = null;
    }

    ActionJump(int currentRow, int currentCol, PlayerType p2Move, int index) {
        this.typeOfAction = ActionType.JUMP;
        this.piecesToMove = p2Move;
        this.piecesToCover = PlayerType.UNKNOWN;
        this.piecesID = index;

        this.oldCoord = new int [] {currentRow, currentCol};
        this.newCoord = null;

        this.pathOfJump = new ArrayList<>();
        this.lengthOfPath = 0;
        this.pathOfJump.add(new int []{currentRow, currentCol});
        this.lengthOfPath += 1;

        // Avoid errors
        assert (this.lengthOfPath == this.pathOfJump.size());
    }

    ActionJump(ActionJump other) {
        this.typeOfAction = other.getTypeOfAction();
        this.piecesToCover = other.getPiecesToCover();
        this.piecesToMove = other.getPiecesToMove();
        this.oldCoord = other.getOldCoord();
        this.newCoord = other.getNewCoord();
        this.piecesID = other.getPiecesID();

        this.pathOfJump = new ArrayList<>();
        this.lengthOfPath = other.getLengthOfPath();
        for (int itr=0; itr<this.lengthOfPath; ++itr) {
            this.pathOfJump.add(other.getIndex(itr));
        }
    }

    public void expandPath(int nextRow, int nextCol) {
        this.pathOfJump.add(new int [] {nextRow, nextCol});
        this.lengthOfPath += 1;

        this.newCoord = this.pathOfJump.get(this.lengthOfPath-1);

        // Avoid errors
        assert (this.lengthOfPath == this.pathOfJump.size());
    }

    public void shortenPath() {
        this.pathOfJump.remove(this.lengthOfPath-1);
        this.lengthOfPath -= 1;

        this.newCoord = this.pathOfJump.get(this.lengthOfPath-1);

        // Avoid errors
        assert (this.lengthOfPath == this.pathOfJump.size());
    }

    @Override
    public int[] getNewCoord() {
        return (this.newCoord != null)? new int [] {this.newCoord[0], this.newCoord[1]}:
                new int [] {this.oldCoord[0], this.oldCoord[1]};
    }

    @Override
    public int[] getOldCoord() {
        return new int [] {this.oldCoord[0], this.oldCoord[1]};
    }

    public int getLengthOfPath() {
        return this.lengthOfPath;
    }

    public List<int[]> getPathOfJump() {
        return this.pathOfJump;
    }

    public int[] getIndex(int index) {
        return this.pathOfJump.get(index);
    }
}
