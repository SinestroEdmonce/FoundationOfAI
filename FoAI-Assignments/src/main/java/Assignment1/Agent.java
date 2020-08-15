package Assignment1;

import java.io.*;

import java.util.*;

/**
 * @projectName FoAI-Assignments
 * @fileName Agent
 * @auther Qiaoyi Yin
 * @time 2019-09-19 15:46
 * @function Main entrance
 */

class DataIO {

    class Coordinates {
        private int row;
        private int col;

        public Coordinates(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public void setRow(int row) {
            this.row = row;
        }

        public void setCol(int col) {
            this.col = col;
        }

        public int getRow() {
            return this.row;
        }

        public int getCol() {
            return this.col;
        }
    }

    private String typeOfSearch;

    public Grid readFile2Grid(String fileName) {
        // To be initialized
        Grid grid = new Grid(0, 0);
        Coordinates landingSite = new Coordinates(0, 0);
        List<Coordinates> targets = new ArrayList<>();

        // Read input data from a file
        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            // Read by line
            String line = bufferedReader.readLine();
            // Read the type of selected searching
            if (line != null)
                this.typeOfSearch = line;

            // Read the size
            line = bufferedReader.readLine();
            if (line != null) {
                String []size = line.split("[\\s\\t\\n]+");
                grid = new Grid(Integer.parseInt(size[1]), Integer.parseInt(size[0]));
            }

            // Read the landing site
            line = bufferedReader.readLine();
            if (line != null) {
                String []size = line.split("[\\s\\t\\n]+");
                landingSite.setRow(Integer.parseInt(size[1]));
                landingSite.setCol(Integer.parseInt(size[0]));
            }

            // Read the threshold for the maximum of difference between elevations
            line = bufferedReader.readLine();
            int threshold = ((line != null)? Integer.parseInt(line): 0);
            grid.setThreshold(threshold);

            // Read the number of targets
            line = bufferedReader.readLine();
            int numOfTargets = ((line != null)? Integer.parseInt(line): 0);

            // Read targets' coordinates
            while (numOfTargets > 0 && (line = bufferedReader.readLine()) != null) {
                String []position = line.split("[\\s\\t\\n]+");
                targets.add(new Coordinates(Integer.parseInt(position[1]), Integer.parseInt(position[0])));
                numOfTargets -= 1;
            }

            // Read land map
            int row = 0;
            while ((line = bufferedReader.readLine()) != null && row < grid.getHeight()) {
                String []elevations = line.split("[\\s\\t\\n]+");
                for (int col=0; col<elevations.length; ++col) {
                    grid.insert2LandMap(new Cell(row, col, Integer.parseInt(elevations[col])));
                }
                row += 1;
            }

            // Set landing site
            grid.setLandingSite(grid.getCell(landingSite.getRow(), landingSite.getCol()));
            // Set targets;
            for (Coordinates target: targets) {
                grid.getCell(target.getRow(), target.getCol()).setTarget(true);
                grid.insert2Targets(grid.getCell(target.getRow(), target.getCol()));
            }

            // Close the file's input stream
            bufferedReader.close();
            fileReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return grid;
    }

    public void writePaths2File(Grid grid, String fileName) {

        try {
            File file = new File(fileName);
            if (!file.exists())
                file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (Cell target: grid.getTargets() ) {
                if (target.getParent() == null && !grid.isSource(target)) {
                    // FAIL is an end or not
                    if (grid.getTargets().indexOf(target) == grid.getTargets().size()-1)
                        bufferedWriter.write("FAIL");
                    else
                        bufferedWriter.write("FAIL\n");
                }
                else {
                    List<Coordinates> coordinatesList = new ArrayList<>();

                    // Backtrack the optimal path
                    Cell prev = target;
                    do {
                        coordinatesList.add(new Coordinates(prev.getRow(), prev.getCol()));
                    } while ((prev = prev.getParent()) != null);

                    // Write the path
                    StringBuilder path = new StringBuilder();
                    for (int idx=coordinatesList.size()-1; idx>=0; --idx) {
                        path.append(coordinatesList.get(idx).getCol());
                        path.append(",");
                        path.append(coordinatesList.get(idx).getRow());
                        path.append(" ");
                    }
                    // Replace the last whitespace with "\n" or ""
                    if (grid.getTargets().indexOf(target) == grid.getTargets().size()-1)
                        path.replace(path.length()-1, path.length(), "");
                    else
                        path.replace(path.length()-1, path.length(), "\n");
                    bufferedWriter.write(path.toString());
                }
            }

            // Close the file's output stream
            bufferedWriter.close();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writePaths2File(List<List<Cell>> path2Targets, Grid grid, String fileName) {

        try {
            File file = new File(fileName);
            if (!file.exists())
                file.createNewFile();

            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            for (List<Cell> path: path2Targets) {
                Cell curTarget = path.get(0);
                if (curTarget.getParent() == null && !grid.isSource(curTarget)) {
                    // FAIL is an end or not
                    if (path2Targets.indexOf(path) == path2Targets.size()-1)
                        bufferedWriter.write("FAIL");
                    else
                        bufferedWriter.write("FAIL\n");
                }
                else {
                    StringBuilder optimalPath = new StringBuilder();
                    for (int idx=path.size()-1; idx>=0; --idx) {
                        // Write the path
                        optimalPath.append(path.get(idx).getCol());
                        optimalPath.append(",");
                        optimalPath.append(path.get(idx).getRow());
                        optimalPath.append(" ");
                    }

                    // Replace the last whitespace with "\n" or ""
                    if (path2Targets.indexOf(path) == path2Targets.size()-1)
                        optimalPath.replace(optimalPath.length()-1, optimalPath.length(), "");
                    else
                        optimalPath.replace(optimalPath.length()-1, optimalPath.length(), "\n");
                    bufferedWriter.write(optimalPath.toString());
                }
            }

            // Close the file's output stream
            bufferedWriter.close();
            fileWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTypeOfSearch() {
        return this.typeOfSearch;
    }

}

public class Agent {

    public static void main(String []args) {
        DataIO dataIO = new DataIO();

        Grid landGrid = dataIO.readFile2Grid("sources/input21.txt");
        String typeOfSearch = dataIO.getTypeOfSearch();
        // Select different searching method
        try {
            switch (typeOfSearch) {
                case "BFS": {
                    BreadthFirstSearch BFS = new BreadthFirstSearch(landGrid);
                    BFS.search(landGrid.getLandingSite());

                    // Output result
                    dataIO.writePaths2File(landGrid, "results/output21.txt");
                    break;
                }
                case "UCS": {
                    UniformCostSearch UCS = new UniformCostSearch(landGrid);
                    UCS.search(landGrid.getLandingSite());

                    // Output result
                    dataIO.writePaths2File(landGrid, "results/output21.txt");
                    break;
                }
                case "A*": {
                    AStarSearch AStarS = new AStarSearch(landGrid);
                    AStarS.search(landGrid.getLandingSite());

                    // Output result
                    dataIO.writePaths2File(AStarS.getPath2Targets(), landGrid, "results/output21.txt");
                    break;
                }
                default:
                    throw new Exception();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
