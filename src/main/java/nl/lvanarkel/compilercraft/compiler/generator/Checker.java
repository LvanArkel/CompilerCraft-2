package nl.lvanarkel.compilercraft.compiler.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import nl.lvanarkel.compilercraft.compiler.parser.ast.AST;
import nl.lvanarkel.compilercraft.compiler.parser.ast.AST.*;
import nl.lvanarkel.compilercraft.compiler.parser.ast.Type;

public class Checker {
    private Map<AST, Type> typemap;
    private Map<AST, Integer> offsets;
    private SymbolTable table;
    private Program root;
    private List<String> errors;

    public Checker (Program root) {
        this.typemap = new HashMap<>();
        this.offsets = new HashMap<>();
        this.table = new SymbolTable();
        this.root = root;
        this.errors = new ArrayList<>();
    }

    public void checkProgram() {
        for (Statement stmnt : root.getStatements()) {
            checkStatement(stmnt);
        }
    }

    public List<String> getErrors() {
        return errors;
    }

    public Map<AST, Integer> getOffsets() {
        return offsets;
    }

    private void checkStatement(Statement statement) {
        if (statement instanceof IfStatement) {
            checkIfStatement((IfStatement) statement);
        } else if (statement instanceof AssignStatement) {
            checkAssignStatement((AssignStatement) statement);
        } else if (statement instanceof VarDeclare) {
            checkVarDeclare((VarDeclare) statement);
        } else if (statement instanceof WhileLoop) {
            checkWhileLoop((WhileLoop) statement);
        } else if (statement instanceof OutStatement) {
            checkOutStatement((OutStatement) statement);
        }
    }

    private void checkIfStatement(IfStatement statement) {
        checkExpression(statement.getCondition());
        checkType(statement.getCondition(), Type.BOOLEAN);
        table.openScope();
        for (Statement stmnt : statement.getThenBody()) {
            checkStatement(stmnt);
        }
        table.closeScope();
        if (statement.getElseBody() != null) {
            table.openScope();
            for (Statement stmnt : statement.getElseBody()) {
                checkStatement(stmnt);
            }
            table.closeScope();
        }
    }

    private void checkAssignStatement(AssignStatement stmnt) {
        checkVarValue(stmnt.getVariable());
        checkExpression(stmnt.getRighthand());
        if (stmnt.getOperator() == null) {
            checkType(stmnt.getRighthand(), typemap.get(stmnt.getVariable()));
        } else {
            checkType(stmnt.getVariable(), stmnt.getOperator().getType(0));
            checkType(stmnt.getRighthand(), stmnt.getOperator().getType(1));
        }
    }

    private void checkVarDeclare(VarDeclare stmnt) {
        String varname = stmnt.getVariable().getName();
        Type type = stmnt.getVariable().getType();
        if (!table.put(varname, type)) {
            errors.add("pos %s: Variable already defined");
        } else {
            offsets.put(stmnt, table.getOffset(varname));
        }
    }

    private void checkWhileLoop(WhileLoop statement) {
        checkExpression(statement.getCondition());
        checkType(statement.getCondition(), Type.BOOLEAN);
        table.openScope();
        for (Statement stmnt : statement.getBody()) {
            checkStatement(stmnt);
        }
        table.closeScope();
    }

    private void checkOutStatement(OutStatement statement) {
        checkExpression(statement.getExpression());
    }

    private void checkExpression(Expression expression) {
        if (expression instanceof UnaryExpression) {
            checkUnaryExpression((UnaryExpression) expression);
        } else if (expression instanceof BinaryExpression) {
            checkBinaryExpression((BinaryExpression) expression);
        } else if (expression instanceof IntValue) {
            checkIntValue((IntValue) expression);
        } else if (expression instanceof BoolValue) {
            checkBoolValue((BoolValue) expression);
        } else if (expression instanceof VarValue) {
            checkVarValue((VarValue) expression);
        }
    }

    private void checkUnaryExpression(UnaryExpression unExpr) {
        checkExpression(unExpr.getExpression());
        Type opType = unExpr.getOperator().getType(0);
        checkType(unExpr.getExpression(), opType);
        typemap.put(unExpr, unExpr.getOperator().getType(2));
    }

    private void checkBinaryExpression(BinaryExpression binExpr) {
        checkExpression(binExpr.getLefthand());
        checkExpression(binExpr.getRighthand());
        Type opType = binExpr.getOperator().getType(0);
        if (opType == null) {
            checkType(binExpr.getRighthand(), typemap.get(binExpr.getLefthand()));
        } else {
            checkType(binExpr.getLefthand(), binExpr.getOperator().getType(0));
            checkType(binExpr.getRighthand(), binExpr.getOperator().getType(1));
        }
        typemap.put(binExpr, binExpr.getOperator().getType(2));

    }

    private void checkIntValue(IntValue intValue) {
        typemap.put(intValue, Type.INTEGER);

    }

    private void checkBoolValue(BoolValue boolValue) {
        typemap.put(boolValue, Type.BOOLEAN);

    }

    private void checkVarValue(VarValue varValue) {
        Type varType = table.type(varValue.getName());
        if (varType == null) {
            errors.add(String.format("pos %s: Variable %s not declared", varValue.getPos(), varValue.getName()));
        }
        typemap.put(varValue, varType);
        offsets.put(varValue, table.getOffset(varValue.getName()));
    }

    private void checkType(AST node, Type expected) {
        Type actual = typemap.get(node);
        if (actual != expected) {
            errors.add(String.format("pos %s: Expected type %s but got %s", node.getPos(), expected, actual));
        }
    }


}
