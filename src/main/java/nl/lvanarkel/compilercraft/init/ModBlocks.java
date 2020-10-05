package nl.lvanarkel.compilercraft.init;


import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import nl.lvanarkel.compilercraft.CompilerCraft;
import nl.lvanarkel.compilercraft.block.Compiler;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, CompilerCraft.MODID);

    public static final RegistryObject<Block> COMPILER = BLOCKS.register("compiler", Compiler::new);
}
