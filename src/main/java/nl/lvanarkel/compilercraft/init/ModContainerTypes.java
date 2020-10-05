package nl.lvanarkel.compilercraft.init;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import nl.lvanarkel.compilercraft.CompilerCraft;
import nl.lvanarkel.compilercraft.container.CompilerContainer;

public class ModContainerTypes {
    public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES =
            new DeferredRegister<>(ForgeRegistries.CONTAINERS, CompilerCraft.MODID);

    public static final RegistryObject<ContainerType<CompilerContainer>> COMPILER = CONTAINER_TYPES.register(
            "compiler", () ->
            IForgeContainerType.create(CompilerContainer::new)
    );
}
