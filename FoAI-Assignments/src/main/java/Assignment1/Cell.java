package Assignment1;

/**
 * @projectName FoAI-Assignments
 * @fileName Cell
 * @auther Qiaoyi Yin
 * @time 2019-09-19 15:46
 * @function Definition of every cell
 */

public class Cell {
    // Coordinates
    private int row;
    private int col;
    private int elevation;

    // Cost function: fCost = gCost or fCost = gCost + hCost
    private double fCost;
    private double gCost;
    private double hCost;

    // Target or not
    private boolean target;

    // Help obtain optimal path
    private Cell parent;

    // Constructors
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;

        this.gCost = 0;
        this.hCost = 0;
        this.fCost = 0;

        this.target = false;
        this.parent = null;
    }

    public Cell(int row, int col, int elevation) {
        this.row = row;
        this.col = col;
        this.elevation = elevation;

        this.gCost = 0;
        this.hCost = 0;
        this.fCost = 0;

        this.target = false;
        this.parent = null;
    }

    public Cell(int row, int col, int elevation, double gCost) {
        this.row = row;
        this.col = col;
        this.elevation = elevation;

        this.gCost = gCost;
        this.fCost = this.gCost;
        this.hCost = 0;

        this.target = false;
        this.parent = null;
    }

    public Cell(int row, int col, int elevation, double gCost, double hCost) {
        this.row = row;
        this.col = col;
        this.elevation = elevation;

        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = this.gCost+this.hCost;

        this.target = false;
        this.parent = null;
    }

    public Cell(int row, int col, int elevation, double gCost, double hCost, Cell parent) {
        this.row = row;
        this.col = col;
        this.elevation = elevation;

        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = this.gCost+this.hCost;


        this.target = false;
        this.parent = parent;
    }

    public Cell(int row, int col, int elevation, double gCost, double hCost, Cell parent, boolean isTarget) {
        this.row = row;
        this.col = col;
        this.elevation = elevation;

        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = this.gCost+this.hCost;

        this.target = isTarget;
        this.parent = parent;
    }

    public Cell(Cell cell) {
        this.row = cell.getRow();
        this.col = cell.getCol();
        this.elevation = cell.getElevation();

        this.gCost = cell.getgCost();
        this.hCost = cell.gethCost();
        this.fCost = this.gCost+this.hCost;

        this.target = cell.isTarget();
        this.parent = cell.getParent();
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }

    public void setgCost(double gCost) {
        this.gCost = gCost;
    }

    public void sethCost(double hCost) {
        this.hCost = hCost;
    }

    public void setParent(Cell parent) {
        this.parent = parent;
    }

    public void setTarget(boolean target) {
        this.target = target;
    }

    public int getRow() {
        return this.row;
    }

    public int getCol() {
        return this.col;
    }

    public int getElevation() {
        return this.elevation;
    }

    public double getgCost() {
        return this.gCost;
    }

    public double gethCost() {
        return this.hCost;
    }

    public double getfCost() {
        this.fCost = this.gCost+this.hCost;
        return this.fCost;
    }

    public Cell getParent() {
        return this.parent;
    }

    public boolean isTarget() {
        return this.target;
    }

    @Override public boolean equals(Object obj) {
        if (obj instanceof Cell) {
            Cell cell = (Cell) obj;
            return (cell.getRow() == this.row
                    && cell.getCol() == this.col);
        }

        return false;
    }
}
