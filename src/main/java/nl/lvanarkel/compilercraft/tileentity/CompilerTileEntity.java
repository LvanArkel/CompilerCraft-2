package nl.lvanarkel.compilercraft.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.ItemStackHandler;
import nl.lvanarkel.compilercraft.CompilerCraft;
import nl.lvanarkel.compilercraft.block.Compiler;
import nl.lvanarkel.compilercraft.container.CompilerContainer;
import nl.lvanarkel.compilercraft.init.ModBlocks;
import nl.lvanarkel.compilercraft.init.ModItems;
import nl.lvanarkel.compilercraft.init.ModTileEntityTypes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class CompilerTileEntity extends TileEntity implements INamedContainerProvider {

    public final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem().equals(ModItems.GPS.get());
        }

        @Override
        protected void onContentsChanged(int slot) {
            markDirty();
        }


    };


    private StringBuilder text;

    public CompilerTileEntity() {
        super(ModTileEntityTypes.COMPILER.get());
        text = new StringBuilder();
    }



    @Override
    public CompoundNBT write(CompoundNBT nbt) {
        super.write(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putString("text", text.toString());
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt) {
        super.read(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        text = new StringBuilder();
        String stringText = nbt.getString("text");
        if (stringText.length() > 0) {
            text.append(nbt.getString("text"));
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent(ModBlocks.COMPILER.get().getTranslationKey());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new CompilerContainer(windowId, playerInventory, this);
    }

    public void resetLogs() {
        text = new StringBuilder();
    }

    public void addLog(String log) {
        text.append(log);
        text.append('\n');
    }

    public StringBuilder getLogs() {
        return text;
    }
}
