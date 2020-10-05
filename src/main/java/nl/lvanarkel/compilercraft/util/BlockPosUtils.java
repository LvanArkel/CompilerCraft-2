package nl.lvanarkel.compilercraft.util;

import net.minecraft.util.math.BlockPos;

public class BlockPosUtils {

    public static String formatBlockPos(BlockPos pos) {
        return String.format("(%d, %d, %d)",pos.getX(), pos.getY(), pos.getZ());
    }

    public static BlockPos fromIntArray(int[] arr) {
        if (arr.length != 3) {
            throw new IllegalArgumentException("Requires an integer list with 3 values");
        }
        return new BlockPos(arr[0], arr[1], arr[2]);
    }

    public static int[] toIntArray(BlockPos pos) {
        return new int[]{pos.getX(), pos.getY(), pos.getZ()};
    }

}
