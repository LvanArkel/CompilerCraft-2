package nl.lvanarkel.compilercraft.compiler.generator;

import nl.lvanarkel.compilercraft.compiler.parser.ast.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Stack;

public class Scope {

    private int size;
    private final Map<String, Type> types;
    private final Map<String, Integer> offsets;

    Scope() {
        this.types = new HashMap<>();
        this.offsets = new HashMap<>();
    }

    boolean contains(String id) {
        return this.types.containsKey(id);
    }

    boolean put(String id, Type type) {
        boolean result = !this.types.containsKey(id);
        if (result) {
            this.types.put(id, type);
            this.offsets.put(id, this.size);
            this.size += 1;
        }
        return result;
    }

    public Type type(String id) {
        return this.types.get(id);
    }

    Integer offset(String id) {
        return this.offsets.get(id);
    }
}
