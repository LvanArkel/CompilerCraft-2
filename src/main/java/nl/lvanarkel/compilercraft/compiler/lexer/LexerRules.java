package nl.lvanarkel.compilercraft.compiler.lexer;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.*;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.SignTileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import nl.lvanarkel.compilercraft.compiler.datastructures.Token;
import nl.lvanarkel.compilercraft.compiler.datastructures.TokenType;
import nl.lvanarkel.compilercraft.util.BlockPosUtils;

import java.util.*;

public class LexerRules {
    public static Map<String, Rule> rules = new HashMap<>();

    static {
        Rule chestRule = new Rule(TokenType.INT) {
            @Override
            public int parseBlock(int index, BlockPos[] posList, List<Token> tokens, World world) {
                tokens.add(new Token(type, Integer.toString(readInt(posList[index], world)), posList[index]));
                return 1;
            }
        };
        rules.put("minecraft:chest", chestRule);
        rules.put("minecraft:trapped_chest", chestRule);
        rules.put("minecraft:oak_fence_gate", new Rule(TokenType.BOOL){
            @Override
            public int parseBlock(int index, BlockPos[] posList, List<Token> tokens, World world) {
                tokens.add(new Token(type, Boolean.toString(readBool(posList[index], world)), posList[index]));
                return 1;
            }
        });
        rules.put("minecraft:oak_sign", new Rule(TokenType.VAR){
            @Override
            public int parseBlock(int index, BlockPos[] posList, List<Token> tokens, World world) {
                BlockPos pos = posList[index];
                BlockState state = world.getBlockState(pos);
                if (!state.getBlock().equals(Blocks.OAK_SIGN)) {
                    throw new IllegalStateException("Block at pos " + BlockPosUtils.formatBlockPos(pos) + " is not an oak sign");
                }
                SignTileEntity signTE = (SignTileEntity)world.getTileEntity(pos);
                String fulltext = Arrays.stream(signTE.signText)
                        .map(ITextComponent::getFormattedText)
                        .reduce("", String::concat);
                BlockPos below = pos.down();
                switch (world.getBlockState(below).getBlock().getRegistryName().toString()) {
                    case "minecraft:oak_fence_gate":
                        tokens.add(new Token(TokenType.BOOLVAR, fulltext, pos));
                        tokens.add(new Token(TokenType.BOOL, Boolean.toString(readBool(below, world)), below));
                        break;
                    case "minecraft:chest":
                    case "minecraft:trapped_chest":
                        tokens.add(new Token(TokenType.INTVAR, fulltext, pos));
                        tokens.add(new Token(TokenType.INT, Integer.toString(readInt(below, world)), below));
                        break;
                    default:
                        tokens.add(new Token(TokenType.VAR, fulltext, pos));

                }
                return 1;
            }
        });
        rules.put("minecraft:crafting_table", new Rule(TokenType.ASSIGN));
        rules.put("minecraft:iron_ore", new Rule(TokenType.ADD));
        rules.put("minecraft:coal_ore", new Rule(TokenType.SUB));
        rules.put("minecraft:gold_ore", new Rule(TokenType.MULT));
        rules.put("minecraft:lapis_ore", new Rule(TokenType.DIV));
        rules.put("minecraft:iron_block", new Rule(TokenType.ADDASSIGN));
        rules.put("minecraft:coal_block", new Rule(TokenType.SUBASSIGN));
        rules.put("minecraft:gold_block", new Rule(TokenType.MULTASSIGN));
        rules.put("minecraft:lapis_block", new Rule(TokenType.DIVASSIGN));
        rules.put("minecraft:repeater", new Rule(null){
            @Override
            public int parseBlock(int index, BlockPos[] posList, List<Token> tokens, World world) {
                BlockPos pos = posList[index];
                BlockState state = world.getBlockState(pos);
                if (!state.getBlock().equals(Blocks.REPEATER)) {
                    throw new IllegalStateException("Block at pos " + BlockPosUtils.formatBlockPos(pos) + " is not a repeater");
                }
                switch (state.get(IntegerProperty.create("delay", 1, 4))) {
                    case 1:
                        tokens.add(new Token(TokenType.LT, "", pos));
                        break;
                    case 2:
                        tokens.add(new Token(TokenType.LE, "", pos));
                        break;
                    case 3:
                        tokens.add(new Token(TokenType.GE, "", pos));
                        break;
                    case 4:
                        tokens.add(new Token(TokenType.GT, "", pos));
                        break;
                    default:
                        throw new IllegalArgumentException("Unknown state in repeater");
                }
                return 1;
            }
        });
        rules.put("minecraft:comparator", new Rule(null){
            @Override
            public int parseBlock(int index, BlockPos[] posList, List<Token> tokens, World world) {
                BlockPos pos = posList[index];
                BlockState state = world.getBlockState(pos);
                if (!state.getBlock().equals(Blocks.COMPARATOR)) {
                    throw new IllegalStateException("Block at pos " + BlockPosUtils.formatBlockPos(pos) + " is not a comparator");
                }
                if (state.get(EnumProperty.create("mode", ComparatorMode.class))
                        .equals(ComparatorMode.COMPARE)) {
                    tokens.add(new Token(TokenType.EQ, "", pos));
                } else {
                    tokens.add(new Token(TokenType.NEQ, "", pos));
                }
                return 1;
            }
        });
        rules.put("minecraft:poppy", new Rule(TokenType.AND));
        rules.put("minecraft:dandelion", new Rule(TokenType.OR));
        rules.put("minecraft:rose_bush", new Rule(TokenType.ANDASSIGN));
        rules.put("minecraft:sunflower", new Rule(TokenType.ORASSIGN));
        rules.put("minecraft:observer", new Rule(TokenType.IF));
        rules.put("minecraft:dispenser", new Rule(TokenType.WHILE));
        rules.put("minecraft:pumpkin", new Rule(TokenType.THEN));
        rules.put("minecraft:jack_o_lantern", new Rule(TokenType.ELSE));
        rules.put("minecraft:note_block", new Rule(TokenType.OUT));
        rules.put("minecraft:glass_pane", new Rule(TokenType.PAROPEN) {
            @Override
            public int parseBlock(int index, BlockPos[] posList, List<Token> tokens, World world) {
                return readBracket(type, index, posList, tokens, world);
            }
        });
        rules.put("minecraft:iron_bars", new Rule(TokenType.BROPEN) {
            @Override
            public int parseBlock(int index, BlockPos[] posList, List<Token> tokens, World world) {
                return readBracket(type, index, posList, tokens, world);
            }
        });
        rules.put("minecraft:air", new Rule(null) {
            @Override
            public int parseBlock(int index, BlockPos[] posList, List<Token> tokens, World world) {
                if (index >= posList.length-1) {
                    return 1;
                }
                BlockPos next = posList[index+1];
                switch (world.getBlockState(next).getBlock().getRegistryName().toString()) {
                    case "minecraft:glass_pane":
                        tokens.add(new Token(TokenType.PARCLOSE, "", next));
                        return 2;
                    case "minecraft:iron_bars":
                        tokens.add(new Token(TokenType.BRCLOSE, "", next));
                        return 2;
                    default:
                        return 1;
                }
            }
        });
    }

    static class Rule {
        TokenType type;

        Rule(TokenType type) {
            this.type = type;
        }

        //Requires posit.hasNext()
        public int parseBlock(int index, BlockPos[] posList, List<Token> tokens, World world) {
            tokens.add(new Token(type, "", posList[index]));
            return 1;
        }
    }

    private static int readBracket(TokenType type, int index, BlockPos[] posList, List<Token> tokens, World world) {
        BlockPos pos = posList[index];
        BlockPos next = posList[index+1];
        if (!world.getBlockState(next).getBlock().getRegistryName().toString().equals("minecraft:air")) {
            throw new IllegalStateException(String.format("Lexer error at pos %s: No air before or after the bracket", next));
        }
        tokens.add(new Token(type, "", pos));
        return 2;
    }

    private static boolean readBool(BlockPos pos, World world) {
        BlockState state = world.getBlockState(pos);
        if (!state.getBlock().equals(Blocks.OAK_FENCE_GATE)) {
            throw new IllegalStateException("Block at pos " + BlockPosUtils.formatBlockPos(pos) + " is not an oak fence gate");
        }
        return state.get(BooleanProperty.create("open"));
    }

    private static int readInt(BlockPos pos, World world) {
        BlockState state = world.getBlockState(pos);
        if (!(state.getBlock().equals(Blocks.CHEST) || state.getBlock().equals(Blocks.TRAPPED_CHEST))) {
            throw new IllegalStateException("Block at pos " + BlockPosUtils.formatBlockPos(pos) + " is not a chest");
        }
        ChestTileEntity chestTE = (ChestTileEntity)world.getTileEntity(pos);
        IItemHandler handler = chestTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
        if (handler == null) {
            throw new IllegalStateException("No item handler for chest at position " + BlockPosUtils.formatBlockPos(pos));
        }
        int result = 0;
        for(int i = 0; i < handler.getSlots(); i++) {
            result += handler.getStackInSlot(i).getCount()*((int)Math.pow(10,i));
        }
        return result;
    }
}
