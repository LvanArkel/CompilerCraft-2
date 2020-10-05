package nl.lvanarkel.compilercraft.compiler.parser.ast;

public enum Operator {
    ADD,
    SUB,
    MULT,
    DIV,
    AND,
    OR,
    NOT,
    EQ,
    NEQ,
    LT,
    LE,
    GE,
    GT;

    public Type getType(int side) {
        //TODO: Result
        switch (this) {
            case ADD:
                return Type.INTEGER;
            case SUB:
                return Type.INTEGER;
            case MULT:
                return Type.INTEGER;
            case DIV:
                return Type.INTEGER;
            case AND:
                return Type.BOOLEAN;
            case OR:
                return Type.BOOLEAN;
            case NOT:
                return Type.BOOLEAN;
            case EQ:
                if (side == 2) {
                    return Type.BOOLEAN;
                }
                return null;
            case NEQ:
                if (side == 2) {
                    return Type.BOOLEAN;
                }
                return null;
            case LT:
                if (side == 2) {
                    return Type.BOOLEAN;
                }
                return Type.INTEGER;
            case LE:
                if (side == 2) {
                    return Type.BOOLEAN;
                }
                return Type.INTEGER;
            case GE:
                if (side == 2) {
                    return Type.BOOLEAN;
                }
                return Type.INTEGER;
            case GT:
                if (side == 2) {
                    return Type.BOOLEAN;
                }
                return Type.INTEGER;
            default:
                return null;
        }
    }
}
