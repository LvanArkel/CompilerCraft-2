package nl.lvanarkel.compilercraft.compiler.parser;

import net.minecraft.util.math.BlockPos;
import nl.lvanarkel.compilercraft.compiler.datastructures.Token;
import nl.lvanarkel.compilercraft.compiler.datastructures.TokenType;
import nl.lvanarkel.compilercraft.compiler.parser.ast.AST.*;
import nl.lvanarkel.compilercraft.compiler.parser.ast.Operator;
import nl.lvanarkel.compilercraft.compiler.parser.ast.Type;
import nl.lvanarkel.compilercraft.util.BlockPosUtils;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private int index;
    private Token[] tokens;

    private Parser(Token[] tokens) {
        this.tokens = tokens;
        this.index = 0;
    }

    public Parser(List<Token> tokenList) {
        this(tokenList.toArray(new Token[tokenList.size()]));
    }

    private Token lookahead() {
        if (index >= tokens.length) {
            return null;
        }
        return tokens[index];
    }

    private void addError(BlockPos pos, String message) {
        throw new IllegalStateException(String.format("Parse error at position %s: %s", BlockPosUtils.formatBlockPos(pos), message));
    }

    private boolean isDone() {
        return index >= tokens.length;
    }

    private Token shift(int amount) {
        index += amount;
        return lookahead();
    }

    private Token parseToken(TokenType type) {
        Token token = lookahead();
        if (token == null) {
            addError(tokens[tokens.length-1].getPos(), "Unexpected end of program");
        }
        if (token.getType() == type) {
            shift(1);
            return token;
        } else {
            addError(token.getPos(), String.format("Expected %s token but got %s", type, token.getType()));
            return null;
        }
    }

    public Program parseProgram() {
        List<Statement> statements = new ArrayList<>();
        while (!isDone()) {
            statements.add(parseStatement());
        }
        return new Program(statements, statements.get(0).getPos());
    }

    private Statement parseStatement() {
        switch (lookahead().getType()) {
            case IF:
                {
                    //IF expr THEN BROPEN stmt* BRCLOSE (ELSE BROPEN stmt* BRCLOSE)?
                    Token iftoken = parseToken(TokenType.IF);
                    Expression condition = parseExpression();
                    parseToken(TokenType.THEN);
                    parseToken(TokenType.BROPEN);
                    List<Statement> stmnts = new ArrayList<>();
                    while(!(lookahead().getType() == TokenType.BRCLOSE)) {
                        stmnts.add(parseStatement());
                    }
                    parseToken(TokenType.BRCLOSE);
                    if (lookahead() == null || lookahead().getType() != TokenType.ELSE) {
                        return new IfStatement(condition, stmnts, iftoken.getPos());
                    } else {
                        parseToken(TokenType.ELSE);
                        parseToken(TokenType.BROPEN);
                        List<Statement> elseStmnts = new ArrayList<>();
                        while(!(lookahead().getType() == TokenType.BRCLOSE)) {
                            elseStmnts.add(parseStatement());
                        }
                        parseToken(TokenType.BRCLOSE);
                        return new IfStatement(condition, stmnts, elseStmnts, iftoken.getPos());
                    }
                }
            case VAR:
                VarValue val = parseVar();
                Operator operator = parseOperator();
                Expression righthand = parseExpression();
                return new AssignStatement(val, operator, righthand, val.getPos());
            case INTVAR:
            case BOOLVAR:
                VarValue varValue = parseVar();
                Expression value = parseLiteral();
                return new VarDeclare(varValue, value, varValue.getPos());
            case WHILE:
                Token whileToken = parseToken(TokenType.WHILE);
                Expression condition = parseExpression();
                parseToken(TokenType.THEN);
                parseToken(TokenType.BROPEN);
                List<Statement> stmnts = new ArrayList<>();
                while(!(lookahead().getType() == TokenType.BRCLOSE)) {
                    stmnts.add(parseStatement());
                }
                parseToken(TokenType.BRCLOSE);
                return new WhileLoop(condition, stmnts, whileToken.getPos());
            case OUT:
                Token outToken = parseToken(TokenType.OUT);
                Expression expr = parseExpression();
                return new OutStatement(expr, outToken.getPos());
            default:
                addError(lookahead().getPos(), "Is not a statement token");
                break;
        }
        return null;
    }

    private Operator parseOperator() {
        Operator op;
        switch (lookahead().getType()) {
            case ASSIGN:
                op = null;
                break;
            case ADD:
            case ADDASSIGN:
                op = Operator.ADD;
                break;
            case SUB:
            case SUBASSIGN:
                op = Operator.SUB;
                break;
            case MULT:
            case MULTASSIGN:
                op = Operator.MULT;
                break;
            case DIV:
            case DIVASSIGN:
                op = Operator.DIV;
                break;
            case AND:
            case ANDASSIGN:
                op = Operator.AND;
                break;
            case OR:
            case ORASSIGN:
                op = Operator.OR;
                break;
            case NOT:
                op = Operator.NOT;
                break;
            case EQ:
                op = Operator.EQ;
                break;
            case NEQ:
                op = Operator.NEQ;
                break;
            case LT:
                op = Operator.LT;
                break;
            case LE:
                op = Operator.LE;
                break;
            case GE:
                op = Operator.GE;
                break;
            case GT:
                op = Operator.GT;
                break;
            default:
                addError(lookahead().getPos(), "Is not an operator");
                return null;
        }
        shift(1);
        return op;
    }

    Expression parseLiteral() {
        TokenType type = lookahead().getType();
        Token var;
        switch (type) {
            case INT:
                var = parseToken(TokenType.INT);
                return new IntValue(Integer.parseInt(var.getValue()), var.getPos());
            case BOOL:
                var = parseToken(TokenType.BOOL);
                return new BoolValue(Boolean.parseBoolean(var.getValue()), var.getPos());
        }
        //TODO: Add error
        return null;
    }

    VarValue parseVar() {
        TokenType type = lookahead().getType();
        Token var;
        switch (type) {
            case VAR:
                var = parseToken(TokenType.VAR);
                return new VarValue(var.getValue(), null, var.getPos());
            case INTVAR:
                var = parseToken(TokenType.INTVAR);
                return new VarValue(var.getValue(), Type.INTEGER, var.getPos());
            case BOOLVAR:
                var = parseToken(TokenType.BOOLVAR);
                return new VarValue(var.getValue(), Type.BOOLEAN, var.getPos());
        }
        //TODO: Add error
        return null;
    }


    Expression parseExpression() {
        Expression lefthand = parseTerm();
        Operator op;
        Expression righthand;
        if (lookahead() == null) {
            return lefthand;
        }
        switch (lookahead().getType()) {
            case ADD:
            case SUB:
            case OR:
                op = parseOperator();
                righthand = parseExpression();
                return new BinaryExpression(op, lefthand, righthand, lefthand.getPos());
        }
        return lefthand;
    }

    private Expression parseTerm() {
        Expression lefthand = parseCompare();
        Operator op;
        Expression righthand;
        if (lookahead() == null) {
            return lefthand;
        }
        switch (lookahead().getType()) {
            case MULT:
            case DIV:
            case AND:
                op = parseOperator();
                righthand = parseTerm();
                return new BinaryExpression(op, lefthand, righthand, lefthand.getPos());
        }
        return lefthand;
    }

    private Expression parseCompare() {
        Expression lefthand = parseValue();
        Operator op;
        Expression righthand;
        if (lookahead() == null) {
            return lefthand;
        }
        switch (lookahead().getType()) {
            case EQ:
            case NEQ:
            case LT:
            case LE:
            case GE:
            case GT:
                op = parseOperator();
                righthand = parseCompare();
                return new BinaryExpression(op, lefthand, righthand, lefthand.getPos());
        }
        return lefthand;
    }

    private Expression parseValue() {
        switch (lookahead().getType()) {
            case NOT:
                BlockPos pos = lookahead().getPos();
                Operator unaryOp = parseOperator();
                Expression val = parseValue();
                return new UnaryExpression(Operator.NOT, val, pos);
            case PAROPEN:
                parseToken(TokenType.PAROPEN);
                Expression expr = parseExpression();
                parseToken(TokenType.PARCLOSE);
                return expr;
            case INT:
            case BOOL:
                return parseLiteral();
            case VAR:
                return parseVar();
        }
        //TODO: Add error
        return null;
    }
}
