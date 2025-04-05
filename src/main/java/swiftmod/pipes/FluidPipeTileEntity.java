package swiftmod.pipes;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.fluids.FluidStack;
import swiftmod.common.Color;
import swiftmod.common.Filter;
import swiftmod.common.NeighboringItems;
import swiftmod.common.SwiftUtils;
import swiftmod.common.upgrades.IFluidFilterUpgradeItem;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.pipes.networks.PipeNetwork;

public abstract class FluidPipeTileEntity extends PipeTileEntity
{
    @SuppressWarnings("unchecked")
    public FluidPipeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, UpgradeInventory upgradeInventory,
    		int numSideInventories, SideUpgradeInventoryBuilder sideUpgradeInventorySupplier)
    {
        super(type, EnumSet.of(PipeType.Fluid), pos, state, upgradeInventory, numSideInventories, sideUpgradeInventorySupplier);
        
        m_network = null;

    	m_filters = new Filter[numSideInventories];
        for (int i = 0; i < numSideInventories; ++i)
            m_filters[i] = null;
    }

	@Override
	public List<PipeNetwork> getNetworks()
	{
		LinkedList<PipeNetwork> networks = new LinkedList<PipeNetwork>();
		if (m_network != null)
			networks.add(m_network);
		return networks;
	}

	@Override
    public PipeTransferData<?> getTransferData(PipeType type, Direction neighborDir, Direction handlerDir)
    {
    	int dirIndex = SwiftUtils.dirToIndex(neighborDir);
		PipeTransferData<FluidStack> td = new PipeTransferData<FluidStack>();
    	td.maxTransferQuantity = m_transferQuantities[dirIndex];
    	td.tickRate = m_tickRates[dirIndex];
    	td.redstoneControl = m_cache.redstoneControls[dirIndex];
    	td.color = m_cache.colors[dirIndex];
		td.filter = m_filters[dirIndex];
    	return td;
    }

    @Override
    protected void assignNetwork(PipeNetwork network, PipeType type)
    {
    	if (network == null || type == PipeType.Fluid)
    		m_network = network;
    }
    
    public PipeNetwork getNetwork(PipeType type)
    {
    	if (type == PipeType.Fluid)
    		return m_network;
    	else
    		return null;
    }
	
    @Override
	public Color getRenderColorForSide(Direction dir)
	{
    	if (m_cache == null)
    		return Color.Transparent;
		return m_cache.getColor(SwiftUtils.dirToIndex(dir));
	}

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag nbt)
    {
        super.read(provider, nbt);

        for (int i = 0; i < m_filters.length; ++i)
        {
            m_filters[i] = null;
            refreshFilter(i);
        }
    }

    public void serializeBufferForContainer(RegistryFriendlyByteBuf buffer, Player player, Direction startingDir)
    {
        NeighboringItems items = new NeighboringItems(level, worldPosition, FluidPipeBlock::isConnectableNeighbor);
        items.setStartingDirection(startingDir);
        m_cache.serialize(buffer, items);
        BlockPos.STREAM_CODEC.encode(buffer, getBlockPos());
    }

    @Override
    protected void onSideUpgradesChanged(int transferIndex)
    {
    	super.onSideUpgradesChanged(transferIndex);
        refreshFilter(transferIndex);
    	setChanged();
    }

    protected void refreshFilter(int transferIndex)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[transferIndex];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicFluidFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof IFluidFilterUpgradeItem)
                {
                    Filter<FluidStack> filter = ((IFluidFilterUpgradeItem) stack.getItem()).createFluidFilter(stack);
                    m_filters[transferIndex] = filter;
                    return;
                }
            }
        }

        m_filters[transferIndex] = null;
    }
    
    @Override
    protected PipeTransferHandler<?> createTransferHandler(int transferIndex)
    {
    	FluidPipeTransferHandler newHandler = new FluidPipeTransferHandler();
    	newHandler.pipe = this;
    	newHandler.neighborDir = SwiftUtils.indexToDir(transferIndex);
    	newHandler.handlerDir = newHandler.neighborDir.getOpposite();
    	return newHandler;
    }

    @Override
    protected PipeTransferQuantity getTransferQuantity(int transferIndex, int stacks)
    {
        return getTransferQuantity(stacks);
    }

    public static PipeTransferQuantity getTransferQuantity(int stacks)
    {
        // Multiply by 1000 to convert from millibuckets to buckets.
        long stacksLong = 1000L * ((long)stacks + 1L);
        if (stacksLong > (long)Integer.MAX_VALUE)
            stacksLong = (long)Integer.MAX_VALUE;
        return new PipeTransferQuantity(false, (int)stacksLong);
    }
    
    @Override
    protected PipeType getTypeForIndex(int transferIndex)
    {
    	return PipeType.Fluid;
    }

    protected PipeNetwork m_network;
    protected Filter<FluidStack>[] m_filters;
}
