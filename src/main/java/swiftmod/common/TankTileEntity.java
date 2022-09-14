package swiftmod.common;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TankTileEntity extends TileEntityBase<FluidTank>
{
    public TankTileEntity(BlockPos pos, BlockState state)
    {
        super(SwiftTileEntities.s_tankTileEntityType, pos, state, new FluidTank(1_000_000));
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
        CompoundTag nbt = new CompoundTag();
        ItemStack stack = new ItemStack(SwiftBlocks.s_tankBlock, 1);
        getCache().write(nbt);
        stack.setTag(new CompoundTag());
        stack.getTag().put(TankItem.NBT_TAG, nbt);
        return stack;
    }

    @Override
    public void read(CompoundTag nbt)
    {
        super.read(nbt);
        getCache().read(nbt);
    }

    @Override
    public void write(CompoundTag nbt)
    {
        super.write(nbt);
        getCache().write(nbt);
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
