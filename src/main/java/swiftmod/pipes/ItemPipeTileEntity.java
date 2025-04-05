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
import swiftmod.common.Color;
import swiftmod.common.Filter;
import swiftmod.common.NeighboringItems;
import swiftmod.common.SwiftUtils;
import swiftmod.common.upgrades.IItemFilterUpgradeItem;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.pipes.networks.PipeNetwork;

public abstract class ItemPipeTileEntity extends PipeTileEntity
{
    @SuppressWarnings("unchecked")
	public ItemPipeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, UpgradeInventory upgradeInventory,
			int numSideInventories, SideUpgradeInventoryBuilder sideUpgradeInventorySupplier)
    {
        super(type, EnumSet.of(PipeType.Item), pos, state, upgradeInventory, numSideInventories, sideUpgradeInventorySupplier);
        
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
		PipeTransferData<ItemStack> td = new PipeTransferData<ItemStack>();
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
    	if (network == null || type == PipeType.Item)
    		m_network = network;
    }
    
    public PipeNetwork getNetwork(PipeType type)
    {
    	if (type == PipeType.Item)
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
        NeighboringItems items = new NeighboringItems(level, worldPosition, ItemPipeBlock::isConnectableNeighbor);
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
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicItemFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack stack = inventory.getItem(slot);
            if (!stack.isEmpty())
            {
                if (stack.getItem() instanceof IItemFilterUpgradeItem)
                {
                    Filter<ItemStack> filter = ((IItemFilterUpgradeItem) stack.getItem()).createItemFilter(stack);
                    m_filters[transferIndex] = filter;
                    return;
                }
            }
        }

        m_filters[transferIndex] = null;
    }

    @Override
    protected PipeTransferHandler<ItemStack> createTransferHandler(int transferIndex)
    {
    	ItemPipeTransferHandler newHandler = new ItemPipeTransferHandler();
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
        if (stacks <= 0)
            return new PipeTransferQuantity(false, 1);
        else
            return new PipeTransferQuantity(true, stacks);
    }
    
    @Override
    protected PipeType getTypeForIndex(int transferIndex)
    {
    	return PipeType.Item;
    }

    protected List<PipeNetwork> m_networkList;
    protected PipeNetwork m_network;
    protected Filter<ItemStack>[] m_filters;
}
