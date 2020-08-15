package Assignment2.EfficientPruningButMessy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * @projectName FoAI-Assignments
 * @fileName Agent
 * @auther Qiaoyi Yin
 * @time 2019-10-18 15:25
 * @function Agent and its Data IO functions
 */

class DataIO {
    // Mode of search
    private SearchMode modeOfSearch;

    // Board
    private Board board;

    // Players
    private Player playerMax;
    private Player playerMin;

    // Camps
    private Camp campBlack;
    private Camp campWhite;

    // Time limits
    private double timeLimit;
    // Begin time
    private double beginingTime;

    // Input file name
    private String fileInput;
    // Output file name
    private  String fileOutput;

    // Default Initialization
    DataIO(String input, String output) {
        this.fileInput = input;
        this.fileOutput = output;

        this.modeOfSearch = SearchMode.UNKNOWN;
        this.board = new Board(16, 16);

        this.playerMax = null;
        this.playerMin = null;

        this.campBlack = new Camp(5);
        this.campBlack.setCamp(PlayerType.BLACK, 16, 16);
        this.campWhite = new Camp(5);
        this.campWhite.setCamp(PlayerType.WHITE, 16, 16);

        this.board.setAttributes(this.campBlack, this.campWhite);

        this.timeLimit = 0.0;
    }

    public Board getBoard() {
        return this.board;
    }

    public Player getPlayerMax() {
        return this.playerMax;
    }

    public Player getPlayerMin() {
        return this.playerMin;
    }

    public SearchMode getModeOfSearch() {
        return this.modeOfSearch;
    }

    public double getTimeLimit() {
        return this.timeLimit;
    }

    private void initializePlayers(String color4Me) {
        if (color4Me.equals("WHITE")) {
            this.playerMax = new Player(PlayerType.WHITE, this.campWhite, this.campBlack);
            this.playerMin = new Player(PlayerType.BLACK, this.campBlack, this.campWhite);
        }
        else {
            this.playerMax = new Player(PlayerType.BLACK, this.campBlack, this.campWhite);
            this.playerMin = new Player(PlayerType.WHITE, this.campWhite, this.campBlack);
        }
    }

    private void updateInfo(int row, int col, boolean isColor4Me, Board board) {
        // Update info. of players
        if (isColor4Me) {
            Pieces pieces = new Pieces(row, col,
                    this.playerMax.getPlayerType(),this.playerMax.whereIsCurrentPieces(row, col, board));
            this.playerMax.addPieces(pieces);

            // Initialize counter
            this.playerMax.initilizeCounter(pieces.getStatusOfPieces());

            // Update info. of the board
            this.board.setBoard(row, col, this.playerMax.getPlayerType());
        }
        else {
            Pieces pieces = new Pieces(row, col,
                    this.playerMin.getPlayerType(), this.playerMin.whereIsCurrentPieces(row, col, board));
            this.playerMin.addPieces(pieces);

            // Initialize counter
            this.playerMin.initilizeCounter(pieces.getStatusOfPieces());

            // Update info. of the board
            this.board.setBoard(row, col, this.playerMin.getPlayerType());
        }
    }

    public void readFile2Memory() throws Exception {
        // Record the time
        this.beginingTime = System.currentTimeMillis();

        FileReader fileReader = new FileReader(this.fileInput);
        BufferedReader in = new BufferedReader(fileReader);

        // Read mode of search
        String line = in.readLine().trim();
        this.modeOfSearch = (line.equals("SINGLE")? SearchMode.SINGLE: SearchMode.GAME);

        // Read the color that I play
        line = in.readLine().trim();
        String color4Me = line;

        // Read time limitation (s)
        line = in.readLine().trim();
        this.timeLimit = Double.parseDouble(line);

        // Initialize the players
        this.initializePlayers(color4Me);
        int row = 0;
        while ((line = in.readLine()) != null) {
            int col = 0;
            char []chars = line.trim().toCharArray();

            // Resolve
            for (char ch: chars) {
                switch (ch) {
                    case 'B': {
                        this.updateInfo(row, col, (ch == color4Me.charAt(0)), this.board);
                        break;
                    }
                    case 'W': {
                        this.updateInfo(row, col, (ch == color4Me.charAt(0)), this.board);
                        break;
                    }
                    case '.': {
                        this.board.setBoard(row, col, PlayerType.EMPTY);
                        break;
                    }
                    default: {
                        throw new Exception("Invalid characters.");
                    }
                }

                ++col;
            }

            ++row;
        }

        in.close();
        fileReader.close();
    }

    public void writeAction2File(Action action) {
        try {
            FileWriter fileWriter = new FileWriter(this.fileOutput);
            BufferedWriter out = new BufferedWriter(fileWriter);
            // Game is terminal
            if (action == null)
                return;

            if (action.getTypeOfAction() == ActionType.MOVE) {
                ActionMove move = (ActionMove) action;
                int[] oldCoord = move.getOldCoord();
                int[] newCoord = move.getNewCoord();

                out.write("E "+oldCoord[1]+","+oldCoord[0]+" "+newCoord[1]+","+newCoord[0]);
            } else {
                ActionJump jump = (ActionJump) action;

                int idx = 0;
                for (; idx<jump.getLengthOfPath()-2; ++idx) {
                    int[] oldCoord = jump.getIndex(idx);
                    int[] newCoord = jump.getIndex(idx+1);
                    out.write("J "+oldCoord[1]+","+oldCoord[0]+" "+newCoord[1]+","+newCoord[0]+"\n");
                }

                int[] oldCoord = jump.getIndex(idx);
                int[] newCoord = jump.getIndex(idx+1);
                out.write("J "+oldCoord[1]+","+oldCoord[0]+" "+newCoord[1]+","+newCoord[0]);
            }

            out.close();
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeBoard2File(Action action) {
        this.writeAction2File(action);

        try {
            FileWriter fileWriter = new FileWriter(this.fileOutput);
            BufferedWriter out = new BufferedWriter(fileWriter);
            this.writeHeader2File(out);
            // Game is terminal
            if (action != null) {

                int[] oldCoord = action.getOldCoord();
                int[] newCoord = action.getNewCoord();

                this.board.setBoard(oldCoord[0], oldCoord[1], action.getPiecesToCover());
                this.board.setBoard(newCoord[0], newCoord[1], action.getPiecesToMove());
            }

            this.writeBoard2File(out);

            out.close();
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeBoard2File(BufferedWriter out) {
        try {
            for (int row=0; row<16; ++row) {
                StringBuilder sBuilder = new StringBuilder();
                for (int col=0; col<16; ++col) {
                    switch (this.board.getBoard(row, col)) {
                        case EMPTY: {
                            sBuilder.append(".");
                            break;
                        }
                        case BLACK: {
                            sBuilder.append("B");
                            break;
                        }
                        case WHITE: {
                            sBuilder.append("W");
                            break;
                        }
                    }
                }
                if (row != 15) {
                    sBuilder.append("\n");
                }
                out.write(sBuilder.toString());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeHeader2File(BufferedWriter out) {
        try {
            if (this.modeOfSearch == SearchMode.SINGLE) {
                out.write("SINGLE\n");
            }
            else {
                out.write("GAME\n");
            }

            if (this.playerMax.getPlayerType() == PlayerType.BLACK) {
                out.write("WHITE\n");
            }
            else {
                out.write("BLACK\n");
            }

            // Time consuming calculation
            double timeConsuming = (System.currentTimeMillis()-this.beginingTime)/1000.0;
            out.write(this.timeLimit-timeConsuming+"\n");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void writeAction2Screen(Action action) {
        if (action == null) {
            System.out.println("No move. Terminal.");
            return;
        }

        if (action.getTypeOfAction() == ActionType.MOVE) {
            ActionMove move = (ActionMove) action;
            int[] oldCoord = move.getOldCoord();
            int[] newCoord = move.getNewCoord();

            System.out.println("E "+oldCoord[1]+","+oldCoord[0]+" "+newCoord[1]+","+newCoord[0]);
        }
        else {
            ActionJump jump = (ActionJump) action;

            int idx = 0;
            for (; idx<jump.getLengthOfPath()-1; ++idx) {
                int[] oldCoord = jump.getIndex(idx);
                int[] newCoord = jump.getIndex(idx+1);
                System.out.println("J "+oldCoord[1]+","+oldCoord[0]+" "+newCoord[1]+","+newCoord[0]);
            }
        }
    }

    public double getBeginingTime() {
        return this.beginingTime;
    }

}

public class Agent {
    public static void main(String []args) {
        String in = new String("sources/input13.txt");
        String out = new String("results/output13.txt");

        DataIO dataIO = new DataIO(in, out);
        try {
            dataIO.readFile2Memory();

            MinimaxSearch minimax = new MinimaxSearch(dataIO.getModeOfSearch(),
                    dataIO.getPlayerMax(), dataIO.getPlayerMin(), dataIO.getBoard(),
                    dataIO.getTimeLimit(), 3, dataIO.getBeginingTime());

            minimax.alphaBetaSearch();

            dataIO.writeAction2File(minimax.getOptimalAct());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
