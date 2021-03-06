package swiftmod.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TankTileEntity extends TileEntityBase<FluidTank>
{
    public TankTileEntity()
    {
        super(SwiftTileEntities.s_tankTileEntityType, new FluidTank(1_000_000));
    }

    public static String getRegistryName()
    {
        return "tank";
    }

    public void readFromItem(ItemStack stack)
    {
    	if (stack.hasTag())
    		getCache().read(stack.getTag().getCompound(TankItem.NBT_TAG));
    }

    public ItemStack writeToItem()
    {
        CompoundNBT nbt = new CompoundNBT();
        ItemStack stack = new ItemStack(SwiftBlocks.s_tankBlock, 1);
        getCache().write(nbt);
        stack.setTag(new CompoundNBT());
        stack.getTag().put(TankItem.NBT_TAG, nbt);
        return stack;
    }

    @Override
    public void read(BlockState state, CompoundNBT nbt)
    {
        super.read(state, nbt);
        getCache().read(nbt);
    }

    @Override
    @Nonnull
    public CompoundNBT write(CompoundNBT nbt)
    {
        super.write(nbt);
        getCache().write(nbt);
        return nbt;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, final @Nullable Direction side)
    {
        //ForgeChunkManager.Ticket ticket = null;
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return LazyOptional.of(() -> m_cache).cast();
        }
        return super.getCapability(cap, side);
    }
}
