package nl.lvanarkel.compilercraft.compiler.lexer;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import nl.lvanarkel.compilercraft.compiler.datastructures.Token;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MinecraftLexer {
    public static List<Token> tokenize(BlockPos[] posList, World world) {
        List<Token> result = new ArrayList<>();
        int i = 0;
        while (i < posList.length) {
            String blockName = world.getBlockState(posList[i]).getBlock().getRegistryName().toString();
            LexerRules.Rule rule = LexerRules.rules.get(blockName);
            i += rule.parseBlock(i, posList, result, world);
        }
        return result;
    }

}
