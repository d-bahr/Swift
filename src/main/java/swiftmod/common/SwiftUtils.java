package swiftmod.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage;
import net.neoforged.neoforge.capabilities.Capabilities.FluidHandler;
import net.neoforged.neoforge.capabilities.Capabilities.ItemHandler;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public final class SwiftUtils
{
    public static int dirToIndex(Direction direction)
    {
        // This is a horrible name and there's actually a 99% chance
        // this stupid API will change in the future, again, because
        // it's complete garbage right now.
        return direction.get3DDataValue();
    }

    public static Direction indexToDir(int index)
    {
        // This is a horrible name and there's actually a 99% chance
        // this stupid API will change in the future, again, because
        // it's complete garbage right now.
        return Direction.from3DDataValue(index);
    }

    public static Direction getDirectionBetweenBlocks(BlockPos pos, BlockPos neighbor)
    {
        if (pos.above().equals(neighbor))
            return Direction.UP;
        else if (pos.below().equals(neighbor))
            return Direction.DOWN;
        else if (pos.north().equals(neighbor))
            return Direction.NORTH;
        else if (pos.south().equals(neighbor))
            return Direction.SOUTH;
        else if (pos.west().equals(neighbor))
            return Direction.WEST;
        else if (pos.east().equals(neighbor))
            return Direction.EAST;
        else
            return null;
    }
    
    public static boolean itemTagsMatch(ItemStack a, ItemStack b)
    {
    	return ItemStack.isSameItemSameComponents(a, b);
    }

    public static boolean isItemHandler(BlockEntity blockEntity, Direction side)
    {
        if (blockEntity == null)
            return false;
        return getItemHandler(blockEntity, side) != null;
    }

    public static boolean isItemHandler(Level level, BlockPos pos, Direction side)
    {
        return getItemHandler(level, pos, side) != null;
    }

    public static IItemHandler getItemHandler(Level level, BlockPos pos, Direction side)
    {
        return level.getCapability(ItemHandler.BLOCK, pos, null, null, side);
    }

    public static IItemHandler getItemHandler(BlockEntity blockEntity, Direction side)
    {
        if (blockEntity == null)
            return null;
        return blockEntity.getLevel().getCapability(ItemHandler.BLOCK, blockEntity.getBlockPos(), null, blockEntity, side);
    }

    public static boolean isFluidHandler(BlockEntity blockEntity, Direction side)
    {
        if (blockEntity == null)
            return false;
        return getFluidHandler(blockEntity, side) != null;
    }

    public static boolean isFluidHandler(Level level, BlockPos pos, Direction side)
    {
        boolean asdf = getFluidHandler(level, pos, side) != null;
        return asdf;
    }

    public static IFluidHandler getFluidHandler(Level level, BlockPos pos, Direction side)
    {
        return level.getCapability(FluidHandler.BLOCK, pos, null, null, side);
    }

    public static IFluidHandler getFluidHandler(BlockEntity blockEntity, Direction side)
    {
        if (blockEntity == null)
            return null;
        return blockEntity.getLevel().getCapability(FluidHandler.BLOCK, blockEntity.getBlockPos(), null, blockEntity, side);
    }

    public static IFluidHandler getFluidHandler(ItemStack itemStack)
    {
        if (itemStack == null || itemStack.isEmpty())
            return null;
        return itemStack.getCapability(Capabilities.FluidHandler.ITEM);
    }

    public static boolean isEnergyHandler(BlockEntity blockEntity, Direction side)
    {
        if (blockEntity == null)
            return false;
        return getEnergyHandler(blockEntity, side) != null;
    }

    public static boolean isEnergyHandler(Level level, BlockPos pos, Direction side)
    {
        return getEnergyHandler(level, pos, side) != null;
    }

    public static IEnergyStorage getEnergyHandler(Level level, BlockPos pos, Direction side)
    {
        return level.getCapability(EnergyStorage.BLOCK, pos, null, null, side);
    }

    public static IEnergyStorage getEnergyHandler(BlockEntity blockEntity, Direction side)
    {
        if (blockEntity == null)
            return null;
        return blockEntity.getLevel().getCapability(EnergyStorage.BLOCK, blockEntity.getBlockPos(), null, blockEntity, side);
    }
    
    public static void putBooleanArray(CompoundTag nbt, String key, boolean[] bools)
    {
        byte[] bytes = new byte[bools.length];
        for (int i = 0; i < bools.length; ++i)
            bytes[i] = (byte) (bools[i] ? 1 : 0);
        nbt.putByteArray(key, bytes);
    }

    public static boolean[] getBooleanArray(CompoundTag nbt, String key)
    {
        byte[] bytes = nbt.getByteArray(key);
        boolean[] bools = new boolean[bytes.length];
        for (int i = 0; i < bytes.length; ++i)
            bools[i] = bytes[i] == 0 ? false : true;
        return bools;
    }

    public static void writeBooleanArray(FriendlyByteBuf buffer, boolean[] bools)
    {
        byte[] bytes = new byte[bools.length];
        for (int i = 0; i < bools.length; ++i)
            bytes[i] = (byte) (bools[i] ? 1 : 0);
        buffer.writeByteArray(bytes);
    }

    public static boolean[] readBooleanArray(FriendlyByteBuf buffer)
    {
        byte[] bytes = buffer.readByteArray();
        boolean[] bools = new boolean[bytes.length];
        for (int i = 0; i < bytes.length; ++i)
            bools[i] = bytes[i] == 0 ? false : true;
        return bools;
    }

    public static String tagName(String s)
    {
        return "swift." + s;
    }
}
