package swiftmod.pipes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.common.world.chunk.ForcedChunkManager;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import swiftmod.common.Color;
import swiftmod.common.ContainerInventory;
import swiftmod.common.IChunkLoadable;
import swiftmod.common.RedstoneControl;
import swiftmod.common.Swift;
import swiftmod.common.SwiftDataComponents;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftUtils;
import swiftmod.common.TileEntityBase;
import swiftmod.common.TransferDirection;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeItem;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.pipes.networks.PipeNetwork;
import swiftmod.pipes.networks.PipeNetworks;

// U = IItemHandler or IFluidHandler
// V = ItemStack or FluidStack
public abstract class PipeTileEntity extends TileEntityBase
        implements MenuProvider, IChunkLoadable
{
    @FunctionalInterface
    public interface SideUpgradeInventoryBuilder
    {
    	UpgradeInventory build(int index);
    };
    
    protected PipeTileEntity(BlockEntityType<?> type, EnumSet<PipeType> pipeTypes, BlockPos pos, BlockState state,
    		UpgradeInventory upgradeInventory, SideUpgradeInventoryBuilder sideUpgradeInventorySupplier)
    {
        this(type, pipeTypes, pos, state, upgradeInventory, Direction.values().length, sideUpgradeInventorySupplier);
    }
    
    protected PipeTileEntity(BlockEntityType<?> type, EnumSet<PipeType> pipeTypes, BlockPos pos, BlockState state,
    		UpgradeInventory upgradeInventory, int numSideUpgradeInventories,
    		SideUpgradeInventoryBuilder sideUpgradeInventorySupplier)
    {
        super(type, pos, state);

        m_cache = new PipeDataCache(numSideUpgradeInventories);
        m_cache.setTransferDirectionChangedCallback(this::onTransferDirectionChanged);
        m_cache.setRedstoneControlChangedCallback(this::onRedstoneControlChanged);
        m_cache.setColorChangedCallback(this::onColorChanged);
        m_cache.setPriorityChangedCallback(this::onPriorityChanged);
        
        m_pipeTypes = pipeTypes;
        m_init = true;
        m_isRemoving = false;

        m_baseUpgradeInventory = upgradeInventory;
        m_baseUpgradeInventory.setMarkDirtyCallback(this::setChanged);
        m_baseUpgradeInventory.setContentsChangedCallback(this::onBaseUpgradesChanged);

        m_sideUpgradeInventories = new UpgradeInventory[numSideUpgradeInventories];
        m_tickRates = new int[numSideUpgradeInventories];
        m_transferQuantities = new PipeTransferQuantity[numSideUpgradeInventories];
        for (int i = 0; i < numSideUpgradeInventories; ++i)
        {
            m_tickRates[i] = 20; // Default with no speed upgrades/downgrades.
            m_transferQuantities[i] = getTransferQuantity(i, 0);
            m_sideUpgradeInventories[i] = sideUpgradeInventorySupplier.build(i);
            m_sideUpgradeInventories[i].setMarkDirtyCallback(this::setChanged);
            int idx = i;
            m_sideUpgradeInventories[i].setContentsChangedCallback((cv) -> onSideUpgradesChanged(idx));
        }
        m_tickCounter = 0;
    }
    
    public PipeDataCache getCache()
    {
    	return m_cache;
    }
    
    public EnumSet<PipeType> getPipeType()
    {
    	return m_pipeTypes;
    }
    
    public boolean isPipeType(PipeType type)
    {
    	return m_pipeTypes.contains(type);
    }
    
    public boolean isPipeType(EnumSet<PipeType> type)
    {
    	for (PipeType t : type)
    	{
    		if (m_pipeTypes.contains(t))
    			return true;
    	}
    	return false;
    }
    
    public boolean isSamePipeType(PipeTileEntity other)
    {
    	return isPipeType(other.m_pipeTypes);
    }
    
    public boolean canConnectDirection(Direction dir)
    {
    	return PipeBlock.stateAllowsConnections(getBlockState(), dir);
    }
    
    public boolean canConnectToWormhole()
    {
    	for (PipeType t : m_pipeTypes)
    	{
    		if (t.tryGetChannelType().hasConversion)
    			return true;
    	}
    	return false;
    }
    
    public void clearNetwork(PipeType type)
    {
    	PipeNetwork oldNetwork = getNetwork(type);
    	if (oldNetwork != null)
    	{
    		removeAllHandlers(type, oldNetwork);
    		oldNetwork.decrementPipeCounter();
    	}
    	assignNetwork(null, type);
    }

    public void setNetwork(PipeNetwork network)
    {
    	PipeType type = network.getType();
    	PipeNetwork oldNetwork = getNetwork(type);
    	if (oldNetwork != null)
    	{
    		removeAllHandlers(type, oldNetwork);
    		oldNetwork.decrementPipeCounter();
    	}
    	assignNetwork(network, type);
    	if (network != null)
    	{
    		network.incrementPipeCounter();
    		addAllHandlers(type, network);
    	}
    }

    protected abstract void assignNetwork(PipeNetwork network, PipeType type);
    public abstract PipeNetwork getNetwork(PipeType type);
    public abstract List<PipeNetwork> getNetworks();

    @Override
    public void write(HolderLookup.Provider provider, CompoundTag nbt)
    {
        super.write(provider, nbt);
        m_cache.write(nbt);

        nbt.put(SwiftUtils.tagName("baseUpgradeSlots"), m_baseUpgradeInventory.serializeNBT(provider));

        ListTag sideUpgradeNBT = new ListTag();
        for (int i = 0; i < m_sideUpgradeInventories.length; ++i)
        {
            CompoundTag side = new CompoundTag();
            side.putInt(SwiftUtils.tagName("direction"), i);
            side.put(SwiftUtils.tagName("inventory"), m_sideUpgradeInventories[i].serializeNBT(provider));
            sideUpgradeNBT.add(side);
        }
        nbt.put(SwiftUtils.tagName("sideUpgradeSlots"), sideUpgradeNBT);
    }

    @Override
    public void read(HolderLookup.Provider provider, CompoundTag nbt)
    {
        super.read(provider, nbt);
        m_cache.read(nbt);

        m_baseUpgradeInventory.deserializeNBT(provider, nbt.getCompound(SwiftUtils.tagName("baseUpgradeSlots")));
        onBaseUpgradesChanged(m_baseUpgradeInventory);

        ListTag sideUpgradeNBT = nbt.getList(SwiftUtils.tagName("sideUpgradeSlots"), Tag.TAG_COMPOUND);
        if (sideUpgradeNBT == null)
        {
            Swift.LOGGER.warn("Invalid item pipe NBT. Side upgrades ignored.");
            return;
        }

        for (int i = 0; i < m_sideUpgradeInventories.length; ++i)
            m_sideUpgradeInventories[i].clearContent();
        
        for (int i = 0; i < sideUpgradeNBT.size(); ++i)
        {
            CompoundTag sideNBT = sideUpgradeNBT.getCompound(i);
            int index = sideNBT.getInt(SwiftUtils.tagName("direction"));
            if (index < m_sideUpgradeInventories.length)
            {
                CompoundTag inventoryNBT = sideNBT.getCompound(SwiftUtils.tagName("inventory"));
                m_sideUpgradeInventories[index].deserializeNBT(provider, inventoryNBT);
            }
        }
        
        for (int i = 0; i < m_sideUpgradeInventories.length; ++i)
            onSideUpgradesChangedWorker(i);
    }
    
    @Override
    public void onChunkUnloaded()
    {
    	for (PipeType type : m_pipeTypes)
    	{
    		PipeNetwork network = getNetwork(type);
    		if (network != null)
    			removeAllHandlers(type, network);
    		network.decrementPipeCounter();
    	}
    }

    @Override
    public boolean isChunkLoaded()
    {
        int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.ChunkLoaderUpgrade);
        if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
        {
            ItemStack stack = m_baseUpgradeInventory.getItem(slot);
            return stack != null &&
            	   !stack.isEmpty() &&
            	   stack.getItem() == SwiftItems.s_chunkLoaderUpgradeItem.get();
        }
        else
        {
            return false;
        }
    }
    
    protected List<Integer> getDataIndicesForDirection(Direction dir)
    {
    	ArrayList<Integer> a = new ArrayList<Integer>(1);
    	a.add(SwiftUtils.dirToIndex(dir));
    	return a;
    }
    
    protected <T> List<T> serializeDataComponents(T[] t, List<Integer> indices)
    {
    	// Returns a subset of a list comprised of the elements at the given indices.
    	// See https://stackoverflow.com/questions/43694410/get-multiple-elements-from-list-by-indices-in-constant-time
        return indices.stream().map(i -> t[i]).collect(Collectors.toList());
    }
    
    protected List<Integer> serializeIntegerDataComponents(int[] t, List<Integer> indices)
    {
    	// Returns a subset of a list comprised of the elements at the given indices.
    	// See https://stackoverflow.com/questions/43694410/get-multiple-elements-from-list-by-indices-in-constant-time
        return indices.stream().map(i -> t[i]).collect(Collectors.toList());
    }

    public boolean copyTileEntityUpgrades(ItemStack itemStack, Direction dir)
    {
        if (dir == null || itemStack == null || itemStack.isEmpty())
            return false;
        
        List<Integer> indices = getDataIndicesForDirection(dir);
        if (indices.size() > m_cache.getDataSize())
        	return false;

        itemStack.set(SwiftDataComponents.REDSTONE_CONTROL_LIST_DATA_COMPONENT, serializeDataComponents(m_cache.getRedstoneControls(), indices));
        itemStack.set(SwiftDataComponents.TRANSFER_DIRECTION_LIST_DATA_COMPONENT, serializeDataComponents(m_cache.getTransferDirections(), indices));
        itemStack.set(SwiftDataComponents.PRIORITY_LIST_DATA_COMPONENT, serializeIntegerDataComponents(m_cache.getPriorities(), indices));
        itemStack.set(SwiftDataComponents.COLOR_LIST_DATA_COMPONENT, serializeDataComponents(m_cache.getColors(), indices));
        
        // TODO: (Re-)Implement copy-pasting for filters.
        //itemStack.set(SwiftDataComponents.ITEM_STACK_LIST_DATA_COMPONENT, serializeDataComponents(m_cache.getRedstoneControls(), indices));
        
        // TODO: Would be cool to add the ability to insert/remove upgrades automatically when copy-pasting.
        
        return true;
    }
    
    protected <T> boolean applyDataComponents(List<Integer> indices, List<T> values, BiConsumer<Integer, T> consumer)
    {
        if (indices.size() != values.size())
        	return false;
        for (int i = 0; i < indices.size(); ++i)
        	consumer.accept(indices.get(i), values.get(i));
        return true;
    }

    public boolean pasteTileEntityUpgrades(ItemStack itemStack, Direction dir)
    {
        if (dir == null || itemStack == null || itemStack.isEmpty())
            return false;

        List<Integer> indices = getDataIndicesForDirection(dir);
        
        if (!applyDataComponents(indices, itemStack.get(SwiftDataComponents.REDSTONE_CONTROL_LIST_DATA_COMPONENT), m_cache::setRedstoneControl))
        	return false;
        if (!applyDataComponents(indices, itemStack.get(SwiftDataComponents.TRANSFER_DIRECTION_LIST_DATA_COMPONENT), m_cache::setTransferDirection))
        	return false;
        if (!applyDataComponents(indices, itemStack.get(SwiftDataComponents.PRIORITY_LIST_DATA_COMPONENT), m_cache::setPriority))
        	return false;
        if (!applyDataComponents(indices, itemStack.get(SwiftDataComponents.COLOR_LIST_DATA_COMPONENT), m_cache::setColor))
        	return false;

        // TODO: (Re-)Implement copy-pasting for filters.
        //applyDataComponents(indices, itemStack.get(SwiftDataComponents.ITEM_STACK_LIST_DATA_COMPONENT), m_cache::setRedstoneControl);
        // TODO: Would be cool to add the ability to insert/remove upgrades automatically when copy-pasting.
        
        return true;
    }
    
    public static void removeTileEntityUpgrades(ItemStack itemStack)
    {
        itemStack.remove(SwiftDataComponents.REDSTONE_CONTROL_LIST_DATA_COMPONENT);
        itemStack.remove(SwiftDataComponents.TRANSFER_DIRECTION_LIST_DATA_COMPONENT);
        itemStack.remove(SwiftDataComponents.PRIORITY_LIST_DATA_COMPONENT);
        itemStack.remove(SwiftDataComponents.COLOR_LIST_DATA_COMPONENT);
        itemStack.remove(SwiftDataComponents.ITEM_STACK_LIST_DATA_COMPONENT);
    }
    
    public boolean tryAddUpgrade(ItemStack itemStack)
    {
    	return tryAddUpgrade(itemStack, null);
    }

    public boolean tryAddUpgrade(ItemStack itemStack, Direction dir)
    {
        if (itemStack.isEmpty())
            return false;
        if (!(itemStack.getItem() instanceof UpgradeItem))
            return false;
        UpgradeType upgrade = ((UpgradeItem)itemStack.getItem()).getType();
        if (dir == null)
        {
            return tryAddBaseUpgrade(itemStack, upgrade, dir);
        }
        else
        {
            int slot = m_baseUpgradeInventory.getSlotForUpgrade(upgrade);
            if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
            {
                return tryAddBaseUpgrade(itemStack, upgrade, dir);
            }
            else
            {
                UpgradeInventory sideUpgradeInventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(dir)];
                if (sideUpgradeInventory.getContainerSize() == 0)
                	return false;
                int slot2 = sideUpgradeInventory.getSlotForUpgrade(upgrade);
                if (slot2 < 0 || slot2 >= sideUpgradeInventory.getContainerSize())
                    return false;
                if (!sideUpgradeInventory.canPlaceItem(slot2, itemStack))
                    return false;
                ItemStack simulation = sideUpgradeInventory.tryInsert(slot2, itemStack, true);
                if (itemStack.getCount() <= simulation.getCount())
                    return false;
                ItemStack transfer = itemStack.split(1);
                sideUpgradeInventory.tryInsert(slot2, transfer, false);
                return true;
            }
        }
    }

    public boolean tryAddUpgrade(Inventory inventory, InteractionHand hand)
    {
        return tryAddUpgrade(inventory, hand, null);
    }

    public boolean tryAddUpgrade(Inventory inventory, InteractionHand hand, Direction dir)
    {
        ItemStack itemStack = ItemStack.EMPTY;
        if (hand == InteractionHand.OFF_HAND)
        {
            itemStack = inventory.offhand.get(0);
        }
        else
        {
            if (Inventory.isHotbarSlot(inventory.selected))
                itemStack = inventory.items.get(inventory.selected);
            else
                return false;
        }
    	return tryAddUpgrade(itemStack, dir);
    }

    protected boolean tryAddBaseUpgrade(ItemStack itemStack, UpgradeType upgradeType, Direction dir)
    {
        int slot = m_baseUpgradeInventory.getSlotForUpgrade(upgradeType);
        if (slot < 0 || slot >= m_baseUpgradeInventory.getContainerSize())
            return false;
        if (!m_baseUpgradeInventory.canPlaceItem(slot, itemStack))
            return false;
        ItemStack simulation = m_baseUpgradeInventory.tryInsert(slot, itemStack, true);
        if (itemStack.getCount() <= simulation.getCount())
            return false;
        ItemStack transfer = itemStack.split(1);
        m_baseUpgradeInventory.tryInsert(slot, transfer, false);
        return true;
    }
    
    public void onRemove(Level world, BlockPos blockPos)
    {
    	m_isRemoving = true;
		PipeNetworks.removePipe(this);
    	dropAllContents(world, blockPos);
    }
    
    protected void removeAllHandlers(PipeType type, PipeNetwork network)
    {
        for (int i = 0; i < m_sideUpgradeInventories.length; ++i)
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
    
    protected void addAllHandlers(PipeType type, PipeNetwork network)
    {
        for (int i = 0; i < m_sideUpgradeInventories.length; ++i)
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
    
    public void getHandlers(PipeType type, List<PipeTransferHandler<?>> insertHandlers, List<PipeTransferHandler<?>> extractHandlers)
    {
        for (int i = 0; i < m_sideUpgradeInventories.length; ++i)
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
    
    public void updateNetworkConnection(Direction dir, boolean connected)
    {
    	if (connected)
    		PipeNetworks.reconnectPipe(this, dir);
    	else
    		PipeNetworks.disconnectPipe(this, dir);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PipeTileEntity tileEntity)
    {
    	tileEntity.serverTick(level);
    }
    
    public abstract PipeTransferData<?> getTransferData(PipeType type, Direction neighborDir, Direction handlerDir);

	public void serverTick(Level level)
    {
    	if (m_init && !m_isRemoving)
    	{
    		PipeNetworks.addPipe(this);
    		m_init = false;
    	}
    }
	
	public abstract Color getRenderColorForSide(Direction dir);

    protected abstract int maxEffectiveSpeedUpgrades();

    protected abstract int maxEffectiveStackUpgrades();

    protected abstract int maxEffectiveSpeedDowngrades();

    protected abstract PipeTransferQuantity getTransferQuantity(int transferIndex, int stacks);
    
    protected abstract PipeType getTypeForIndex(int transferIndex);
    
    protected void onTransferDirectionChanged(int transferIndex, TransferDirection transferDir)
    {
    	PipeNetwork network = getNetwork(getTypeForIndex(transferIndex));
    	if (network == null)
    		return;
    	
    	RedstoneControl rc = m_cache.redstoneControls[transferIndex];
    	if (rc != RedstoneControl.Disabled)
    	{
    		PipeTransferHandler<?> handler = createTransferHandler(transferIndex);
    		handler.priority = m_cache.getPriority(transferIndex);
    		network.removeHandler(handler, transferDir.opposite());
    		network.addHandler(handler, transferDir);
    	}
    	setChanged();
    }
    
    protected void onRedstoneControlChanged(int transferIndex, RedstoneControl rcPrev, RedstoneControl rcNew)
    {
    	PipeNetwork network = getNetwork(getTypeForIndex(transferIndex));
    	if (network == null)
    		return;
    	
    	if (rcPrev != RedstoneControl.Disabled && rcNew == RedstoneControl.Disabled)
    	{
    		TransferDirection transferDir = m_cache.getTransferDirection(transferIndex);
    		PipeTransferHandler<?> handler = createTransferHandler(transferIndex);
    		handler.priority = m_cache.getPriority(transferIndex);
    		network.removeHandler(handler, transferDir);
    	}
    	else if (rcPrev == RedstoneControl.Disabled && rcNew != RedstoneControl.Disabled)
    	{
    		TransferDirection transferDir = m_cache.getTransferDirection(transferIndex);
    		PipeTransferHandler<?> handler = createTransferHandler(transferIndex);
    		handler.priority = m_cache.getPriority(transferIndex);
    		network.addHandler(handler, transferDir);
    	}
    	setChanged();
    }
    
    protected void onColorChanged(int transferIndex, Color colorPrev, Color colorNew)
    {
    	// TODO: This is still broken.
    	if (!level.isClientSide)
    	{
    		// Send a message to the client to force a redraw of the colored attachment.
            setChanged();
    		//level.setBlocksDirty(getBlockPos(), getBlockState(), getBlockState());
    		level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            //level.setBlock(getBlockPos(), getBlockState(), 3);
    	}
    }
    
    protected void onPriorityChanged(int transferIndex, int oldPriority, int newPriority)
    {
    	PipeNetwork network = getNetwork(getTypeForIndex(transferIndex));
    	if (network == null)
    		return;
    	
    	RedstoneControl rc = m_cache.redstoneControls[transferIndex];
    	TransferDirection td = m_cache.transferDirections[transferIndex];
    	if (rc != RedstoneControl.Disabled)
    	{
    		PipeTransferHandler<?> handler = createTransferHandler(transferIndex);
    		handler.priority = oldPriority;
    		network.removeHandler(handler, td);
    		handler.priority = newPriority;
    		network.addHandler(handler, td);
    	}
    	setChanged();
    }

    protected void onSideUpgradesChanged(int transferIndex)
    {
    	onSideUpgradesChangedWorker(transferIndex);
    }
    
    private void onSideUpgradesChangedWorker(int transferIndex)
    {
    	UpgradeInventory sideUpgradeInventory = m_sideUpgradeInventories[transferIndex];
        int slot = sideUpgradeInventory.getSlotForUpgrade(UpgradeType.StackUpgrade);
        if (slot >= 0 && slot < sideUpgradeInventory.getContainerSize())
        {
        	int numStackUpgrades = 0;
            ItemStack stack = sideUpgradeInventory.getItem(slot);
            if (stack.getCount() > 0)
            {
                UpgradeItem upgradeItem = (UpgradeItem) stack.getItem();
                switch (upgradeItem.getType())
                {
                case StackUpgrade:
                	numStackUpgrades = stack.getCount();
            		break;
                case UltimateStackUpgrade:
                	numStackUpgrades = maxEffectiveStackUpgrades();
            		break;
            	default:
                	numStackUpgrades = 0;
            		break;
                }
            }
            m_transferQuantities[transferIndex] = getTransferQuantity(transferIndex, numStackUpgrades);
        }
        
        slot = sideUpgradeInventory.getSlotForUpgrade(UpgradeType.SpeedUpgrade);
        if (slot >= 0 && slot < sideUpgradeInventory.getContainerSize())
        {
            int numSpeedUpgrades = 0;
            ItemStack stack = sideUpgradeInventory.getItem(slot);
            if (stack.getCount() > 0)
            {
                UpgradeItem upgradeItem = (UpgradeItem) stack.getItem();
                switch (upgradeItem.getType())
                {
                case SpeedUpgrade:
                    numSpeedUpgrades += stack.getCount();
            		break;
                case SpeedDowngrade:
                    numSpeedUpgrades -= stack.getCount();
            		break;
            	default:
            		break;
                }
            }
            
            if (numSpeedUpgrades > 0)
                numSpeedUpgrades = Math.min(numSpeedUpgrades, maxEffectiveSpeedUpgrades());
            else if (numSpeedUpgrades < 0)
                numSpeedUpgrades = Math.max(numSpeedUpgrades, -maxEffectiveSpeedDowngrades());

            if (numSpeedUpgrades >= 19)
            {
                m_tickRates[transferIndex] = 1;
            }
            else if (numSpeedUpgrades > 0)
            {
            	m_tickRates[transferIndex] = 20 - numSpeedUpgrades;
            }
            else if (numSpeedUpgrades < 0)
            {
            	m_tickRates[transferIndex] = 20 + (20 * -numSpeedUpgrades);
            }
            else
            {
            	m_tickRates[transferIndex] = 20;
            }
        }
    }

    private void onBaseUpgradesChanged(ContainerInventory cv)
    {
        // TODO: Fix chunk loading.
    	/*
    	if (hasLevel() && level instanceof ServerLevel)
        {
            int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.ChunkLoaderUpgrade);
            if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
            {
                ItemStack stack = m_baseUpgradeInventory.getItem(slot);
                ChunkPos chunk = new ChunkPos(worldPosition);
                ServerLevel serverWorld = (ServerLevel) level;
                boolean add = stack != null && !stack.isEmpty();
                
                ForcedChunkManager.forceChunk(serverWorld, Swift.MOD_NAME, worldPosition, chunk.x, chunk.z, add, true);
            }
        }
        */
    }

    /*protected void onChannelUpdate(ChannelSpec spec)
    {
    	if (hasLevel() && level instanceof ServerLevel)
    	{
	        if (spec != null)
	            getChannelManager().reattach(spec, this);
	        else
	            getChannelManager().detach(this);
    	}
    }

    private ChannelSpec getChannel(ItemStack stack)
    {
        if (stack.getCount() > 0)
        {
            Item item = stack.getItem();
            if (item instanceof TeleporterUpgradeItem)
            {
                CompoundTag channelNBT = stack.getTagElement(TeleporterUpgradeItem.NBT_TAG);
                if (channelNBT != null)
                {
                    ChannelSpec spec = new ChannelSpec(channelNBT);
                    spec.type = getChannelType();
                    return spec;
                }
            }
        }

        return null;
    }*/

    /**
     * When this tile entity is destroyed, drop all of its contents into the world
     * 
     * @param world
     * @param blockPos
     */
    public void dropAllContents(Level world, BlockPos blockPos)
    {
    	Containers.dropContents(world, blockPos, m_baseUpgradeInventory);
        for (int i = 0; i < m_sideUpgradeInventories.length; ++i)
        	Containers.dropContents(world, blockPos, m_sideUpgradeInventories[i]);
    }
    
    protected abstract PipeTransferHandler<?> createTransferHandler(int transferIndex);

    protected PipeDataCache m_cache;
    protected UpgradeInventory m_baseUpgradeInventory;
    protected UpgradeInventory[] m_sideUpgradeInventories;
    protected int m_tickCounter;
    protected PipeTransferQuantity[] m_transferQuantities;
    protected int[] m_tickRates;
    
    private boolean m_init;
    private boolean m_isRemoving;
    
    protected final EnumSet<PipeType> m_pipeTypes;
}
