package Assignment2.EfficientPruningButMessy;

/**
 * @projectName FoAI-Assignments
 * @fileName Board
 * @auther Qiaoyi Yin
 * @time 2019-10-18 14:59
 * @function Board to store the current state
 */

public class Board {
    // Attributes
    private int height;
    private int width;

    // Status of the board
    private PlayerType[][]board;

    // Attributes of the board
    private PlayerType[][]attributes;

    // Default construction
    Board() {
        this.height = 0;
        this.width = 0;
        this.board = null;
        this.attributes = null;
    }

    Board(int height, int width) {
        this.height = height;
        this.width = width;

        this.board = new PlayerType[height][width];
        this.attributes = new PlayerType[height][width];

        for (int row=0; row<this.height; ++row) {
            for (int col=0; col<this.width; ++col) {
                this.attributes[row][col] = PlayerType.EMPTY;
                this.board[row][col] = PlayerType.EMPTY;
            }
        }
    }

    Board(Board other) {
        this.height = other.getHeight();
        this.width = other.getWidth();

        this.board = new PlayerType[this.height][this.width];
        for (int row=0; row<this.height; ++row) {
            for (int col=0; col<this.width; ++col) {
                this.board[row][col] = other.getBoard()[row][col];
            }
        }

        this.attributes = new PlayerType[this.height][this.width];
        for (int row=0; row<this.height; ++row) {
            for (int col=0; col<this.width; ++col) {
                this.attributes[row][col] = other.getAttributes()[row][col];
            }
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public PlayerType[][] getBoard() {
        return this.board;
    }

    public PlayerType getBoard(int row, int col) {
        assert (row >= 0 && row < height);
        assert (col >= 0 && col < width);

        return this.board[row][col];
    }

    public PlayerType[][] getAttributes() {
        return this.attributes;
    }

    public PlayerType getAttributes(int row, int col) {
        assert (row >= 0 && row < height);
        assert (col >= 0 && col < width);

        return this.attributes[row][col];
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setBoard(char [][]chars) throws Exception{
        // Satisfy the (H x W) requirements
        assert (this.height == chars.length && this.width == chars[0].length);

        for (int row=0; row<this.height; ++row) {
            for (int col=0; col<this.width; ++col) {
                switch (chars[row][col]) {
                    case 'B': {
                        this.board[row][col] = PlayerType.BLACK;
                    }
                    case 'W': {
                        this.board[row][col] = PlayerType.WHITE;
                    }
                    case '.': {
                        this.board[row][col] = PlayerType.EMPTY;
                    }
                    default: {
                        this.board[row][col] = PlayerType.UNKNOWN;
                        throw new Exception("Invalid type of pieces.");
                    }
                }
            }
        }
    }

    public void setAttributes(Camp blackCamp, Camp whiteCamp) {
        // Store black camp
        int blackTop = blackCamp.getCampTop();
        int blackDown = blackCamp.getCampDown();
        for (int i1=blackTop; i1<=blackDown; ++i1) {
            int idx = i1-blackCamp.getCampTop();
            for (int j2=blackCamp.getCampLeft(idx); j2<=blackCamp.getCampRight(idx); ++j2) {
                this.setAttributes(i1, j2, PlayerType.BLACK);
            }
        }

        // Store white camp
        int whiteTop = whiteCamp.getCampTop();
        int whiteDown =whiteCamp.getCampDown();
        for (int i1=whiteTop; i1<=whiteDown; ++i1) {
            int idx = i1-whiteCamp.getCampTop();
            for (int j2=whiteCamp.getCampLeft(idx); j2<=whiteCamp.getCampRight(idx); ++j2) {
                this.setAttributes(i1, j2, PlayerType.WHITE);
            }
        }
    }

    public void setBoard(int row, int col, PlayerType type) {
        // Satisfy the (H x W) requirements
        assert (row >= 0 && row < this.height);
        assert (col >= 0 && col < this.width);

        this.board[row][col] = type;
    }

    public void setAttributes(int row, int col, PlayerType type) {
        // Satisfy the (H x W) requirements
        assert (row >= 0 && row < this.height);
        assert (col >= 0 && col < this.width);

        this.attributes[row][col] = type;
    }

    public void updateBoard(Action action) throws Exception {
        int []oldCoord = action.getOldCoord();
        int []newCoord = action.getNewCoord();

        // Assert new coordinate must have no pieces
        if (this.board[newCoord[0]][newCoord[1]] != PlayerType.EMPTY) {
            throw new Exception("Invalid action(move/jump).");
        }
        else {
            this.board[newCoord[0]][newCoord[1]] = action.getPiecesToMove();
            this.board[oldCoord[0]][oldCoord[1]] = action.getPiecesToCover();
        }
    }

    public void restoreBoard(Action action) throws Exception {
        int []oldCoord = action.getOldCoord();
        int []newCoord = action.getNewCoord();

        // Assert new coordinate must have no pieces
        if (this.board[oldCoord[0]][oldCoord[1]] != PlayerType.EMPTY) {
            throw new Exception("Incorrect restore(move/jump).");
        }
        else {
            this.board[oldCoord[0]][oldCoord[1]] = action.getPiecesToMove();
            this.board[newCoord[0]][newCoord[1]] = action.getPiecesToCover();
        }
    }

    public void debug() {
        // Print the board
        for (int row=0; row<this.height; ++row) {
            for (int col=0; col<this.width; ++col) {
                switch (this.board[row][col]) {
                    case EMPTY: {
                        System.out.print(".");
                        break;
                    }
                    case BLACK: {
                        System.out.print("B");
                        break;
                    }
                    case WHITE: {
                        System.out.print("W");
                        break;
                    }
                }
            }
            System.out.println();
        }

        System.out.println();
        // Print the attribute
        for (int row=0; row<this.height; ++row) {
            for (int col=0; col<this.width; ++col) {
                switch (this.attributes[row][col]) {
                    case EMPTY: {
                        System.out.print(".");
                        break;
                    }
                    case BLACK: {
                        System.out.print("B");
                        break;
                    }
                    case WHITE: {
                        System.out.print("W");
                        break;
                    }
                }
            }
            System.out.println();
        }
    }
}
