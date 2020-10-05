package nl.lvanarkel.compilercraft.compiler.generator;

import nl.lvanarkel.compilercraft.compiler.parser.ast.Type;

import java.util.Stack;

public class SymbolTable {
    private final Stack<Scope> scopes;

    /** Constructs a fresh, initially empty symbol table. */
    SymbolTable() {
        this.scopes = new Stack<>();
        openScope();
    }
    /** Adds a next deeper scope level. */
    void openScope() {
        this.scopes.push(new Scope());
    }

    /** Removes the deepest scope level.
     * @throws RuntimeException if the table only contains the outer scope.
     */
    void closeScope() {
        if (this.scopes.size() == 1) {
            throw new IllegalStateException("Can't close outer scope");
        }
        this.scopes.pop();
    }

    /** Tries to declare a given identifier in the deepest scope level.
     * @return <code>true</code> if the identifier was added,
     * <code>false</code> if it was already declared in this scope.
     */
    boolean put(String id, Type record) {
        return this.scopes.peek().put(id, record);
    }

    /** Looks up a given identifier and returns the associated type.
     * @return the record associated with the inner (deepest) declaration
     * of the identifier; {@code null} if there is none.
     */
    public Type type(String id) {
        Type result = null;
        for (int i = this.scopes.size() - 1; result == null && i >= 0; i--) {
            result = this.scopes.get(i).type(id);
        }
        return result;
    }

    int getOffset(String id) {
        for (int i = this.scopes.size() -1; i >= 0; i--) {
            if (scopes.get(i).contains(id)) {
                return scopes.get(i).offset(id);
            }
        }
        return -1;
    }
}
