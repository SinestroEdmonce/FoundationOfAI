package Assignment3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;

import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @projectName FoAI-Assignments
 * @fileName Agent
 * @auther Qiaoyi Yin
 * @time 2019-11-20 09:54
 * @function Agent to do the query
 */

class DataIO {
    // Knowledge Base
    private KnowledgeBase KB;
    private int numOfKB;

    // Queries
    private List<Sentence> queries;
    private int numOfQueries;

    // Input file name
    private String inFile;

    // Output file name;
    private String outFile;

    // Constructor
    DataIO (String in, String out) {
        this.KB = new KnowledgeBase();
        this.queries = new ArrayList<>();

        this.inFile = in;
        this.outFile = out;
    }

    public int getNumOfKB() {
        return this.numOfKB;
    }

    public int getNumOfQueries() {
        return this.numOfQueries;
    }

    public KnowledgeBase getKB() {
        return this.KB;
    }

    public List<Sentence> getQueries() {
        return this.queries;
    }

    // Read file
    public void readFile() {
        try {
            FileReader fileReader = new FileReader(this.inFile);
            BufferedReader in = new BufferedReader(fileReader);
            String line = null;
            int count = 0;

            // Read the number of queries
            line = in.readLine();
            this.numOfQueries = Integer.parseInt(line);
            // Read the queries
            while (count < numOfQueries && ((line = in.readLine()) != null)) {
                Sentence query = new Sentence(line);
                this.queries.add(query);
                ++count;
            }

            // Read the number of given sentences in the KB
            count = 0;
            line = in.readLine();
            this.numOfKB = Integer.parseInt(line);
            // Read the sentences
            while (count < numOfKB && ((line = in.readLine()) != null)) {
                Sentence sentence = new Sentence(line);
                this.KB.add(sentence);
                ++count;
            }

            in.close();
            fileReader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Write file
    public void writeFile(boolean []results) {
        try {
            FileWriter fileWriter = new FileWriter(this.outFile);
            BufferedWriter out = new BufferedWriter(fileWriter);

            int idx = 0;
            for (; idx<results.length-1; ++idx) {
                out.write((results[idx])? "TRUE\n": "FALSE\n");
            }
            out.write((results[idx])? "TRUE": "FALSE");

            out.close();
            fileWriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class Agent {
    public static void main(String[] args) {
        // Initialize the KB
        DataIO dataIO = new DataIO("sources/input_26.txt", "results/output_26.txt");
        dataIO.readFile();

        KnowledgeBase KB = dataIO.getKB();
        List<Sentence> queries = dataIO.getQueries();

        KB.debug();
        for (Sentence query: queries) {
            query.debug(false);
        }

        boolean []ret = KB.query(queries);
        dataIO.writeFile(ret);
    }
}
