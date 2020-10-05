package nl.lvanarkel.compilercraft.compiler.executor;

import com.sun.org.apache.xpath.internal.operations.Bool;
import nl.lvanarkel.compilercraft.compiler.generator.SymbolTable;
import nl.lvanarkel.compilercraft.compiler.parser.ast.AST;
import nl.lvanarkel.compilercraft.compiler.parser.ast.AST.*;
import nl.lvanarkel.compilercraft.compiler.parser.ast.Operator;
import nl.lvanarkel.compilercraft.tileentity.CompilerTileEntity;
import java.util.Map;

import java.util.Iterator;

public class Machine {
    private static final int STACK_SIZE = 10000;
    private static final int MEMORY_SIZE = 20;

    private CompilerTileEntity compilerTE;

    private Program root;
    private Map<AST, Integer> offsets;

    private int[] stack;
    private int[][] memory;
    private int sp;
    private volatile boolean isRunning;

    public Machine(Program root, Map<AST, Integer> offsets, CompilerTileEntity cte) {
        this.compilerTE = cte;
        this.root = root;
        this.offsets = offsets;
        this.stack = new int[STACK_SIZE];
        this.memory = new int[MEMORY_SIZE][];
        this.sp = 0;
        this.isRunning = false;
    }

    public void runMachine() {
        isRunning = true;
        execute(root);
    }

    private void stackPush(int value) {
        stack[sp++] = value;
    }

    private int stackPop() {
        return stack[--sp];
    }

    public void stop() {
        isRunning = false;
    }

    public void execute(Program prog) {
        Iterator<Statement> it = prog.getStatements().iterator();
        while (isRunning && it.hasNext()) {
            Statement stmt = it.next();
            execute(stmt);
        }
    }

    private void execute(Statement stmt) {
        if (stmt instanceof IfStatement) {
            execute((IfStatement) stmt);
        } else if (stmt instanceof AssignStatement) {
            execute((AssignStatement) stmt);
        } else if (stmt instanceof VarDeclare) {
            execute((VarDeclare) stmt);
        } else if (stmt instanceof WhileLoop) {
            execute((WhileLoop) stmt);
        } else if (stmt instanceof OutStatement) {
            execute((OutStatement) stmt);
        }
    }

    private void execute(IfStatement stmt) {
        if (isRunning) {
            execute(stmt.getCondition());
            int result = stackPop();
            if (result == 1) {
                Iterator<Statement> it = stmt.getThenBody().iterator();
                while (isRunning && it.hasNext()) {
                    execute(it.next());
                }
            } else {
                if (stmt.getElseBody() != null) {
                    Iterator<Statement> it = stmt.getElseBody().iterator();
                    while (isRunning && it.hasNext()) {
                        execute(it.next());
                    }
                }
            }
        }
    }

    private void execute(AssignStatement stmt) {
        if (isRunning) {
            int varloc = offsets.get(stmt.getVariable());
            execute(stmt.getRighthand());
            int result = stackPop();
            if (stmt.getOperator() != null) {
                memory[varloc][0] = calculateResult(memory[varloc][0], result, stmt.getOperator());
            } else {
                memory[varloc][0] = result;
            }
        }
    }

    private void execute(VarDeclare stmt) {
        if (isRunning) {
            int varloc = offsets.get(stmt);
            execute(stmt.getDefaultValue());
            memory[varloc] = new int[]{stackPop()};
        }
    }

    private void execute(WhileLoop stmt) {
        while (isRunning) {
            execute(stmt.getCondition());
            if (stackPop() == 1) {
                Iterator<Statement> it = stmt.getBody().iterator();
                while(isRunning && it.hasNext()) {
                    execute(it.next());
                }
            } else {
                break;
            }
        }
    }

    private void execute(OutStatement stmt) {
        if (isRunning) {
            execute(stmt.getExpression());
            compilerTE.addLog(Integer.toString(stackPop()));
        }
    }

    private void execute(Expression expr) {
        if (expr instanceof UnaryExpression) {
            execute((UnaryExpression) expr);
        } else if (expr instanceof BinaryExpression) {
            execute((BinaryExpression) expr);
        } else if (expr instanceof IntValue) {
            execute((IntValue) expr);
        } else if (expr instanceof BoolValue) {
            execute((BoolValue) expr);
        } else if (expr instanceof VarValue) {
            execute((VarValue) expr);
        }
    }

    private void execute(UnaryExpression expr) {
        if (isRunning) {
            execute(expr.getExpression());
            stackPush(calculateResult(stackPop(), -1, expr.getOperator()));
        }
    }

    private void execute(BinaryExpression expr) {
        if (isRunning) {
            execute(expr.getLefthand());
            execute(expr.getRighthand());
            int right = stackPop();
            stackPush(calculateResult(stackPop(), right, expr.getOperator()));
        }
    }

    private void execute(IntValue expr) {
        if (isRunning) {
            stackPush(expr.getVal());
        }
    }

    private void execute(BoolValue expr) {
        if (isRunning) {
            stackPush(expr.getInt());
        }
    }

    private void execute(VarValue expr) {
        if (isRunning) {
            stackPush(memory[offsets.get(expr)][0]);
        }
    }

    private int calculateResult(int left, int right, Operator op) {
        switch (op) {
            case ADD:
                return left + right;
            case SUB:
                return left - right;
            case MULT:
                return left * right;
            case DIV:
                return left / right;
            case AND:
                return left & right;
            case OR:
                return left | right;
            case NOT:
                return (left+1)%2;
            case EQ:
                return left == right ? 1 : 0;
            case NEQ:
                return left == right ? 0 : 1;
            case LT:
                return left < right ? 1 : 0;
            case LE:
                return left <= right ? 1 : 0;
            case GE:
                return left >= right ? 1 : 0;
            case GT:
                return left > right ? 1 : 0;
        }
        return -1;
    }




}
