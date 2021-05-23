package swiftmod.common;

import java.util.Optional;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

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
        if (pos.above() == neighbor)
            return Direction.UP;
        else if (pos.below() == neighbor)
            return Direction.DOWN;
        else if (pos.north() == neighbor)
            return Direction.NORTH;
        else if (pos.south() == neighbor)
            return Direction.SOUTH;
        else if (pos.west() == neighbor)
            return Direction.WEST;
        else if (pos.east() == neighbor)
            return Direction.EAST;
        else
            return null;
    }
    
    public static boolean itemTagsMatch(ItemStack a, ItemStack b)
    {
        if (a.hasTag() == b.hasTag())
        {
            if (a.hasTag())
            {
                return a.getTag().equals(b.getTag());
            }
            else
            {
                return true;
            }
        }
        else
        {
            return false;
        }
    }

    public static boolean isItemHandler(TileEntity tileEntity, Direction side)
    {
        if (tileEntity == null)
            return false;
        LazyOptional<IItemHandler> capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        if (capability == null)
            return false;
        Optional<IItemHandler> resolved = capability.resolve();
        if (resolved == null)
            return false;
        return resolved.isPresent();
    }

    public static IItemHandler getItemHandler(TileEntity tileEntity, Direction side)
    {
        if (tileEntity == null)
            return null;
        LazyOptional<IItemHandler> capability = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        if (capability == null)
            return null;
        Optional<IItemHandler> resolved = capability.resolve();
        if (resolved == null)
            return null;
        return resolved.isPresent() ? resolved.get() : null;
    }

    public static boolean isFluidHandler(TileEntity tileEntity, Direction side)
    {
        if (tileEntity == null)
            return false;
        LazyOptional<IFluidHandler> capability = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
        if (capability == null)
            return false;
        Optional<IFluidHandler> resolved = capability.resolve();
        if (resolved == null)
            return false;
        return resolved.isPresent();
    }

    public static IFluidHandler getFluidHandler(TileEntity tileEntity, Direction side)
    {
        if (tileEntity == null)
            return null;
        LazyOptional<IFluidHandler> capability = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
        if (capability == null)
            return null;
        Optional<IFluidHandler> resolved = capability.resolve();
        if (resolved == null)
            return null;
        return resolved.isPresent() ? resolved.get() : null;
    }

    public static IFluidHandler getFluidHandler(ItemStack itemStack)
    {
        if (itemStack == null || itemStack.isEmpty())
            return null;
        LazyOptional<IFluidHandler> capability = itemStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        if (capability == null)
            return null;
        Optional<IFluidHandler> resolved = capability.resolve();
        if (resolved == null)
            return null;
        return resolved.isPresent() ? resolved.get() : null;
    }

    public static boolean isEnergyHandler(TileEntity tileEntity, Direction side)
    {
        if (tileEntity == null)
            return false;
        LazyOptional<IEnergyStorage> capability = tileEntity.getCapability(CapabilityEnergy.ENERGY, side);
        if (capability == null)
            return false;
        Optional<IEnergyStorage> resolved = capability.resolve();
        if (resolved == null)
            return false;
        return resolved.isPresent();
    }
    
    public static void putBooleanArray(CompoundNBT nbt, String key, boolean[] bools)
    {
        byte[] bytes = new byte[bools.length];
        for (int i = 0; i < bools.length; ++i)
            bytes[i] = (byte) (bools[i] ? 1 : 0);
        nbt.putByteArray(key, bytes);
    }

    public static boolean[] getBooleanArray(CompoundNBT nbt, String key)
    {
        byte[] bytes = nbt.getByteArray(key);
        boolean[] bools = new boolean[bytes.length];
        for (int i = 0; i < bytes.length; ++i)
            bools[i] = bytes[i] == 0 ? false : true;
        return bools;
    }

    public static void writeBooleanArray(PacketBuffer buffer, boolean[] bools)
    {
        byte[] bytes = new byte[bools.length];
        for (int i = 0; i < bools.length; ++i)
            bytes[i] = (byte) (bools[i] ? 1 : 0);
        buffer.writeByteArray(bytes);
    }

    public static boolean[] readBooleanArray(PacketBuffer buffer)
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
