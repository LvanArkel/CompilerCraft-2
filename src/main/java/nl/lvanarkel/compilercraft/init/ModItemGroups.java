package nl.lvanarkel.compilercraft.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import nl.lvanarkel.compilercraft.CompilerCraft;

import java.util.function.Supplier;

public class ModItemGroups {
    public static final ItemGroup COMPILER_GROUP = new ModItemGroup(CompilerCraft.MODID,
            () -> new ItemStack(ModItems.GPS.get()));

    public static class ModItemGroup extends ItemGroup {

        private final Supplier<ItemStack> iconSupplier;

        ModItemGroup(final String name, final Supplier<ItemStack> iconSupplier) {
            super(name);
            this.iconSupplier = iconSupplier;
        }

        @Override
        public ItemStack createIcon() {
            return iconSupplier.get();
        }
    }
}
