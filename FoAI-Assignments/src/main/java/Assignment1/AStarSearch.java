package Assignment1;

import java.util.*;

/**
 * @projectName FoAI-Assignments
 * @fileName AStarSearch
 * @auther Qiaoyi Yin
 * @time 2019-09-19 15:47
 * @function A* GeneralSearch
 */

public class AStarSearch implements GeneralSearch {
    // Land map
    private Grid landGrid;

    // Direction vector
    private int []diRow = {-1, -1, 0, 1, 1, 1, 0, -1};
    private int []diCol = {0, 1, 1, 1, 0, -1, -1, -1};
    private int numOfDi;

    // Path to targets
    private List<List<Cell>> path2Targets = new ArrayList<>();

    // Target list
    private List<Cell> targets2Found;

    // Is the target can be arrived at or not
    private boolean isArrived;

    AStarSearch(Grid grid) {
        this.landGrid = grid;
        this.numOfDi = this.diRow.length;
        this.isArrived = false;

        this.targets2Found = grid.getTargets();
    }

    @Override
    public double pathGCost(Cell parent, Cell child) {
        if (Math.abs(parent.getRow()-child.getRow()) == 1
                && Math.abs(parent.getCol()-child.getCol()) == 1)
            // Moving diagonally, considering the elevation difference
            return (parent.getgCost()+14+Math.abs(parent.getElevation()-child.getElevation()));
        else
            // Moving off-diagonally, considering the elevation difference
            return (parent.getgCost()+10+Math.abs(parent.getElevation()-child.getElevation()));
    }

    @Override
    public double pathHCost(Cell child, Cell target) {
        int xDist = Math.abs(target.getCol()-child.getCol());
        int yDist = Math.abs(target.getRow()-child.getRow());

        double horizontalHCost = Math.min(xDist, yDist)*14+(Math.max(xDist, yDist)-Math.min(xDist, yDist))*10;
        double verticalHCost = Math.abs(target.getElevation()-child.getElevation());

        return (horizontalHCost+verticalHCost);
    }

    @Override
    public Deque<Cell> expand(Cell currCell) {
        Deque<Cell> validChildren = new LinkedList<>();

        for (int diIdx=0; diIdx<numOfDi; ++diIdx) {
            int nextRow = currCell.getRow()+this.diRow[diIdx];
            int nextCol = currCell.getCol()+this.diCol[diIdx];

            // Boundary check & No loop check
            if (nextRow >= 0 && nextRow < this.landGrid.getHeight()
                    && nextCol >= 0 && nextCol < this.landGrid.getWidth()
                    && !this.landGrid.getCell(nextRow, nextCol).equals(currCell.getParent())) {

                // Threshold check
                int elevationDiff = Math.abs(this.landGrid.getCell(nextRow, nextCol).getElevation()-currCell.getElevation());
                if (elevationDiff <= this.landGrid.getThreshold())
                    validChildren.addLast(new Cell(this.landGrid.getCell(nextRow, nextCol)));
            }
        }

        return validChildren;
    }

    public boolean saveOptimalPath2Target(Cell target) {
        List<Cell> optimalPath = new ArrayList<>();

        // Backtrack the optimal path
        Cell prev = target;
        do {
            optimalPath.add(prev);
        } while ((prev = prev.getParent()) != null);

        // Save the optimal path
        this.path2Targets.add(optimalPath);

        return true;
    }

    @Override
    public void search(Cell landingSite) {
        for (Cell curTarget: this.targets2Found) {
            // Reset reachable status
            this.isArrived = false;

            // Reset open queue
            Queue<Cell> openPque = new PriorityQueue<>(new Comparator<Cell>() {
                @Override
                public int compare(Cell c1, Cell c2) {
                    return Double.compare(c1.getfCost(), c2.getfCost());
                }
            });

            // Reset visited set
            boolean [][]closedIsVisited = new boolean[this.landGrid.getHeight()][this.landGrid.getWidth()];

            openPque.add(landingSite);
            while (!openPque.isEmpty()) {
                Cell currCell = openPque.poll();

                // Pruning
                if (currCell.equals(curTarget)) {
                    // Logging
                    System.out.println(curTarget);
                    this.isArrived = true;
                    this.saveOptimalPath2Target(currCell);
                    break;
                }

                // Current cell is visited. Skip it.
                if (closedIsVisited[currCell.getRow()][currCell.getCol()])
                    continue;

                Deque<Cell> children = this.expand(currCell);
                while (!children.isEmpty()) {
                    Cell child = children.pollFirst();

                    double gCost = this.pathGCost(currCell, child);
                    double hCost =this.pathHCost(child, curTarget);
                    // Whether under estimated or not
                    boolean isUnderEstimatd = (gCost+hCost < currCell.getfCost());

                    // Child in visited set. Skip it.
                    // if (closedIsVisited[child.getRow()][child.getCol()])
                    //     continue;

                    child.setgCost(gCost);
                    child.sethCost(((isUnderEstimatd)? currCell.getfCost()-gCost: hCost));
                    child.setParent(currCell);

                    openPque.add(child);
                }

                // Mark the current cell as visited
                closedIsVisited[currCell.getRow()][currCell.getCol()] = true;
            }

            // If the target cannot be arrived at, save the target with its parent being 'null'
            if (!this.isArrived)
                this.saveOptimalPath2Target(curTarget);
        }
    }

    public List<List<Cell>> getPath2Targets() {
        return this.path2Targets;
    }

    @Override
    public boolean isTarget(Cell cell) {
        return this.landGrid.getCell(cell.getRow(), cell.getCol()).isTarget();
    }

    @Override
    public Grid getLandGrid() {
        return this.landGrid;
    }

}
