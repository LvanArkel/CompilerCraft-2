package nl.lvanarkel.compilercraft.compiler.datastructures;

import net.minecraft.util.math.BlockPos;

public class Token {
    private TokenType type;
    private String value;
    private BlockPos pos;

    public Token(TokenType type, String value, BlockPos pos) {
        this.type = type;
        this.value = value;
        this.pos = pos;
    }

    public TokenType getType() {
        return type;
    }

    public void setType(TokenType type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public BlockPos getPos() {
        return pos;
    }

    public void setPos(BlockPos pos) {
        this.pos = pos;
    }
}
