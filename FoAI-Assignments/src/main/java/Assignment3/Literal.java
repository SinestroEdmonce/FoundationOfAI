package Assignment3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @projectName FoAI-Assignments
 * @fileName Literal
 * @auther Qiaoyi Yin
 * @time 2019-11-20 09:56
 * @function Literal, the atomic sentence
 */

public class Literal {
    // Type of the literal
    private LiteralType typeOfLiteral;

    // Literal name
    private String name;

    // Whether is negated
    private boolean hasNegation;

    // Argument list
    private List<String> arguments;

    // Raw string
    private String raw;

    public Literal() {
        this.typeOfLiteral = LiteralType.UNKNOWN;
        this.name = null;
        this.hasNegation = false;
        this.arguments = null;
        this.raw = null;
    }

    public Literal(String literal, boolean isImplication) {
        this.arguments = new ArrayList<>();
        this.typeOfLiteral = LiteralType.PREDICATE;

        String []elements = literal.split("\\(|,|\\)");
        String literalName = elements[0];
        // Whether has negation
        if (isImplication) {
            if (literalName.charAt(0) == '~') {
                this.name = literalName.substring(1);
                this.hasNegation = false;
            }
            else {
                this.name = literalName;
                this.hasNegation = true;
            }
        }
        else {
            if (literalName.charAt(0) == '~') {
                this.name = literalName.substring(1);
                this.hasNegation = true;
            }
            else {
                this.name = literalName;
                this.hasNegation = false;
            }
        }

        // Store the arguments
        for (int idx=1; idx<elements.length; ++idx) {
            if (elements[idx].equals("")) {
                continue;
            }

            this.arguments.add(elements[idx]);
        }

        this.updateRaw();
    }

    /**
     * Cloned constructor
     * @param literal
     */
    public Literal(Literal literal) {
        this.arguments = new ArrayList<>();
        this.typeOfLiteral = literal.getTypeOfLiteral();

        this.name = new String(literal.getName());
        this.hasNegation = literal.isHasNegation();
        this.raw = literal.toString();

        for (String argument: literal.getArguments()) {
            this.arguments.add(new String(argument));
        }
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof Literal) {
            Literal literal = (Literal) obj;
            // Compare type
            if (this.typeOfLiteral != literal.typeOfLiteral) {
                return false;
            }
            // Compare negation
            if (this.isHasNegation() != literal.isHasNegation()) {
                return false;
            }
            // Compare literal name
            if (!this.name.equals(literal.name)) {
                return false;
            }
            // Compare arguments
            if (this.arguments.size() != literal.getArguments().size()) {
                return false;
            }
            for (int idx=0; idx<this.arguments.size(); ++idx) {
                if (!this.getArgument(idx).equals(literal.getArgument(idx))) {
                    return false;
                }
            }

            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        if (this.raw == null) {
            return "";
        }

        return this.raw;
    }

    public LiteralType getTypeOfLiteral() {
        return this.typeOfLiteral;
    }

    public List<String> getArguments() {
        return this.arguments;
    }

    public String getArgument(int index) {
        return this.arguments.get(index);
    }

    public String getName() {
        return this.name;
    }

    public boolean isHasNegation() {
        return this.hasNegation;
    }

    public void nagated() {
        this.hasNegation = !this.hasNegation;
        this.updateRaw();
    }

    public void unify(Map<String, String> substitution) {
        // Execute the unification
        for (int idx=0; idx<this.arguments.size(); ++idx) {
            if (this.isArgumentVariable(idx)) {
                if (substitution.containsKey(this.arguments.get(idx))) {
                    this.arguments.set(idx, substitution.get(this.arguments.get(idx)));
                }
            }
        }
        this.updateRaw();
    }

    private void updateRaw() {
        // Update the raw string
        StringBuilder rawBuilder = new StringBuilder();
        rawBuilder.append((this.hasNegation)? "~": "");
        rawBuilder.append(this.name);
        rawBuilder.append("(");
        for (String arg: this.arguments) {
            rawBuilder.append(arg);
            rawBuilder.append(",");
        }

        rawBuilder.replace(rawBuilder.length()-1, rawBuilder.length(), ")");
        this.raw = rawBuilder.toString();
    }

    private boolean isSameWithoutNegation(Literal other) {
        // Compare the name
        if (!this.name.equals(other.getName())) {
            return false;
        }

        // Compare the argument list
        List<String> argsMe = this.arguments;
        List<String> argsOther = other.getArguments();
        if (argsMe.size() != argsOther.size()) {
            return false;
        }

        for (int idx=0; idx<argsMe.size(); ++idx) {
            if (!argsMe.get(idx).equals(argsOther.get(idx))) {
                return false;
            }
        }

        return true;
    }

    public boolean isSame(Literal other) {
        if (this.hasNegation != other.isHasNegation()) {
            return false;
        }

        return this.isSameWithoutNegation(other);
    }

    public boolean isContradicted(Literal other) {
        if (this.hasNegation == other.isHasNegation()) {
            return false;
        }

        return this.isSameWithoutNegation(other);
    }

    public boolean isBelongTo(Sentence superset, Map<String, String> substitution) {
        for (Literal literal: superset.getLiterals()) {
            if (this.equals(literal)) {
                return true;
            }

            Literal afterUnified = new Literal(literal);
            afterUnified.unify(substitution);

            if (this.equals(afterUnified)) {
                return true;
            }
        }

        return false;
    }

    public boolean isArgumentVariable(int index) {
        if (this.arguments.get(index).length() == 1) {
            return Character.isLowerCase(this.arguments.get(index).charAt(0));
        }
        else {
            return false;
        }
    }

    public void debug(boolean isKB) {
        if (this.hasNegation) {
            System.out.print((isKB)? "\t\t~": "\t~");
        }
        else {
            System.out.print((isKB)? "\t\t": "\t");
        }
        System.out.print(this.name+": ");

        int idx=0;
        for (;idx<this.arguments.size()-1; ++idx) {
            System.out.print(this.arguments.get(idx)+",");
        }
        System.out.println(this.arguments.get(idx));
    }
}
