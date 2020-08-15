package Assignment1;

import java.util.*;

/**
 * @projectName FoAI-Assignments
 * @fileName GeneralSearch
 * @auther Qiaoyi Yin
 * @time 2019-09-20 20:03
 * @function Interface for General Search
 */

public interface GeneralSearch {

    double pathGCost(Cell parent, Cell child);

    double pathHCost(Cell child, Cell target);

    boolean saveOptimalPath2Target(Cell target);

    Deque<Cell> expand(Cell currCell);

    void search(Cell landingsite);

    boolean isTarget(Cell cell);

    Grid getLandGrid();
}
