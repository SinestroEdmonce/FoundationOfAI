package Assignment1;

import java.util.ArrayList;
import java.util.List;

/**
 * @projectName FoAI-Assignments
 * @fileName Grid
 * @auther Qiaoyi Yin
 * @time 2019-09-19 15:46
 * @function Definition of the current landscape (Grid)
 */

public class Grid {
    // Size
    private int height;
    private int width;

    // Threshold for the cell's elevation
    private int threshold;

    // Content
    private List<List<Cell>> landMap = new ArrayList<>();

    // Landing site
    private Cell landingSite;

    // Targets
    private List<Cell> targets = new ArrayList<>();

    // Constructors
    public Grid(int height, int width) {
        this.height = height;
        this.width = width;

        this.landingSite = new Cell(0, 0, 0);
        for (int row=0; row<height; ++row) {
            this.landMap.add(new ArrayList<>());
            for (int col=0; col<width; ++col) {
                this.landMap.get(row).add(new Cell(row, col));
            }
        }
    }

    public Grid(Grid grid) {
        this.height = grid.getHeight();
        this.width = grid.getWidth();
        this.threshold = grid.getThreshold();

        this.landingSite = new Cell(grid.getLandingSite());

        for (Cell cell: grid.getTargets())
            this.targets.add(new Cell(cell));

        for (int row=0; row<this.height; ++row) {
            this.landMap.add(new ArrayList<>());
            for (Cell cell: grid.getLandMap().get(row)) {
                this.landMap.get(row).add(new Cell(cell));
            }
        }
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setThreshold(int threshold) {
        this.threshold = threshold;
    }

    public void setLandingSite(Cell cell) {
        this.landingSite = cell;
    }

    public int getHeight() {
        return this.height;
    }

    public int getThreshold() {
        return this.threshold;
    }

    public int getWidth() {
        return this.width;
    }

    public List<Cell> getTargets() {
        return this.targets;
    }

    public List<List<Cell>> getLandMap() {
        return this.landMap;
    }

    public Cell getLandingSite() {
        return this.landingSite;
    }

    public void insert2LandMap(Cell cell) {
        int row = cell.getRow();
        int col = cell.getCol();

        this.landMap.get(row).set(col, cell);
    }

    public void insert2Targets(Cell cell) {
        this.targets.add(cell);
    }

    public Cell getCell(int row, int col) {
        return this.landMap.get(row).get(col);
    }

    public Cell getCell(Cell cell) {
        return this.landMap.get(cell.getRow()).get(cell.getCol());
    }

    public int getNumOfTargets() {
        return this.targets.size();
    }

    public boolean isSource(Cell target) {
        return this.landingSite.equals(target);
    }

}
