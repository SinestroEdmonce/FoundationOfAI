package Assignment2.InefficientPruningButClean;

/**
 * @projectName FoAI-Assignments
 * @fileName Pieces
 * @auther Qiaoyi Yin
 * @time 2019-10-18 14:58
 * @function Pieces, used by Players
 */

public class Pieces {
    // Coordinates
    private int row;
    private int col;

    // PlayerType: black and white, (EMPTY won't be use here)
    private PlayerType typeOfPieces;

    // Status
    private PiecesStatus statusOfPieces;

    // Default construction
    Pieces() {
        this.row = -1;
        this.col = -1;
        this.typeOfPieces = PlayerType.UNKNOWN;
        this.statusOfPieces = PiecesStatus.UNKNOWN;
    }

    Pieces(int row, int col) {
        this.row = row;
        this.col = col;
        this.typeOfPieces = PlayerType.UNKNOWN;
        this.statusOfPieces = PiecesStatus.UNKNOWN;
    }

    Pieces(int row, int col, PlayerType playerType) {
        this.row = row;
        this.col = col;
        this.typeOfPieces = playerType;
        this.statusOfPieces = PiecesStatus.UNKNOWN;
    }

    Pieces(int row, int col, PlayerType playerType, PiecesStatus status) {
        this.row = row;
        this.col = col;
        this.typeOfPieces = playerType;
        this.statusOfPieces = status;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setCoord(int row, int col) {
        this.setRow(row);
        this.setCol(col);
    }

    public void setStatusOfPieces(PiecesStatus statusOfPieces) {
        this.statusOfPieces = statusOfPieces;
    }

    public void setTypeOfPieces(PlayerType playerTypeOfPieces) {
        this.typeOfPieces = playerTypeOfPieces;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public PiecesStatus getStatusOfPieces() {
        return this.statusOfPieces;
    }

    public PlayerType getTypeOfPieces() {
        return this.typeOfPieces;
    }

    public boolean isInOwnCamp() {
        return (this.statusOfPieces == PiecesStatus.IN_OWN_CAMP);
    }

    public boolean isInOppositeCamp() {
        return (this.statusOfPieces == PiecesStatus.IN_OPPOSITE_CAMP);
    }

    public boolean isNotInCamp() {
        return (this.statusOfPieces == PiecesStatus.NOT_IN_CAMPS);
    }

}
