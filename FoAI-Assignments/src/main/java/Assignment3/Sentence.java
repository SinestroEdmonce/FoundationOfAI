package Assignment3;

import java.util.*;

/**
 * @projectName FoAI-Assignments
 * @fileName Sentence
 * @auther Qiaoyi Yin
 * @time 2019-11-20 09:56
 * @function Sentences, used in KB
 */

public class Sentence {
    // Literals
    private List<Literal> literals;

    // Indexing
    private Map<String, ArrayList<Integer>> IndexingMap;

    public Sentence() {
        this.literals = new ArrayList<>();
        this.IndexingMap = new HashMap<>();
    }

    public Sentence(String sentence) {
        // Initialize
        this.literals = new ArrayList<>();
        this.IndexingMap = new HashMap<>();

        // Split
        String []literalStrings = sentence.split(" & | => ");
        // Is implication or not
        boolean isImplication = sentence.contains("=>");
        int idx = 0;
        for (; idx<literalStrings.length-1; ++idx) {
            // Avoid empty string
            if (literalStrings[idx].equals("")) {
                continue;
            }

            // Obtain the literal
            Literal literal = new Literal(literalStrings[idx], isImplication);
            this.literals.add(literal);
            // Construct the indexing
            ArrayList<Integer> indices = this.IndexingMap.getOrDefault(literal.getName(), new ArrayList<>());
            indices.add(idx);
            this.IndexingMap.put(literal.getName(), indices);
        }

        // Obtain the literal that is right to '=>'
        // Or obtain the atomic sentence
        Literal literal = new Literal(literalStrings[idx], false);
        this.literals.add(literal);
        // Construct the indexing
        ArrayList<Integer> indices = this.IndexingMap.getOrDefault(literal.getName(), new ArrayList<>());
        indices.add(idx);
        this.IndexingMap.put(literal.getName(), indices);
    }

    /**
     * Cloned Constructor
     * @param sentence
     */
    public Sentence(Sentence sentence) {
        // Initialize
        this.literals = new ArrayList<>();
        this.IndexingMap = new HashMap<>();

        // Clone the literals and indexing
        int idx = 0;
        for (Literal literal: sentence.getLiterals()) {
            Literal cloned = new Literal(literal);
            this.literals.add(cloned);

            // Construct the indexing
            ArrayList<Integer> indices = this.IndexingMap.getOrDefault(cloned.getName(), new ArrayList<>());
            indices.add(idx);
            this.IndexingMap.put(cloned.getName(), indices);

            // Update index
            ++idx;
        }
    }

    public void add(Literal literal) {
        // Update literal list
        this.literals.add(literal);
        int index = this.literals.size()-1;

        // Construct the indexing
        ArrayList<Integer> indices = this.IndexingMap.getOrDefault(literal.getName(), new ArrayList<>());
        indices.add(index);
        this.IndexingMap.put(literal.getName(), indices);
    }

    public void negated() {
        for (Literal literal: this.literals) {
            literal.nagated();
        }
    }

    public int size() {
        return this.literals.size();
    }

    public boolean isEmpty() {
        return this.literals.isEmpty();
    }

    public List<Literal> getLiterals() {
        return this.literals;
    }

    public Literal getLiteral(int index) {
        return this.literals.get(index);
    }

    public Map<String, ArrayList<Integer>> getIndexingMap() {
        return this.IndexingMap;
    }

    public ArrayList<Integer> getIndices(Literal literal) {
        return this.IndexingMap.getOrDefault(literal.getName(), new ArrayList<>());
    }

    public void debug(boolean isKB) {
        if (isKB) {
            System.out.println("\tSentence: ");
        }
        else {
            System.out.println("Query: ");
        }
        for (Literal literal : this.literals) {
            literal.debug(isKB);
        }
    }

    public boolean isTautology() {
        // Check whether it is a tautology
        for (int i1=0; i1<this.literals.size()-1; ++i1) {
            boolean isValid = false;
            for (int j2=i1+1; j2<this.literals.size(); ++j2) {
                if (this.getLiteral(i1).isContradicted(this.getLiteral(j2))) {
                    isValid = true;
                    break;
                }
            }

            if (isValid) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        if (this.literals.isEmpty()) {
            return "";
        }

        StringBuilder sentence = new StringBuilder();
        int idx = 0;
        for (; idx<this.literals.size()-1; ++idx) {
            sentence.append(this.literals.get(idx).toString());
            sentence.append("|");
        }
        sentence.append(this.literals.get(idx).toString());

        return sentence.toString();
    }
}
