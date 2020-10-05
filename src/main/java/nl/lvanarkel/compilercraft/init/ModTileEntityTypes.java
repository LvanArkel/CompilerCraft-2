package nl.lvanarkel.compilercraft.init;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import nl.lvanarkel.compilercraft.CompilerCraft;
import nl.lvanarkel.compilercraft.tileentity.CompilerTileEntity;

public class ModTileEntityTypes {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES =
            new DeferredRegister<>(ForgeRegistries.TILE_ENTITIES, CompilerCraft.MODID);

    public static final RegistryObject<TileEntityType<CompilerTileEntity>> COMPILER =
            TILE_ENTITY_TYPES.register("compiler", () ->
                    TileEntityType.Builder.create(
                            CompilerTileEntity::new,
                            ModBlocks.COMPILER.get())
                .build(null));
}
