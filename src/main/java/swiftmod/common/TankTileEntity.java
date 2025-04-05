package swiftmod.common;

import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;

public class TankTileEntity extends TileEntityBase
{
    public TankTileEntity(BlockPos pos, BlockState state)
    {
        super(SwiftTileEntities.s_tankTileEntityType.get(), pos, state);
        m_cache = new FluidTank(1_000_000);
    }

    public static String getRegistryName()
    {
        return "tank";
    }
    
    public FluidTank getCache()
    {
    	return m_cache;
    }

    public void readFromItem(ItemStack stack)
    {
    	FluidStack fluidStack = stack.get(SwiftDataComponents.FLUID_STACK_DATA_COMPONENT).fluidStack();
        if (fluidStack != null && !fluidStack.isEmpty())
        	m_cache.setFluid(fluidStack);
    }

    public ItemStack writeToItem()
    {
        ItemStack stack = new ItemStack(SwiftBlocks.s_tankBlock.get(), 1);
        FluidStack fluidStack = m_cache.getFluid();
        stack.set(SwiftDataComponents.FLUID_STACK_DATA_COMPONENT, new ImmutableFluidStack(fluidStack));
        return stack;
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag nbt)
    {
        super.read(provider, nbt);
        m_cache.read(provider, nbt);
    }

    @Override
    public void write(HolderLookup.Provider provider, CompoundTag nbt)
    {
        super.write(provider, nbt);
        m_cache.write(provider, nbt);
    }

    public static void registerCapabilities(RegisterCapabilitiesEvent event)
    {
    	event.registerBlockEntity(Capabilities.FluidHandler.BLOCK, SwiftTileEntities.s_tankTileEntityType.get(), (entity, side) ->
    	{
    		return entity.getCache();
    	});
    }
    
    private FluidTank m_cache;
}
