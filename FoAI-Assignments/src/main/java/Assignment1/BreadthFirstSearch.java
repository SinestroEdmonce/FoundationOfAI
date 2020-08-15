package Assignment1;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * @projectName FoAI-Assignments
 * @fileName BreadthFirstSearch
 * @auther Qiaoyi Yin
 * @time 2019-09-19 15:46
 * @function Breadth-First GeneralSearch
 */

public class BreadthFirstSearch implements GeneralSearch {
    // Land map
    private Grid landGrid;

    // Direction vector
    private int []diRow = {-1, -1, 0, 1, 1, 1, 0, -1};
    private int []diCol = {0, 1, 1, 1, 0, -1, -1, -1};
    private int numOfDi;

    // Number of targets
    private int numOfTarget;

    BreadthFirstSearch(Grid grid) {
        this.landGrid = grid;
        this.numOfDi = this.diRow.length;
        this.numOfTarget = this.landGrid.getNumOfTargets();
    }

    @Override
    public double pathGCost(Cell parent, Cell child) {
        return (parent.getfCost()+1);
    }

    @Override
    public double pathHCost(Cell child, Cell target) {
        return 0;
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

    @Override
    public boolean isTarget(Cell cell) {
        return this.landGrid.getCell(cell.getRow(), cell.getCol()).isTarget();
    }

    @Override
    public boolean saveOptimalPath2Target(Cell target) {
        if (this.landGrid.getCell(target).getParent() != null)
            return false;

        // Backtrack the optimal path
        Cell prev = target;
        do {
            // Save the optimal path
            this.landGrid.getCell(prev).setParent(prev.getParent());
        } while ((prev = prev.getParent()) != null);

        return true;
    }

    @Override
    public void search(Cell landingSite) {
        // Reset open queue
        Deque<Cell> openDeque = new LinkedList<>();

        // Reset explored set and visited matrix
        HashMap<String, Boolean> openIsExplored = new HashMap<>();
        boolean [][]closedIsVisited = new boolean[this.landGrid.getHeight()][this.landGrid.getWidth()];

        openDeque.addLast(landingSite);
        while (!openDeque.isEmpty()) {
            Cell currCell = openDeque.pollFirst();

            // Pruning
            if (this.isTarget(currCell)
                    && this.saveOptimalPath2Target(currCell)) {
                // Logging
                System.out.println(this.numOfTarget);
                this.numOfTarget -= 1;
                if (this.numOfTarget <= 0)
                    return;
            }

            // Current cell is visited. Skip it.
            if (closedIsVisited[currCell.getRow()][currCell.getCol()])
                continue;

            Deque<Cell> children = this.expand(currCell);
            while (!children.isEmpty()) {
                Cell child = children.pollFirst();

                double pathCost = this.pathGCost(currCell, child);
                // 1.Not visited before
                // 2.Currently in the open queue,
                //   meaning that this child at most has the same level as the node found in the open queue does
                // 3.Currently in the closed queue, meaning that it has been visited before
                String cellKey = child.getRow()+","+child.getCol();
                if (!closedIsVisited[child.getRow()][child.getCol()]
                        && !openIsExplored.containsKey(cellKey)) {

                    child.setgCost(pathCost);
                    child.setParent(currCell);

                    openDeque.addLast(child);
                    openIsExplored.put(cellKey, true);
                }
            }

            // Insert the current cell into explored set
            closedIsVisited[currCell.getRow()][currCell.getCol()] = true;
            // Remove the current cell from open queue
            openIsExplored.remove(currCell.getRow()+","+currCell.getCol());
        }
    }

    @Override
    public Grid getLandGrid() {
        return this.landGrid;
    }
}
