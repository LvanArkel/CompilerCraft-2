package nl.lvanarkel.compilercraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import nl.lvanarkel.compilercraft.CompilerCraft;
import nl.lvanarkel.compilercraft.init.ModBlocks;
import nl.lvanarkel.compilercraft.init.ModItemGroups;
import nl.lvanarkel.compilercraft.util.BlockPosUtils;

public class Gps extends Item {
    public Gps() {
        super(new Item.Properties().group(ModItemGroups.COMPILER_GROUP).maxStackSize(1));
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getWorld();
        if (!world.isRemote) {
            BlockPos pos = context.getPos();
            if (world.getBlockState(pos).getBlock() == ModBlocks.COMPILER.get()) {
                CompilerCraft.LOGGER.debug("Compiler pressed");
                return ActionResultType.PASS;
            }
            PlayerEntity player = context.getPlayer();
            ItemStack is = context.getItem();
            CompoundNBT nbt = is.getTag();
            if (nbt == null) {
                nbt = new CompoundNBT();
            }
            //If the player is sneaking, the bottom right coordinate is saved
            String location = player.isCrouching() ? "br" : "tl";
            nbt.putIntArray(location, new int[]{pos.getX(), pos.getY(), pos.getZ()});
            is.setTag(nbt);
            CompilerCraft.LOGGER.debug(is.getTag());
            String msg = String.format("%s: %s", location, BlockPosUtils.formatBlockPos(context.getPos()));
            player.sendStatusMessage(new StringTextComponent(
                    msg),
                    true);
        }
        return ActionResultType.CONSUME;
    }

    public ActionResult<ItemStack> onItemUse(World worldIn, PlayerEntity playerIn, Hand handIn) {
        //TODO: not implemented
        if (!worldIn.isRemote) {
            CompilerCraft.LOGGER.debug("Right clicked");
            playerIn.sendMessage(new StringTextComponent("Hello there"));
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
