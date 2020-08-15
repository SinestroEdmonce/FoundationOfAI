package Assignment3;

import java.util.*;

/**
 * @projectName FoAI-Assignments
 * @fileName KnowledgeBase
 * @auther Qiaoyi Yin
 * @time 2019-11-20 09:55
 * @function KnowledgeBase, used to store all the sentences
 */

public class KnowledgeBase {
    private List<Sentence> sentences;
    private Map<String, ArrayList<Integer>> literalIndexingMap;
    private Map<String, Integer> sentenceIndexingMap;

    public KnowledgeBase() {
        this.sentences = new ArrayList<>();
        this.literalIndexingMap = new HashMap<>();
        this.sentenceIndexingMap = new HashMap<>();
    }

    /**
     * Cloned Constructor
     * @param KB
     */
    public KnowledgeBase(KnowledgeBase KB) {
        this.sentences = new ArrayList<>();
        this.literalIndexingMap = new HashMap<>();
        this.sentenceIndexingMap = new HashMap<>();

        for (int idx=0; idx<KB.getSentences().size(); ++idx) {
            Sentence cloned = new Sentence(KB.getSentence(idx));
            this.add(cloned);
        }
    }

    public void add(Sentence sentence) {
        this.sentences.add(sentence);
        int index = this.sentences.size()-1;

        // Store the indexing
        this.sentenceIndexingMap.put(sentence.toString(), index);
        for (Literal literal: sentence.getLiterals()) {
            ArrayList<Integer> indices = this.literalIndexingMap.getOrDefault(literal.getName(), new ArrayList<>());
            // Avoid duplicates
            if (indices.size() == 0 || indices.get(indices.size()-1) != index) {
                indices.add(index);
            }

            this.literalIndexingMap.put(literal.getName(), indices);
        }
    }

    public List<Sentence> getSentences() {
        return this.sentences;
    }

    public Sentence getSentence(int index) {
        return this.sentences.get(index);
    }

    public Map<String, ArrayList<Integer>> getLiteralIndexingMap() {
        return this.literalIndexingMap;
    }

    public ArrayList<Integer> getIndice(Literal literal) {
        return this.literalIndexingMap.getOrDefault(literal.getName(), new ArrayList<>());
    }

    public int size() {
        return this.sentences.size();
    }

    public Map<String, Integer> getSentenceIndexingMap() {
        return this.sentenceIndexingMap;
    }

    public boolean contains(Sentence sentence) {
        return this.sentenceIndexingMap.containsKey(sentence.toString());
    }

    public void debug() {
        System.out.println("KB: ");

        // Output all DNF form
        System.out.println("\t"+"DNF Forms:");
        for (Sentence sentence: this.sentences) {
            System.out.println("\t"+sentence.toString());
        }
        System.out.println();
    }

    public boolean []query(List<Sentence> queries) {
        boolean []results = new boolean[queries.size()];
        for (int idx=0; idx<queries.size(); ++idx) {
            results[idx] = this.query(queries.get(idx));
            System.out.println("Query #"+(idx+1)+": "+results[idx]);
        }

        return results;
    }

    private boolean query(Sentence querySentence) {
        // Cloned and add the new query's negation into the KB
        KnowledgeBase extraKB = new KnowledgeBase(this);
        querySentence.negated();
        extraKB.add(querySentence);

        // Add the new query to the queue
        Queue<Sentence> sentenceQueue = new LinkedList<>();
        sentenceQueue.add(querySentence);
        // Maintain a set to keep the queue possessing no duplicates
        Set<String> existInQueue = new HashSet<>();
        existInQueue.add(querySentence.toString());
        // Core Algorithm: query
        while (!sentenceQueue.isEmpty()) {
            Sentence currentQuery = sentenceQueue.remove();

            // Use resolution to check all the sentences in KB
            List<Sentence> add2KB = new ArrayList<>();
            for (Literal literal: currentQuery.getLiterals()) {
                // Check all the sentences in KB that contains the given literal
                for (int index : extraKB.getIndice(literal)) {
                    // Locate the sentence
                    Sentence sentence = extraKB.getSentence(index);

                    // Obtain the all the resolution results
                    List<Sentence> results = extraKB.resolution(sentence, currentQuery, literal);
                    // Check termination constraint
                    // Check whether the new sentence is a superset of some set in KB
                    for (Sentence res : results) {
                        // If the two sentences are contradicted with each other,
                        // we can prove the query by contradiction,
                        // because the result sentence from the resolution will be empty.
                        if (res.isEmpty()) {
                            return true;
                        }

                        // If KB does not contain the new sentence
                        // If the new sentence is not a tautology
                        if (!extraKB.contains(res) && !res.isTautology()) {
                            add2KB.add(res);
                        }
                    }
                }
            }

            // Add all pre-check resolution results to KB
            for (Sentence newSentences: add2KB) {
                extraKB.add(newSentences);
            }

            // KB check all sentences to remove super sets
            // KB only reserves those effective sentences
            extraKB = extraKB.checkSuperSet();

            // Add those reserved sentence to the queue
            // And those reserved sentence cannot appear in the queue before
            for (Sentence newSentence: add2KB) {
                if (extraKB.contains(newSentence) && !existInQueue.contains(newSentence.toString())) {
                    sentenceQueue.add(newSentence);
                    existInQueue.add(newSentence.toString());
                }
            }
        }

        return false;
    }

    private KnowledgeBase checkSuperSet() {
        // Cleaned KB
        KnowledgeBase cleanKB = new KnowledgeBase();

        // Check all the sentences in KB
        // Only reserve those sentences that are not others' super set
        // Record those non-reserved sentences' index
        Set<Integer> nonReserved = new HashSet<>();
        for (int i1=0; i1<this.sentences.size(); ++i1) {
            for (int j2=0; j2<this.sentences.size(); ++j2) {
                // Itself or it has already been removed
                if (j2 == i1 || nonReserved.contains(j2)) {
                    continue;
                }

                // Check whether others are a super set of itself
                if (this.isSuperSet(this.getSentence(i1), this.getSentence(j2))) {
                    // If sentences[i1] is a super set of sentences[j2]
                    nonReserved.add(i1);
                    break;
                }
            }
        }

        // Add all reserved sentences to the cleaned KB
        for (int idx=0; idx<this.sentences.size(); ++idx) {
            if (!nonReserved.contains(idx)) {
                cleanKB.add(this.getSentence(idx));
            }
        }

        return cleanKB;
    }

    private boolean isSuperSet(Sentence itself, Sentence other) {
        boolean isSuper = true;
        for (Literal otherLiteral: other.getLiterals()) {
            // Whether there exist a same literal in other
            boolean isSame = false;
            // Check all the literals in itself
            for (Literal itselfLiteral: itself.getLiterals()) {
                if (itselfLiteral.equals(otherLiteral)) {
                    isSame = true;
                    break;
                }
            }

            // If no mapping for the current new literal
            if (!isSame) {
                isSuper = false;
                break;
            }
        }

        return isSuper;
    }

    private List<Sentence> resolution(Sentence sentence, Sentence query, Literal currentLiteral) {
        List<Sentence> results = new ArrayList<>();
        for (int index: sentence.getIndices(currentLiteral)) {
            if (sentence.getLiteral(index).isHasNegation() != currentLiteral.isHasNegation()) {
                // Do the unification
                Map<String, String> substitution = new HashMap<>();
                if (this.unify(sentence.getLiteral(index), currentLiteral, substitution)) {

                    // Obtain the query literal after unification
                    Literal queryAfterUnified = new Literal(currentLiteral);
                    queryAfterUnified.unify(substitution);
                    // Construct new sentence after resolution
                    // Concatenate two parts
                    Sentence res = new Sentence();
                    // Add query's parts
                    for (Literal literal : query.getLiterals()) {
                        // If the literal is same with the query
                        if (literal.equals(currentLiteral)) {
                            continue;
                        }

                        Literal newLiteral = new Literal(literal);
                        // Do the unification along all the literals
                        newLiteral.unify(substitution);

                        // If the new literal is same with the query after the unification
                        // We drop the new literal
                        if (!queryAfterUnified.equals(newLiteral)) {
                            res.add(newLiteral);
                        }
                    }

                    // Obtain the KB literal after unification
                    Literal kbAfterUnified = new Literal(sentence.getLiteral(index));
                    kbAfterUnified.unify(substitution);
                    // Add KB's parts
                    for (int idx = 0; idx < sentence.getLiterals().size(); ++idx) {
                        // If the literal is same with the KB-literal
                        if (sentence.getLiteral(index).equals(sentence.getLiteral(idx))) {
                            continue;
                        }

                        Literal newLiteral = new Literal(sentence.getLiteral(idx));
                        // Do the unification along all the literals
                        newLiteral.unify(substitution);

                        // If the new literal is same with the KB-literal after the unification
                        if (!kbAfterUnified.equals(newLiteral)) {
                            res.add(newLiteral);
                        }
                    }

                    // Unify itself
                    // this.unifySelf(res);

                    // Delete duplicated literals
                    res = this.reduceDuplicatedLiterals(res);

                    results.add(res);
                }
            }
        }

        return results;
    }

    private void unifySelf(Sentence sentence) {
        for (int i1=0; i1<sentence.size(); ++i1) {
            for (int j2=i1+1; j2<sentence.size(); ++j2) {
                Map<String, String> substitution = new HashMap<>();
                Literal l1 = sentence.getLiteral(i1);
                Literal l2 = sentence.getLiteral(j2);
                // If can unify
                if (this.canUnify(l1, l2)) {
                    if (this.unify(l1, l2, substitution)) {
                        for (Literal literal: sentence.getLiterals()) {
                            if (this.canUnify(l1, literal)) {
                                literal.unify(substitution);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean canUnify(Literal l1, Literal l2) {
        return l1.getName().equals(l2.getName()) && l1.isHasNegation() != l2.isHasNegation();
    }

    private Sentence reduceDuplicatedLiterals(Sentence sentence) {
        Set<Integer> nonReserved = new HashSet<>();
        List<Literal> literals = sentence.getLiterals();
        for (int i1=0; i1<literals.size()-1; ++i1) {
            for (int j2=i1+1; j2<literals.size(); ++j2) {
                if (literals.get(i1).equals(literals.get(j2))) {
                    nonReserved.add(i1);
                    break;
                }
            }
        }

        Sentence reserved = new Sentence();
        for (int idx=0; idx<literals.size(); ++idx) {
            if (!nonReserved.contains(idx)) {
                reserved.add(literals.get(idx));
            }
        }

        return reserved;
    }

    private boolean isVariable(String arg) {
        if (arg.length() > 1) {
            return false;
        }
        else {
            return Character.isLowerCase(arg.charAt(0));
        }
    }

    private boolean unify(Literal kb, Literal query, Map<String, String> subs) {
        List<String> argsKB = kb.getArguments();
        List<String> argsQuery = query.getArguments();

        if (argsKB.size() != argsQuery.size()) {
            return false;
        }

        // Obtain the initial substitution
        for (int idx=0; idx<argsKB.size(); ++idx) {
            if (!this.unify(argsKB.get(idx), argsQuery.get(idx), subs)) {
                return false;
            }
        }

        // Obtain the final and global substitution
        for (Map.Entry<String, String> entry: subs.entrySet()) {
            String val = entry.getValue();
            while (this.isVariable(val) && subs.containsKey(val)) {
                // Recursively look for a constant
                val = subs.get(val);
            }
            entry.setValue(val);
        }

        return true;
    }

    private boolean unify(String kb, String query, Map<String, String> substitution) {
        if (kb.equals(query)) {
            return true;
        }
        else if (this.isVariable(kb)) {
            return this.unifyVar(kb, query, substitution);
        }
        else if (this.isVariable(query)) {
            return this.unifyVar(query, kb, substitution);
        }
        else {
            substitution.clear();
            return false;
        }
    }

    private boolean unifyVar(String var, String other, Map<String, String> substitution) {
        if (substitution.containsKey(var)) {
            return this.unify(substitution.get(var), other, substitution);
        }
        else if (substitution.containsKey(other)) {
            return this.unify(var, substitution.get(other), substitution);
        }
        else {
            substitution.put(var, other);
            return true;
        }
    }

}
