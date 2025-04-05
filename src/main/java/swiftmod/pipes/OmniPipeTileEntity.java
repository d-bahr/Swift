package swiftmod.pipes;

import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import swiftmod.common.Color;
import swiftmod.common.Filter;
import swiftmod.common.NeighboringItems;
import swiftmod.common.RedstoneControl;
import swiftmod.common.SwiftUtils;
import swiftmod.common.TransferDirection;
import swiftmod.common.upgrades.IFluidFilterUpgradeItem;
import swiftmod.common.upgrades.IItemFilterUpgradeItem;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.pipes.networks.PipeNetwork;

public abstract class OmniPipeTileEntity extends PipeTileEntity
{
    @SuppressWarnings("unchecked")
	public OmniPipeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, UpgradeInventory upgradeInventory,
			int numSideInventories, SideUpgradeInventoryBuilder sideUpgradeInventorySupplier)
    {
        super(type, EnumSet.of(PipeType.Item, PipeType.Fluid, PipeType.Energy), pos,
        		state, upgradeInventory, numSideInventories, sideUpgradeInventorySupplier);

        m_itemNetwork = null;
        m_fluidNetwork = null;
        m_energyNetwork = null;

        int len = Direction.values().length;
    	m_itemFilters = new Filter[len];
    	m_fluidFilters = new Filter[len];
        for (int i = 0; i < len; ++i)
        {
        	m_itemFilters[i] = null;
        	m_fluidFilters[i] = null;
        }
    }

	@Override
	public List<PipeNetwork> getNetworks()
	{
		LinkedList<PipeNetwork> networks = new LinkedList<PipeNetwork>();
		if (m_itemNetwork != null)
			networks.add(m_itemNetwork);
		if (m_fluidNetwork != null)
			networks.add(m_fluidNetwork);
		if (m_energyNetwork != null)
			networks.add(m_energyNetwork);
		return networks;
	}

	@Override
    public PipeTransferData<?> getTransferData(PipeType type, Direction neighborDir, Direction handlerDir)
    {
    	int filterIndex = SwiftUtils.dirToIndex(neighborDir);
    	int dirIndex = filterIndex;
    	PipeTransferData<?> td;
    	switch (type)
    	{
    	default:
    	case Item:
    	{
    		PipeTransferData<ItemStack> ptd = new PipeTransferData<ItemStack>();
    		ptd.filter = m_itemFilters[filterIndex];
    		td = ptd;
    		dirIndex += MIN_ITEM_INDEX;
    		break;
    	}
    	case Fluid:
    	{
    		PipeTransferData<FluidStack> ptd = new PipeTransferData<FluidStack>();
    		ptd.filter = m_fluidFilters[filterIndex];
    		td = ptd;
    		dirIndex += MIN_FLUID_INDEX;
    		break;
    	}
    	case Energy:
    	{
    		td = new FilterlessPipeTransferData();;
    		dirIndex += MIN_ENERGY_INDEX;
    		break;
    	}
    	}
    	td.maxTransferQuantity = m_transferQuantities[dirIndex];
    	td.tickRate = m_tickRates[dirIndex];
    	td.redstoneControl = m_cache.redstoneControls[dirIndex];
    	td.color = m_cache.colors[dirIndex];
    	return td;
    }

    @Override
    protected void assignNetwork(PipeNetwork network, PipeType type)
    {
    	switch (type)
    	{
    	case Item:
    		m_itemNetwork = network;
    		break;
    	case Fluid:
    		m_fluidNetwork = network;
    		break;
    	case Energy:
    		m_energyNetwork = network;
    		break;
    	}
    }
    
    public PipeNetwork getNetwork(PipeType type)
    {
    	switch (type)
    	{
    	case Item:
    		return m_itemNetwork;
    	case Fluid:
    		return m_fluidNetwork;
    	case Energy:
    		return m_energyNetwork;
		default:
			return null;
    	}
    }
	
    @Override
	public Color getRenderColorForSide(Direction dir)
	{
    	if (m_cache == null)
    		return Color.Transparent;
    	int index = SwiftUtils.dirToIndex(dir);
    	Color c = m_cache.getColor(MIN_ITEM_INDEX + index);
    	if (c != Color.Transparent)
    		return c;
    	c = m_cache.getColor(MIN_FLUID_INDEX + index);
    	if (c != Color.Transparent)
    		return c;
    	c = m_cache.getColor(MIN_ENERGY_INDEX + index);
		return c;
	}

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag nbt)
    {
        super.read(provider, nbt);

        for (int i = 0; i < m_itemFilters.length; ++i)
        {
        	m_itemFilters[i] = null;
            refreshFilter(MIN_ITEM_INDEX + i);
        }

        for (int i = 0; i < m_fluidFilters.length; ++i)
        {
        	m_fluidFilters[i] = null;
            refreshFilter(MIN_FLUID_INDEX + i);
        }
    }

    public void serializeBufferForContainer(RegistryFriendlyByteBuf buffer, Player player, Direction startingDir)
    {
        NeighboringItems items = new NeighboringItems(level, worldPosition,
        		OmniPipeBlock::isConnectableNeighbor, startingDir, OmniPipeTileEntity::getPipeTypeForStartingDir);
        m_cache.serialize(buffer, items);
        BlockPos.STREAM_CODEC.encode(buffer, getBlockPos());
    }
    
    private static PipeType getPipeTypeForStartingDir(BlockGetter blockGetter, BlockPos pos, Direction dir)
    {
        BlockPos neighborPos = pos.relative(dir);
        BlockEntity blockEntity = blockGetter.getBlockEntity(neighborPos);
        if (blockEntity != null)
        {
        	if (SwiftUtils.isItemHandler(blockEntity, dir))
        		return PipeType.Item;
        	else if (SwiftUtils.isFluidHandler(blockEntity, dir))
        		return PipeType.Fluid;
        	else if (SwiftUtils.isEnergyHandler(blockEntity, dir))
        		return PipeType.Energy;
        }
        return PipeType.Item;
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
    	if (transferIndex < MAX_ITEM_INDEX)
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
	                    m_itemFilters[transferIndex - MIN_ITEM_INDEX] = filter;
	                    return;
	                }
	            }
	        }
	
	        m_itemFilters[transferIndex - MIN_ITEM_INDEX] = null;
    	}
    	else if (transferIndex < MAX_FLUID_INDEX)
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
                        m_fluidFilters[transferIndex - MIN_FLUID_INDEX] = filter;
                        return;
                    }
                }
            }
	
	        m_fluidFilters[transferIndex - MIN_FLUID_INDEX] = null;
    	}
    }
    
    @Override
    protected PipeTransferHandler<?> createTransferHandler(int transferIndex)
    {
    	if (transferIndex < MAX_ITEM_INDEX)
    	{
        	ItemPipeTransferHandler newHandler = new ItemPipeTransferHandler();
        	newHandler.pipe = this;
        	newHandler.neighborDir = SwiftUtils.indexToDir(transferIndex);
        	newHandler.handlerDir = newHandler.neighborDir.getOpposite();
        	return newHandler;
    	}
    	else if (transferIndex < MAX_FLUID_INDEX)
    	{
        	FluidPipeTransferHandler newHandler = new FluidPipeTransferHandler();
        	newHandler.pipe = this;
        	newHandler.neighborDir = SwiftUtils.indexToDir(transferIndex);
        	newHandler.handlerDir = newHandler.neighborDir.getOpposite();
        	return newHandler;
    	}
    	else if (transferIndex < MAX_ENERGY_INDEX)
    	{
        	EnergyPipeTransferHandler newHandler = new EnergyPipeTransferHandler();
        	newHandler.pipe = this;
        	newHandler.neighborDir = SwiftUtils.indexToDir(transferIndex);
        	newHandler.handlerDir = newHandler.neighborDir.getOpposite();
        	return newHandler;
    	}
    	else
    	{
    		return null;
    	}
    }

    @Override
    protected PipeTransferQuantity getTransferQuantity(int transferIndex, int stacks)
    {
    	if (transferIndex < MAX_ITEM_INDEX)
    		return ItemPipeTileEntity.getTransferQuantity(stacks);
    	else if (transferIndex < MAX_FLUID_INDEX)
    		return FluidPipeTileEntity.getTransferQuantity(stacks);
    	else
    		return EnergyPipeTileEntity.getTransferQuantity(stacks);
    }
    
    @Override
    protected PipeType getTypeForIndex(int transferIndex)
    {
    	if (transferIndex < MAX_ITEM_INDEX)
    		return PipeType.Item;
    	else if (transferIndex < MAX_FLUID_INDEX)
    		return PipeType.Fluid;
    	else
    		return PipeType.Energy;
    }

    @Override
    protected void removeAllHandlers(PipeType type, PipeNetwork network)
    {
    	int start = getStartIndex(type);
    	int end = getEndIndex(type);
        for (int i = start; i < end; ++i)
        {
	    	RedstoneControl rc = m_cache.getRedstoneControl(i);
	    	if (rc != RedstoneControl.Disabled)
	    	{
	    		PipeTransferHandler<?> handler = createTransferHandler(i);
	    		handler.priority = m_cache.getPriority(i);
		    	TransferDirection td = m_cache.getTransferDirection(i);
		    	network.removeHandler(handler, td);
	    	}
	    }
    }

    @Override
    protected void addAllHandlers(PipeType type, PipeNetwork network)
    {
    	int start = getStartIndex(type);
    	int end = getEndIndex(type);
        for (int i = start; i < end; ++i)
        {
	    	RedstoneControl rc = m_cache.getRedstoneControl(i);
	    	if (rc != RedstoneControl.Disabled)
	    	{
	    		PipeTransferHandler<?> handler = createTransferHandler(i);
	    		handler.priority = m_cache.getPriority(i);
		    	TransferDirection td = m_cache.getTransferDirection(i);
		    	network.addHandler(handler, td);
	    	}
	    }
    }

    @Override
    public void getHandlers(PipeType type, List<PipeTransferHandler<?>> insertHandlers, List<PipeTransferHandler<?>> extractHandlers)
    {
    	int start = getStartIndex(type);
    	int end = getEndIndex(type);
        for (int i = start; i < end; ++i)
        {
	    	RedstoneControl rc = m_cache.getRedstoneControl(i);
	    	if (rc != RedstoneControl.Disabled)
	    	{
	    		PipeTransferHandler<?> handler = createTransferHandler(i);
	    		handler.priority = m_cache.getPriority(i);
		    	TransferDirection td = m_cache.getTransferDirection(i);
		    	if (td == TransferDirection.Insert)
		    		insertHandlers.add(handler);
		    	else
		    		extractHandlers.add(handler);
	    	}
	    }
    }
    
    private int getStartIndex(PipeType type)
    {
    	switch (type)
    	{
    	case Item:
    		return MIN_ITEM_INDEX;
    	case Fluid:
    		return MIN_FLUID_INDEX;
    	case Energy:
    		return MIN_ENERGY_INDEX;
		default:
			return 0;
    	}
    }
    
    private int getEndIndex(PipeType type)
    {
    	switch (type)
    	{
    	case Item:
    		return MAX_ITEM_INDEX;
    	case Fluid:
    		return MAX_FLUID_INDEX;
    	case Energy:
    		return MAX_ENERGY_INDEX;
		default:
			return 0;
    	}
    }

    protected PipeNetwork m_itemNetwork;
    protected PipeNetwork m_fluidNetwork;
    protected PipeNetwork m_energyNetwork;
    protected Filter<ItemStack>[] m_itemFilters;
    protected Filter<FluidStack>[] m_fluidFilters;

    public static final int MIN_ITEM_INDEX = 0;
    public static final int MAX_ITEM_INDEX = 6;

    public static final int MIN_FLUID_INDEX = 6;
    public static final int MAX_FLUID_INDEX = 12;
    
    public static final int MIN_ENERGY_INDEX = 12;
    public static final int MAX_ENERGY_INDEX = 18;

    public static final int MAX_TRANSFER_INDEX = MAX_ENERGY_INDEX;
}
