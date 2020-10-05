package nl.lvanarkel.compilercraft.compiler.parser.ast;

import net.minecraft.stats.Stat;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class AST {
    private BlockPos pos;

    AST(BlockPos pos) {
        this.pos = pos;
    }

    public BlockPos getPos() {
        return pos;
    }

    public static class Program extends AST {
        private List<Statement> statements;

        public Program(List<Statement> statements, BlockPos pos) {
            super(pos);
            this.statements = statements;
        }

        public List<Statement> getStatements() {
            return statements;
        }
    }

    public static class Statement extends AST {
        Statement(BlockPos pos) {
            super(pos);
        }
    }

    public static class IfStatement extends Statement {
        private Expression condition;
        private List<Statement> thenBody;
        private List<Statement> elseBody;


        public IfStatement(Expression condition, List<Statement> thenBody, List<Statement> elseBody, BlockPos pos) {
            super(pos);
            this.condition = condition;
            this.thenBody = thenBody;
            this.elseBody = elseBody;
        }

        public IfStatement(Expression condition, List<Statement> thenBody, BlockPos pos) {
            this(condition, thenBody, null, pos);
        }

        public Expression getCondition() {
            return condition;
        }

        public List<Statement> getThenBody() {
            return thenBody;
        }

        public List<Statement> getElseBody() {
            return elseBody;
        }
    }

    public static class AssignStatement extends Statement {
        private VarValue variable;
        private Operator operator;
        private Expression righthand;

        public AssignStatement(VarValue variable, Operator operator, Expression righthand, BlockPos pos) {
            super(pos);
            this.variable = variable;
            this.operator = operator;
            this.righthand = righthand;
        }

        public VarValue getVariable() {
            return variable;
        }

        public Operator getOperator() {
            return operator;
        }

        public Expression getRighthand() {
            return righthand;
        }
    }

    public static class VarDeclare extends Statement {
        private VarValue variable;
        private Expression defaultValue;

        public VarDeclare(VarValue variable, Expression defaultValue, BlockPos pos) {
            super(pos);
            this.variable = variable;
            this.defaultValue = defaultValue;
        }

        public VarValue getVariable() {
            return variable;
        }

        public Expression getDefaultValue() {
            return defaultValue;
        }
    }

    public static class WhileLoop extends Statement {
        private Expression condition;
        private List<Statement> body;

        public WhileLoop (Expression condition, List<Statement> body, BlockPos pos) {
            super(pos);
            this.condition = condition;
            this.body = body;
        }

        public Expression getCondition() {
            return condition;
        }

        public List<Statement> getBody() {
            return body;
        }
    }

    public static class OutStatement extends Statement {
        private Expression expression;

        public OutStatement (Expression expression, BlockPos pos) {
            super(pos);
            this.expression = expression;
        }

        public Expression getExpression() {
            return expression;
        }
    }

    public static class Expression extends AST {

        Expression(BlockPos pos) {
            super(pos);
        }
    }

    public static class UnaryExpression extends Expression {
        private Operator operator;
        private Expression expression;

        public UnaryExpression (Operator operator, Expression expression, BlockPos pos) {
            super(pos);
            this.operator = operator;
            this.expression = expression;
        }

        public Operator getOperator() {
            return operator;
        }

        public Expression getExpression() {
            return expression;
        }
    }

    public static class BinaryExpression extends Expression {
        private Operator operator;
        private Expression lefthand;
        private Expression righthand;

        public BinaryExpression (Operator operator, Expression lefthand, Expression righthand, BlockPos pos) {
            super(pos);
            this.operator = operator;
            this.lefthand = lefthand;
            this.righthand = righthand;
        }

        public Operator getOperator() {
            return operator;
        }

        public Expression getLefthand() {
            return lefthand;
        }

        public Expression getRighthand() {
            return righthand;
        }
    }

    public static class IntValue extends Expression {
        private int val;

        public IntValue(int val, BlockPos pos) {
            super(pos);
            this.val = val;
        }

        public int getVal() {
            return val;
        }
    }

    public static class BoolValue extends Expression {
        private boolean val;

        public BoolValue(boolean val, BlockPos pos) {
            super(pos);
            this.val = val;
        }

        public int getInt() {
            return val ? 1 : 0;
        }
    }

    public static class VarValue extends Expression {
        private String name;
        private Type type;

        public VarValue(String name, Type type, BlockPos pos) {
            super(pos);
            this.type = type;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public Type getType() {
            return type;
        }
    }
}
