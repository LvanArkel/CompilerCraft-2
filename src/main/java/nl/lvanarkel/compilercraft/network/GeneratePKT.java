package nl.lvanarkel.compilercraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import nl.lvanarkel.compilercraft.CompilerCraft;
import nl.lvanarkel.compilercraft.compiler.datastructures.Token;
import nl.lvanarkel.compilercraft.compiler.executor.Machine;
import nl.lvanarkel.compilercraft.compiler.generator.Checker;
import nl.lvanarkel.compilercraft.compiler.lexer.MinecraftLexer;
import nl.lvanarkel.compilercraft.compiler.parser.Parser;
import nl.lvanarkel.compilercraft.compiler.parser.ast.AST;
import nl.lvanarkel.compilercraft.tileentity.CompilerTileEntity;
import nl.lvanarkel.compilercraft.util.BlockPosUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class GeneratePKT {

    //The position of the compiler
    private final BlockPos pos;

    public GeneratePKT(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(GeneratePKT pkt, PacketBuffer buf) {
        buf.writeLong(pkt.pos.toLong());
    }

    public static GeneratePKT decode(PacketBuffer buf) {
        return new GeneratePKT(BlockPos.fromLong(buf.readLong()));
    }

    public static class Handler {
        public static void handle(final GeneratePKT message, Supplier<NetworkEvent.Context> ctx) {
            CompilerCraft.LOGGER.debug("Starting executing compiler at position " + BlockPosUtils.formatBlockPos(message.pos));
            //Retrieve blocks from world
            World world = Minecraft.getInstance().world;
            TileEntity te = world.getTileEntity(message.pos);
            if (te == null || !(te instanceof CompilerTileEntity)) {
                throw new IllegalStateException(String.format("Block at %s is not a compiler", BlockPosUtils.formatBlockPos(message.pos)));
            }
            CompilerTileEntity compilerTE = (CompilerTileEntity) te;
            compilerTE.resetLogs();
            compilerTE.addLog("Executing program");
            CompoundNBT nbt = compilerTE.inventory.getStackInSlot(0).getTag();
            BlockPos topleft = BlockPosUtils.fromIntArray(nbt.getIntArray("tl"));
            BlockPos bottomright = BlockPosUtils.fromIntArray(nbt.getIntArray("br"));
            //TODO: Make sure there is a max on the size of the GPS coords (probably 10x10)
            //TODO: Check if the coordinates are on the same Y. Right now I just take the Y of the topleft.
            BlockPos[] blockposList = findblocks(topleft, bottomright);
            for (BlockPos pos : blockposList) {
                CompilerCraft.LOGGER.debug(world.getBlockState(pos).getBlock().toString());
            }
            try {
                List<Token> tokens = MinecraftLexer.tokenize(blockposList, ctx.get().getSender().getServerWorld());
                Parser parser = new Parser(tokens);
                AST.Program program = parser.parseProgram();
                Checker checker = new Checker(program);
                checker.checkProgram();
                List<String> checkerErrors = checker.getErrors();
                for (String error : checkerErrors) {
                    compilerTE.addLog("Type error at " + error);
                }
                if (checkerErrors.size() == 0) {
                    Machine machine = new Machine(program, checker.getOffsets(), compilerTE);
                    machine.runMachine();
                }
            } catch (IllegalStateException e) {
                compilerTE.addLog(e.getMessage());
            }
        }
    }

    private static BlockPos[] findblocks(BlockPos tl, BlockPos br) {
        int size = (Math.abs(tl.getX()-br.getX())-1)*(Math.abs(tl.getZ()-br.getZ())-1);
        BlockPos[] result = new BlockPos[size];
        int i = 0;
        if (tl.getX() <= br.getX()) {
            if (tl.getZ() <= br.getZ()) {
                //xtl <= xbr, ztl <= zbr
                for (int r = tl.getZ()+1; r < br.getZ(); r++) {
                    for (int c = tl.getX()+1; c < br.getX(); c++) {
                        result[i++] = new BlockPos(c, tl.getY(), r);
                    }
                }
            } else {
                //xtl <= xbr, ztl > zbr
                for (int r = tl.getX()+1; r < br.getX(); r++) {
                    for (int c = tl.getZ()-1; c > br.getZ(); c--) {
                        result[i++] = new BlockPos(r, tl.getY(), c);
                    }
                }
            }
        } else {
            if (tl.getZ() <= br.getZ()) {
                //xtl > xbr, ztl <= zbr
                for (int r = tl.getX()-1; r > br.getX(); r--) {
                    for (int c = tl.getZ()+1; c < br.getZ(); c++) {
                        result[i++] = new BlockPos(r, tl.getY(), c);
                    }
                }
            } else {
                //xtl > xbr, ztl > zbr
                for (int r = tl.getZ()-1; r > br.getZ(); r--) {
                    for (int c = tl.getX()-1; c > br.getX(); c--) {
                        result[i++] = new BlockPos(c, tl.getY(), r);
                    }
                }
            }
        }
        return result;
    }
}
