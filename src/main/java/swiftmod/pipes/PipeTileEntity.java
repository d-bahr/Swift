package swiftmod.pipes;

import java.util.ArrayList;
import java.util.Set;
import java.util.function.Supplier;

import io.netty.util.internal.ThreadLocalRandom;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.world.ForgeChunkManager;
import swiftmod.common.Color;
import swiftmod.common.ContainerInventory;
import swiftmod.common.CopyPastaItem;
import swiftmod.common.Filter;
import swiftmod.common.FilterMatchResult;
import swiftmod.common.IChunkLoadable;
import swiftmod.common.RedstoneControl;
import swiftmod.common.Swift;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftUtils;
import swiftmod.common.TileEntityBase;
import swiftmod.common.TransferDirection;
import swiftmod.common.channels.ChannelAttachment;
import swiftmod.common.channels.ChannelData;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.common.channels.OwnerBasedChannelManager;
import swiftmod.common.upgrades.ChannelConfigurationDataCache;
import swiftmod.common.upgrades.FilterUpgradeItem;
import swiftmod.common.upgrades.SideUpgradeDataCache;
import swiftmod.common.upgrades.TeleporterUpgradeItem;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeItem;
import swiftmod.common.upgrades.UpgradeType;

// U = IItemHandler or IFluidHandler
// V = ItemStack or FluidStack
public abstract class PipeTileEntity<T extends PipeDataCache, U, V> extends TileEntityBase<T>
        implements MenuProvider, IChunkLoadable
{
    @SuppressWarnings("unchecked")
    protected PipeTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, T cache, UpgradeInventory upgradeInventory,
            Supplier<UpgradeInventory> sideUpgradeInventorySupplier)
    {
        super(type, pos, state, cache);

        m_baseUpgradeInventory = upgradeInventory;
        m_baseUpgradeInventory.setMarkDirtyCallback(this::setChanged);
        m_baseUpgradeInventory.setContentsChangedCallback(this::onBaseUpgradesChanged);

        Direction[] dirs = Direction.values();
        int len = dirs.length;
        m_sideUpgradeInventories = new UpgradeInventory[len];
        m_filters = new Filter[len];
        for (int i = 0; i < len; ++i)
        {
            m_filters[i] = null;
            m_sideUpgradeInventories[i] = sideUpgradeInventorySupplier.get();
            m_sideUpgradeInventories[i].setMarkDirtyCallback(this::setChanged);
            Direction d = dirs[i];
            m_sideUpgradeInventories[i].setContentsChangedCallback((cv) -> onSideUpgradesChanged(d));
        }
        m_tickCounter = 0;
    }

    @Override
    public void write(CompoundTag nbt)
    {
        super.write(nbt);

        getCache().write(nbt, false);
        nbt.put(SwiftUtils.tagName("baseUpgradeSlots"), m_baseUpgradeInventory.serializeNBT());

        ListTag sideUpgradeNBT = new ListTag();
        for (int i = 0; i < m_sideUpgradeInventories.length; ++i)
        {
            CompoundTag side = new CompoundTag();
            side.putInt(SwiftUtils.tagName("direction"), i);
            side.put(SwiftUtils.tagName("inventory"), m_sideUpgradeInventories[i].serializeNBT());
            sideUpgradeNBT.add(side);
        }
        nbt.put(SwiftUtils.tagName("sideUpgradeSlots"), sideUpgradeNBT);
    }

    @Override
    public void read(CompoundTag nbt)
    {
        super.read(nbt);

        getCache().read(nbt);
        m_baseUpgradeInventory.deserializeNBT(nbt.getCompound(SwiftUtils.tagName("baseUpgradeSlots")));

        if (hasLevel() && level instanceof ServerLevel)
        {
            int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.TeleportUpgrade);
            if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
            {
                ItemStack stack = m_baseUpgradeInventory.getItem(slot);
                ChannelSpec spec = getChannel(stack);
                if (spec != null && hasLevel())
                    getChannelManager().attach(spec, this);
            }

            slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.ChunkLoaderUpgrade);
            if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
            {
                ItemStack stack = m_baseUpgradeInventory.getItem(slot);
                ChunkPos chunk = new ChunkPos(worldPosition);
                ServerLevel serverWorld = (ServerLevel) level;
                boolean add = stack != null && !stack.isEmpty();
                ForgeChunkManager.forceChunk(serverWorld, Swift.MOD_NAME, worldPosition, chunk.x, chunk.z, add, true);
            }
        }

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
                m_sideUpgradeInventories[index].deserializeNBT(inventoryNBT);
            }
        }

        for (int i = 0; i < m_filters.length; ++i)
        {
            m_filters[i] = null;
            refreshFilter(SwiftUtils.indexToDir(i));
        }
    }

    @Override
    public boolean isChunkLoaded()
    {
        int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.ChunkLoaderUpgrade);
        if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
        {
            ItemStack stack = m_baseUpgradeInventory.getItem(slot);
            return stack != null && !stack.isEmpty() && stack.getItem() == SwiftItems.s_chunkLoaderUpgradeItem;
        }
        else
        {
            return false;
        }
    }

    public boolean copyTileEntityUpgrades(CompoundTag nbt, Direction dir, CopyPastaItem.CopyType copyType)
    {
        switch (copyType)
        {
        case CopyLikeToLike:
        {
            ListTag list = new ListTag();
            if (dir == null)
                list.add(copyUpgrades(m_baseUpgradeInventory, null));
            else
                list.add(copyUpgrades(m_sideUpgradeInventories[SwiftUtils.dirToIndex(dir)], dir));
            nbt.put(SwiftUtils.tagName("targets"), list);
            return true;
        }
        case CopyBase:
        {
            ListTag list = new ListTag();
            list.add(copyUpgrades(m_baseUpgradeInventory, null));
            nbt.put(SwiftUtils.tagName("targets"), list);
            return true;
        }
        case CopySingleDirection:
        {
            if (dir == null)
                return false;
            ListTag list = new ListTag();
            list.add(copyUpgrades(m_sideUpgradeInventories[SwiftUtils.dirToIndex(dir)], dir));
            nbt.put(SwiftUtils.tagName("targets"), list);
            return true;
        }
        case CopyAllDirections:
        {
            ListTag list = new ListTag();
            for (Direction d : Direction.values())
                list.add(copyUpgrades(m_sideUpgradeInventories[SwiftUtils.dirToIndex(d)], d));
            nbt.put(SwiftUtils.tagName("targets"), list);
            return true;
        }
        case CopyAll:
        {
            ListTag list = new ListTag();
            list.add(copyUpgrades(m_baseUpgradeInventory, null));
            for (Direction d : Direction.values())
                list.add(copyUpgrades(m_sideUpgradeInventories[SwiftUtils.dirToIndex(d)], d));
            nbt.put(SwiftUtils.tagName("targets"), list);
            return true;
        }
        default:
            return false;
        }
    }

    private CompoundTag copyUpgrades(UpgradeInventory inventory, Direction dir)
    {
        CompoundTag nbt = new CompoundTag();
        if (dir == null)
        {
            nbt.putInt(SwiftUtils.tagName("direction"), -1);
        }
        else
        {
            int i = SwiftUtils.dirToIndex(dir);
            nbt.putInt(SwiftUtils.tagName("direction"), i);
            RedstoneControl rc = getCache().redstoneControls[i];
            TransferDirection td = getCache().transferDirections[i];
            RedstoneControl.write(nbt, rc);
            TransferDirection.write(nbt, td);
        }
        ListTag upgradeInventoryNBT = new ListTag();
        for (int i = 0; i < inventory.getContainerSize(); ++i)
        {
            ItemStack stack = inventory.getItem(i);
            if (stack == null)
                stack = ItemStack.EMPTY;
            upgradeInventoryNBT.add(stack.serializeNBT());
        }
        nbt.put(SwiftUtils.tagName("upgrades"), upgradeInventoryNBT);
        return nbt;
    }

    public boolean pasteTileEntityUpgrades(CompoundTag nbt, Direction dir, CopyPastaItem.CopyType copyType)
    {
        boolean hasMatch = false;
        ListTag list = nbt.getList(SwiftUtils.tagName("targets"), Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); ++i)
        {
            CompoundTag item = list.getCompound(i);
            int dirInt = item.getInt(SwiftUtils.tagName("direction"));
            RedstoneControl rc = RedstoneControl.read(item);
            TransferDirection td = TransferDirection.read(item);
            Direction sourceDir = (dirInt < 0) ? null : SwiftUtils.indexToDir(dirInt);

            switch (copyType)
            {
            case CopyLikeToLike:
            {
                if (dir == null && sourceDir == null)
                {
                    pasteUpgrades(item, m_baseUpgradeInventory, dir);
                    return true;
                }
                else if (dir != null && sourceDir != null)
                {
                    int idx = SwiftUtils.dirToIndex(dir);
                    pasteUpgrades(item, m_sideUpgradeInventories[idx], dir);
                    getCache().redstoneControls[idx] = rc;
                    getCache().transferDirections[idx] = td;
                    return true;
                }
                break;
            }
            case CopyBase:
            {
                if (sourceDir == null)
                {
                    pasteUpgrades(item, m_baseUpgradeInventory, null);
                    return true;
                }
                break;
            }
            case CopySingleDirection:
            {
                if (dir != null && sourceDir != null)
                {
                    int idx = SwiftUtils.dirToIndex(dir);
                    pasteUpgrades(item, m_sideUpgradeInventories[idx], dir);
                    getCache().redstoneControls[idx] = rc;
                    getCache().transferDirections[idx] = td;
                    return true;
                }
                break;
            }
            case CopyAllDirections:
            {
                if (sourceDir != null)
                {
                    int idx = SwiftUtils.dirToIndex(sourceDir);
                    pasteUpgrades(item, m_sideUpgradeInventories[idx], sourceDir);
                    getCache().redstoneControls[idx] = rc;
                    getCache().transferDirections[idx] = td;
                    hasMatch = true;
                }
                break;
            }
            case CopyAll:
            {
                if (sourceDir == null)
                {
                    pasteUpgrades(item, m_baseUpgradeInventory, null);
                }
                else
                {
                    int idx = SwiftUtils.dirToIndex(sourceDir);
                    pasteUpgrades(item, m_sideUpgradeInventories[idx], sourceDir);
                    getCache().redstoneControls[idx] = rc;
                    getCache().transferDirections[idx] = td;
                }
                hasMatch = true;
                break;
            }
            default:
                return false;
            }
        }
        return hasMatch;
    }

    private void pasteUpgrades(CompoundTag nbt, UpgradeInventory inventory, Direction targetDir)
    {
        ListTag list = nbt.getList(SwiftUtils.tagName("upgrades"), Tag.TAG_COMPOUND);
        int length = Math.min(inventory.getContainerSize(), list.size());
        for (int i = 0; i < length; ++i)
        {
            CompoundTag item = list.getCompound(i);
            ItemStack targetItem = inventory.getItem(i);
            ItemStack sourceItem = ItemStack.of(item);
            if (!sourceItem.isEmpty() && !targetItem.isEmpty() && sourceItem.getItem() == targetItem.getItem())
            {
                inventory.setItem(i, sourceItem.copy());

                if (sourceItem.getItem() instanceof TeleporterUpgradeItem)
                {
                    ChannelSpec spec = ChannelConfigurationDataCache.getChannel(sourceItem);
                    onChannelUpdate(spec);
                }
                else if (targetDir != null && sourceItem.getItem() instanceof FilterUpgradeItem)
                {
                    onSideUpgradesChanged(targetDir);
                }
            }
        }
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

    @Override
    public void setLevel(Level newLevel)
    {
        // If the world changes, detach the channel manager from the current world
        // and reattach it in the world. This may happen the first time the tile
        // entity is loaded, as the world is set after the NBT is loaded, for some
        // kind of obscure reason.
        if (hasLevel() && level instanceof ServerLevel)
        {
            getChannelManager().detach(this);
        }

        super.setLevel(newLevel);

        if (hasLevel() && level instanceof ServerLevel)
        {
            int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.TeleportUpgrade);
            if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
            {
                ItemStack stack = m_baseUpgradeInventory.getItem(slot);
                ChannelSpec spec = getChannel(stack);
                if (spec != null)
                    getChannelManager().attach(spec, this);
            }
        }
    }

    public static <T extends PipeDataCache, U, V> void serverTick(Level level, BlockPos pos, BlockState state, PipeTileEntity<T, U, V> tileEntity)
    {
    	tileEntity.serverTick(level);
    }

    public void serverTick(Level level)
    {
    	// TODO: Take a look at how Mekanism handles their block updates on pipes. Callbacks on their
        // own don't seem to work correctly; I think they might just be doing some calculations every
        // tick, similar to the below logic.
        PipeBlock pb = (PipeBlock)getBlockState().getBlock();
        pb.updateStateOnNeighborChange(level, getBlockState(), Direction.UP, worldPosition);
        pb.updateStateOnNeighborChange(level, getBlockState(), Direction.DOWN, worldPosition);
        pb.updateStateOnNeighborChange(level, getBlockState(), Direction.NORTH, worldPosition);
        pb.updateStateOnNeighborChange(level, getBlockState(), Direction.SOUTH, worldPosition);
        pb.updateStateOnNeighborChange(level, getBlockState(), Direction.WEST, worldPosition);
        pb.updateStateOnNeighborChange(level, getBlockState(), Direction.EAST, worldPosition);

        int numSpeedUpgrades = 0;
        int numStackUpgrades = 0;
        boolean hasUltimateStackUpgrade = false;
        boolean interdimensionalTeleport = false;
        ItemStack teleportItem = null;

        for (int i = 0; i < m_baseUpgradeInventory.getContainerSize(); ++i)
        {
            ItemStack stack = m_baseUpgradeInventory.getItem(i);
            int count = stack.getCount();
            if (count > 0)
            {
                UpgradeItem upgradeItem = (UpgradeItem) stack.getItem();
                switch (upgradeItem.getType())
                {
                case SpeedUpgrade:
                    numSpeedUpgrades += count;
                    break;
                case StackUpgrade:
                    numStackUpgrades += count;
                    break;
                case SpeedDowngrade:
                    numSpeedUpgrades -= count;
                    break;
                case UltimateStackUpgrade:
                    hasUltimateStackUpgrade = true;
                    break;
                case TeleportUpgrade:
                    teleportItem = stack;
                    break;
                case InterdimensionalUpgrade:
                    teleportItem = stack;
                    interdimensionalTeleport = true;
                    break;
                default:
                    break;
                }
            }
        }

        if (numSpeedUpgrades > 0)
            numSpeedUpgrades = Math.min(numSpeedUpgrades, maxEffectiveSpeedUpgrades());
        else if (numSpeedUpgrades < 0)
            numSpeedUpgrades = Math.max(numSpeedUpgrades, -maxEffectiveSpeedDowngrades());

        if (hasUltimateStackUpgrade)
            numStackUpgrades = maxEffectiveStackUpgrades();
        else if (numStackUpgrades > 0)
            numStackUpgrades = Math.min(numStackUpgrades, maxEffectiveSpeedUpgrades());

        int moveOnTick = 20;
        if (numSpeedUpgrades >= 19)
        {
            moveOnTick = 1;
        }
        else if (numSpeedUpgrades > 0)
        {
            moveOnTick -= numSpeedUpgrades;
        }
        else if (numSpeedUpgrades < 0)
        {
            moveOnTick += (20 * -numSpeedUpgrades);
        }

        m_tickCounter++;

        if (m_tickCounter >= moveOnTick)
        {
            m_tickCounter = 0;

            Direction[] dirs = Direction.values();

            // Pre-determine extract vs insert to avoid a nested loop below.
            ArrayList<U> extractHandlers = new ArrayList<U>(dirs.length);
            ArrayList<Direction> extractDirs = new ArrayList<Direction>(dirs.length);
            ArrayList<Byte> extractStates = new ArrayList<Byte>(dirs.length);
            ArrayList<PipeTileEntity<T, U, V>> insertPipes = new ArrayList<PipeTileEntity<T, U, V>>(dirs.length);
            ArrayList<U> insertHandlers = new ArrayList<U>(dirs.length);
            ArrayList<Direction> insertDirs = new ArrayList<Direction>(dirs.length);
            ArrayList<Byte> insertStates = new ArrayList<Byte>(dirs.length);

            boolean hasRedstone = level.hasNeighborSignal(worldPosition);
            for (int i = 0; i < dirs.length; ++i)
            {
                if (isRedstoneValid(getCache().redstoneControls[i], hasRedstone))
                {
                    ArrayList<U> handlers = new ArrayList<U>();
                    ArrayList<Byte> dirStates = new ArrayList<Byte>();
                    UpgradeInventory sideUpgradeInventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(dirs[i])];
                    BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(dirs[i]));

                    ItemStack sideConfigStack = ItemStack.EMPTY;
                    if (sideUpgradeInventory.getContainerSize() > 0)
                    {
	                    int sideConfigSlot = sideUpgradeInventory.getSlotForUpgrade(UpgradeType.SideUpgrade);
	                    if (sideConfigSlot >= 0)
	                        sideConfigStack = sideUpgradeInventory.getItem(sideConfigSlot);
                    }

                    if (!sideConfigStack.isEmpty())
                    {
                        SideUpgradeDataCache cache = new SideUpgradeDataCache(sideConfigStack);
                        byte[] states = cache.getStates();
                        if (states.length == dirs.length)
                        {
                            for (int j = 0; j < states.length; ++j)
                            {
                                if (states[j] != 0)
                                {
                                    U handler = getHandler(neighbor, dirs[j]);
                                    if (handler != null && getSize(handler) > 0)
                                    {
                                        handlers.add(handler);
                                        dirStates.add(states[j]);
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        U handler = getHandler(neighbor, dirs[i].getOpposite());
                        if (handler != null && getSize(handler) > 0)
                        {
                            handlers.add(handler);
                            dirStates.add((byte) Color.Rainbow.getIndex());
                        }
                    }

                    if (handlers.size() > 0)
                    {
                        if (getCache().transferDirections[i] == TransferDirection.Extract)
                        {
                            for (int j = 0; j < handlers.size(); ++j)
                            {
                                extractHandlers.add(handlers.get(j));
                                extractDirs.add(dirs[i]);
                                extractStates.add(dirStates.get(j));
                            }
                        }
                        else
                        {
                            for (int j = 0; j < handlers.size(); ++j)
                            {
                                insertPipes.add(this);
                                insertHandlers.add(handlers.get(j));
                                insertDirs.add(dirs[i]);
                                insertStates.add(dirStates.get(j));
                            }
                        }
                    }
                }
            }

            if (teleportItem != null)
            {
                CompoundTag channelNBT = teleportItem.getTagElement(TeleporterUpgradeItem.NBT_TAG);
                if (channelNBT != null)
                {
                    ChannelSpec spec = new ChannelSpec(channelNBT);
                    spec.tag = getChannelTag();
                    OwnerBasedChannelManager<ChannelData> manager = getChannelManager();
                    Set<ChannelAttachment> linkedEntities = manager.getAttached(spec);
                    for (ChannelAttachment target : linkedEntities)
                    {
                        // Only match with tile entities other than the current one.
                        if (target.pos.equals(worldPosition)
                                && target.world.dimension().compareTo(level.dimension()) == 0)
                            continue;

                        // If not interdimensional, world ID must match.
                        if (!interdimensionalTeleport && target.world.dimension().compareTo(level.dimension()) != 0)
                            continue;

                        PipeTileEntity<T, U, V> otherPipe = castToSelf(target.getTileEntity());
                        if (otherPipe != null)
                        {
                            boolean blockHasRedstone = target.isRedstonePowered();
                            for (int i = 0; i < dirs.length; ++i)
                            {
                                if (isRedstoneValid(otherPipe.getCache().redstoneControls[i], blockHasRedstone)
                                        && otherPipe.getCache().transferDirections[i] == TransferDirection.Insert)
                                {
                                    ArrayList<U> handlers = new ArrayList<U>();
                                    ArrayList<Byte> dirStates = new ArrayList<Byte>();
                                    UpgradeInventory sideUpgradeInventory = otherPipe.m_sideUpgradeInventories[SwiftUtils
                                            .dirToIndex(dirs[i])];
                                    BlockEntity otherPipeNeighbor = target.getNeighbor(dirs[i]);

                                    ItemStack sideConfigStack = ItemStack.EMPTY;
                                    if (sideUpgradeInventory.getContainerSize() > 0)
                                    {
	                                    int sideConfigSlot = sideUpgradeInventory
	                                            .getSlotForUpgrade(UpgradeType.SideUpgrade);
	                                    if (sideConfigSlot >= 0)
	                                        sideConfigStack = sideUpgradeInventory.getItem(sideConfigSlot);
                                    }

                                    if (!sideConfigStack.isEmpty())
                                    {
                                        SideUpgradeDataCache cache = new SideUpgradeDataCache(sideConfigStack);
                                        byte[] states = cache.getStates();
                                        if (states.length == dirs.length)
                                        {
                                            for (int j = 0; j < states.length; ++j)
                                            {
                                                if (states[j] != 0)
                                                {
                                                    U handler = getHandler(otherPipeNeighbor, dirs[j]);
                                                    if (handler != null && getSize(handler) > 0)
                                                    {
                                                        handlers.add(handler);
                                                        dirStates.add(states[j]);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    else
                                    {
                                        U handler = getHandler(otherPipeNeighbor, dirs[i].getOpposite());
                                        if (handler != null && getSize(handler) > 0)
                                        {
                                            handlers.add(handler);
                                            dirStates.add((byte) Color.Rainbow.getIndex());
                                        }
                                    }

                                    if (handlers.size() > 0)
                                    {
                                        for (int j = 0; j < handlers.size(); ++j)
                                        {
                                            insertPipes.add(otherPipe);
                                            insertHandlers.add(handlers.get(j));
                                            insertDirs.add(dirs[i]);
                                            insertStates.add(dirStates.get(j));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (extractHandlers.isEmpty() || insertHandlers.isEmpty())
                return;

            TransferQuantity quantity = getTransferQuantity(numStackUpgrades);

            transferItems(extractHandlers, extractDirs, extractStates, insertPipes, insertHandlers, insertDirs,
                    insertStates, quantity);
        }
    }

    protected abstract int maxEffectiveSpeedUpgrades();

    protected abstract int maxEffectiveStackUpgrades();

    protected abstract int maxEffectiveSpeedDowngrades();

    protected abstract boolean canAcceptTeleportUpgrade();

    protected class TransferQuantity
    {
        TransferQuantity()
        {
            moveStacks = false;
            quantity = 1;
        }

        TransferQuantity(boolean stacks, int amount)
        {
            moveStacks = stacks;
            quantity = amount;
        }

        boolean moveStacks;
        int quantity;
    };

    protected abstract TransferQuantity getTransferQuantity(int stacks);

    private void transferItems(ArrayList<U> extractHandlers, ArrayList<Direction> extractDirs,
            ArrayList<Byte> extractStates, ArrayList<PipeTileEntity<T, U, V>> insertPipes, ArrayList<U> insertHandlers,
            ArrayList<Direction> insertDirs, ArrayList<Byte> insertStates, TransferQuantity transferQuantity)
    {
        for (int i = 0; i < extractHandlers.size(); ++i)
        {
            U extractHandler = extractHandlers.get(i);
            Direction extractDirection = extractDirs.get(i);
            byte extractState = extractStates.get(i);
            if (getSize(extractHandler) == 0)
                continue;
            transferQuantity.quantity = transferItems(extractHandler, extractDirection, extractState, insertPipes,
                    insertHandlers, insertDirs, insertStates, transferQuantity.quantity, transferQuantity.moveStacks);
        }
    }

    private int transferItems(U extractHandler, Direction extractDir, byte extractState,
            ArrayList<PipeTileEntity<T, U, V>> insertPipes, ArrayList<U> insertHandlers,
            ArrayList<Direction> insertDirs, ArrayList<Byte> insertStates, int itemMoveCount, boolean moveStacks)
    {
        // Using a random starting slot a number of advantages:
        // 1) It minimizes the amount of time it takes (on average) to search an entire inventory for a
        // valid stack of items.
        // 2) It allows us to side-step some issues with the inability to transfer complete stacks of items
        // if, for example,
        // a single item enters the extracting inventory at a rate of once per tick.
        // So basically, it should be faster, and also shouldn't throttle as hard as slow-trickle inputs.
        int size = getSize(extractHandler);
        int startingSlot = ThreadLocalRandom.current().nextInt(0, size);

        // Randomize the inventory for receiving items.
        int insertInventoryIndex = ThreadLocalRandom.current().nextInt(0, insertHandlers.size());

        Filter<V> extractionFilter = m_filters[SwiftUtils.dirToIndex(extractDir)];

        for (int extractionSlot = startingSlot; extractionSlot < size; ++extractionSlot)
        {
            itemMoveCount = tryInsertItems(extractHandler, extractionSlot, extractionFilter, extractState, insertPipes,
                    insertHandlers, insertDirs, insertStates, insertInventoryIndex, itemMoveCount, moveStacks);

            if (itemMoveCount <= 0)
                return 0;
        }

        for (int extractionSlot = 0; extractionSlot < startingSlot; ++extractionSlot)
        {
            itemMoveCount = tryInsertItems(extractHandler, extractionSlot, extractionFilter, extractState, insertPipes,
                    insertHandlers, insertDirs, insertStates, insertInventoryIndex, itemMoveCount, moveStacks);

            if (itemMoveCount <= 0)
                return 0;
        }

        return itemMoveCount;
    }

    private int tryInsertItems(U extractHandler, int extractionSlot, Filter<V> extractionFilter, byte extractState,
            ArrayList<PipeTileEntity<T, U, V>> insertPipes, ArrayList<U> insertHandlers,
            ArrayList<Direction> insertDirs, ArrayList<Byte> insertStates, int insertInventoryIndex, int itemMoveCount,
            boolean moveStacks)
    {
        V stack = getStack(extractHandler, extractionSlot);
        if (isEmpty(stack))
            return itemMoveCount;

        if (extractionFilter != null)
        {
            FilterMatchResult<Filter<V>> matchResult = extractionFilter.filter(stack);
            if (!matchResult.matches)
                return itemMoveCount;
        }

        for (int j = insertInventoryIndex; j < insertHandlers.size(); ++j)
        {
            PipeTileEntity<T, U, V> insertPipe = insertPipes.get(j);
            U insertHandler = insertHandlers.get(j);
            Direction insertDir = insertDirs.get(j);
            byte insertState = insertStates.get(j);
            if (extractState == Color.Rainbow.getIndex() || insertState == Color.Rainbow.getIndex()
                    || extractState == insertState)
            {
                itemMoveCount = tryInsertItems(extractHandler, extractionSlot, insertPipe, insertHandler, insertDir,
                        stack, itemMoveCount, moveStacks);
            }
        }

        for (int j = 0; j < insertInventoryIndex; ++j)
        {
            PipeTileEntity<T, U, V> insertPipe = insertPipes.get(j);
            U insertHandler = insertHandlers.get(j);
            Direction insertDir = insertDirs.get(j);
            byte insertState = insertStates.get(j);
            if (extractState == Color.Rainbow.getIndex() || insertState == Color.Rainbow.getIndex()
                    || extractState == insertState)
            {
                itemMoveCount = tryInsertItems(extractHandler, extractionSlot, insertPipe, insertHandler, insertDir,
                        stack, itemMoveCount, moveStacks);
            }
        }

        return itemMoveCount;
    }

    private int tryInsertItems(U extractHandler, int extractionSlot, PipeTileEntity<T, U, V> insertPipe,
            U insertHandler, Direction insertDir, V stack, int itemMoveCount, boolean moveStacks)
    {
        Filter<V> insertionFilter = insertPipe.m_filters[SwiftUtils.dirToIndex(insertDir)];

        FilterMatchResult<Filter<V>> matchResult = new FilterMatchResult<Filter<V>>();
        if (insertionFilter != null)
        {
            // Passing true here reduces the filter so on a match only the matching filter
            // is returned. This allows for better handling of item-count matching when
            // ore dictionary is used.
            matchResult = insertionFilter.filter(stack, true);
            if (!matchResult.matches)
                return itemMoveCount;
        }

        int targetWhitelistCount = matchResult.getMatchCount();
        int currentItemCount = 0;
        if (targetWhitelistCount > 0)
        {
            for (int insertionSlot = 0; insertionSlot < getSize(insertHandler); ++insertionSlot)
            {
                V existingStack = getStack(insertHandler, insertionSlot);
                FilterMatchResult<Filter<V>> c = matchResult.filter.filter(existingStack);
                if (c.matches)
                    currentItemCount += getCount(existingStack);
            }

            if (currentItemCount >= targetWhitelistCount)
                return itemMoveCount;
        }

        for (int insertionSlot = 0; insertionSlot < getSize(insertHandler); ++insertionSlot)
        {
            // Simulate the insertion.
            int numItemsToInsert = simulateInsertion(insertHandler, insertionSlot, stack);
            if (targetWhitelistCount > 0)
                numItemsToInsert = Math.min(numItemsToInsert, targetWhitelistCount - currentItemCount);

            // Make sure that we could actually insert items into the stack. If we can't, then just
            // bail out. As a side-effect, we also can determine the number of items
            if (numItemsToInsert > 0)
            {
                if (moveStacks)
                {
                    // Transfer the item(s).
                    int extractCount = transfer(extractHandler, extractionSlot, insertHandler, insertionSlot, stack,
                            numItemsToInsert);
                    currentItemCount += extractCount;

                    --itemMoveCount;
                    if (itemMoveCount <= 0)
                        return 0;

                    // After insertion, bail out. Just go to the next stack. Trying to finish
                    // transferring this entire stack is prohibitively CPU-intensive.
                    break;
                }
                else
                {
                    int moveCount = Math.min(numItemsToInsert, itemMoveCount);

                    // Transfer the item(s).
                    int extractCount = transfer(extractHandler, extractionSlot, insertHandler, insertionSlot, stack,
                            moveCount);
                    currentItemCount += extractCount;

                    itemMoveCount -= extractCount;
                    if (itemMoveCount <= 0)
                        return 0;
                }
            }
        }

        return itemMoveCount;
    }

    private void onSideUpgradesChanged(Direction dir)
    {
        refreshFilter(dir);
    }

    private void onBaseUpgradesChanged(ContainerInventory cv)
    {
    	if (hasLevel() && level instanceof ServerLevel)
        {
            int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.TeleportUpgrade);
            if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
            {
                ItemStack stack = m_baseUpgradeInventory.getItem(slot);
                ChannelSpec spec = getChannel(stack);
                OwnerBasedChannelManager<ChannelData> channelManager = getChannelManager();
                if (spec != null)
                    channelManager.attach(spec, this);
                else
                    channelManager.detach(this);
            }

            slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.ChunkLoaderUpgrade);
            if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
            {
                ItemStack stack = m_baseUpgradeInventory.getItem(slot);
                ChunkPos chunk = new ChunkPos(worldPosition);
                ServerLevel serverWorld = (ServerLevel) level;
                boolean add = stack != null && !stack.isEmpty();
                ForgeChunkManager.forceChunk(serverWorld, Swift.MOD_NAME, worldPosition, chunk.x, chunk.z, add,
                        true);
            }
        }
    }

    protected void onChannelUpdate(ChannelSpec spec)
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
                    spec.tag = getChannelTag();
                    return spec;
                }
            }
        }

        return null;
    }

    private static boolean isRedstoneValid(RedstoneControl rc, boolean hasRedstone)
    {
        switch (rc)
        {
        case Ignore:
            return true;
        case Inverted:
            return !hasRedstone;
        case Normal:
            return hasRedstone;
        default:
            return false;
        }
    }

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

    protected abstract PipeTileEntity<T, U, V> castToSelf(BlockEntity entity);

    protected abstract void refreshFilter(Direction dir);

    protected abstract U getHandler(BlockEntity blockEntity, Direction dir);

    protected abstract int getSize(U handler);

    protected abstract int transfer(U extractHandler, int extractSlot, U insertHandler, int insertSlot, V stack,
            int numToTransfer);

    protected abstract int simulateInsertion(U insertHandler, int insertSlot, V stack);

    protected abstract V getStack(U handler, int slot);

    protected abstract int getCount(V stack);

    protected abstract boolean isEmpty(V stack);

    protected abstract OwnerBasedChannelManager<ChannelData> getChannelManager();
    
    protected abstract int getChannelTag();

    protected UpgradeInventory m_baseUpgradeInventory;
    protected UpgradeInventory[] m_sideUpgradeInventories;
    protected int m_tickCounter;
    protected Filter<V>[] m_filters;
}
