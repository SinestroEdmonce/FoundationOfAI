package Assignment1;

import java.util.*;

/**
 * @projectName FoAI-Assignments
 * @fileName UniformCostSearch
 * @auther Qiaoyi Yin
 * @time 2019-09-19 15:46
 * @function Uniform Cost GeneralSearch
 */
public class UniformCostSearch implements GeneralSearch {
    // Land map
    private Grid landGrid;

    // Direction vector
    private int []diRow = {-1, -1, 0, 1, 1, 1, 0, -1};
    private int []diCol = {0, 1, 1, 1, 0, -1, -1, -1};
    private int numOfDi;

    // Number of targets
    private int numOfTarget;

    UniformCostSearch(Grid grid) {
        this.landGrid = grid;
        this.numOfDi = this.diRow.length;
        this.numOfTarget = this.landGrid.getNumOfTargets();
    }

    @Override
    public double pathGCost(Cell parent, Cell child) {
        if (Math.abs(parent.getRow()-child.getRow()) == 1
                && Math.abs(parent.getCol()-child.getCol()) == 1)
            return (parent.getfCost()+14);
        else
            return (parent.getfCost()+10);
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
        Queue<Cell> openPque = new PriorityQueue<>(new Comparator<Cell>() {
            @Override
            public int compare(Cell c1, Cell c2) {
                return Double.compare(c1.getfCost(), c2.getfCost());
            }
        });

        // Reset visited matrix
        boolean [][]closedIsVisited = new boolean[this.landGrid.getHeight()][this.landGrid.getWidth()];

        openPque.add(landingSite);
        while (!openPque.isEmpty()) {
            Cell currCell = openPque.poll();

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
                // Child in visited set. Skip it.
                if (closedIsVisited[child.getRow()][child.getCol()])
                    continue;

                child.setgCost(pathCost);
                child.setParent(currCell);

                openPque.add(child);
            }

            // Mark the current cell as visited
            closedIsVisited[currCell.getRow()][currCell.getCol()] = true;
        }
    }

    @Override
    public Grid getLandGrid() {
        return this.landGrid;
    }

}
