package nl.lvanarkel.compilercraft.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraftforge.items.SlotItemHandler;
import nl.lvanarkel.compilercraft.init.ModBlocks;
import nl.lvanarkel.compilercraft.init.ModContainerTypes;
import nl.lvanarkel.compilercraft.tileentity.CompilerTileEntity;

import javax.annotation.Nullable;
import java.util.Objects;

public class CompilerContainer extends Container {

    public final CompilerTileEntity tileEntity;
    private final IWorldPosCallable canInteractWithCallable;
    private static final int GPS = 0;

    public CompilerContainer(int windowId, PlayerInventory playerInventory, PacketBuffer data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data));
    }

    public CompilerContainer(int windowId, PlayerInventory playerInventory, CompilerTileEntity tileEntity) {
        super(ModContainerTypes.COMPILER.get(), windowId);
        this.tileEntity = tileEntity;
        this.canInteractWithCallable = IWorldPosCallable.of(tileEntity.getWorld(), tileEntity.getPos());

        this.addSlot(new SlotItemHandler(tileEntity.inventory, GPS, 134, 36));

        int xStart = 8;
        int yStart = 140;
        int slotSize = 18;

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, 9+(row*9)+column,
                        xStart+(column*slotSize),
                        yStart+(row*slotSize)));
            }
        }

        int hotBary = yStart + slotSize*3+4;
        for(int col = 0; col < 9; col++) {
            this.addSlot(new Slot(playerInventory, col, xStart + (col*slotSize), hotBary));
        }

    }

    public static CompilerTileEntity getTileEntity(PlayerInventory inventory, PacketBuffer data) {
        Objects.requireNonNull(inventory, "playerInventory cannot be null!");
        Objects.requireNonNull(data, "data cannot be null");
        TileEntity tileAtPos = inventory.player.world.getTileEntity(data.readBlockPos());
        if (tileAtPos instanceof CompilerTileEntity) {
            return (CompilerTileEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(canInteractWithCallable, playerIn, ModBlocks.COMPILER.get());
    }
}
