package nl.lvanarkel.compilercraft;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nl.lvanarkel.compilercraft.init.ModBlocks;
import nl.lvanarkel.compilercraft.init.ModContainerTypes;
import nl.lvanarkel.compilercraft.init.ModItems;
import nl.lvanarkel.compilercraft.init.ModTileEntityTypes;
import nl.lvanarkel.compilercraft.network.PacketHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(CompilerCraft.MODID)
public class CompilerCraft {

    public static final String MODID = "compilercraft";

    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public CompilerCraft () {
        LOGGER.debug("Hello from Compilercraft!");

        final ModLoadingContext modLoadingContext = ModLoadingContext.get();
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        //Register Deferred Registers
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
        ModContainerTypes.CONTAINER_TYPES.register(modEventBus);

        DeferredWorkQueue.runLater(() -> {
            PacketHandler.register();
        });
    }
}
