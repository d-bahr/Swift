package swiftmod.pipes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import swiftmod.common.Color;
import swiftmod.common.ContainerBase;
import swiftmod.common.EnableableSlot;
import swiftmod.common.NeighboringItem;
import swiftmod.common.NeighboringItems;
import swiftmod.common.RedstoneControl;
import swiftmod.common.SlotBase;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftUtils;
import swiftmod.common.TransferDirection;
import swiftmod.common.client.ColorConfigurationPacket;
import swiftmod.common.client.PriorityConfigurationPacket;
import swiftmod.common.client.RedstoneControlConfigurationPacket;
import swiftmod.common.client.SlotConfigurationPacket;
import swiftmod.common.client.TransferDirectionConfigurationPacket;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeItem;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.common.upgrades.WildcardFilterUpgradeDataCache;
import swiftmod.pipes.PipeTileEntity.SideUpgradeInventoryBuilder;

public abstract class PipeContainer extends ContainerBase<PipeDataCache>
        implements RedstoneControlConfigurationPacket.Handler, TransferDirectionConfigurationPacket.Handler,
        ColorConfigurationPacket.Handler, SlotConfigurationPacket.Handler, PriorityConfigurationPacket.Handler
{
    @FunctionalInterface
    public interface RefreshFilterCallback
    {
        void refreshFilter(int transferIndex);
    };

    @FunctionalInterface
    public interface BaseSlotChangedCallback
    {
        void onChanged(SlotBase slot);
    };

    @FunctionalInterface
    public interface DirectionalSlotChangedCallback
    {
        void onChanged(SlotBase slot, int transferIndex);
    };

    protected PipeContainer(@Nullable MenuType<?> type, int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData, Supplier<UpgradeInventory> upgradeInventorySupplier,
            SideUpgradeInventoryBuilder sideUpgradeInventorySupplier, int x, int y)
    {
    	this(type, windowID, playerInventory, extraData, upgradeInventorySupplier, Direction.values().length,
    		sideUpgradeInventorySupplier, x, y);
    }
    
    protected PipeContainer(@Nullable MenuType<?> type, int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData, Supplier<UpgradeInventory> upgradeInventorySupplier,
            int numSideUpgradeInventories, SideUpgradeInventoryBuilder sideUpgradeInventorySupplier,
            int x, int y)
    {
        super(type, windowID, new PipeDataCache(), playerInventory, x, y);

        m_refreshFilterCallback = null;
        m_filterSlotChangedCallback = null;

        int len = Direction.values().length;

        m_itemStacks = new ItemStack[len];
        m_facing = new Direction[len];
        for (int i = 0; i < len; ++i)
        {
            m_itemStacks[i] = null;
            m_facing[i] = Direction.NORTH;
        }

        NeighboringItems neighbors = m_cache.deserialize(extraData);
        m_startingDirection = neighbors.getStartingDirection();
        m_startingPipeType = neighbors.getStartingPipeType();
        ArrayList<NeighboringItem> items = neighbors.getItems();
        for (int i = 0; i < items.size(); ++i)
        {
            NeighboringItem item = items.get(i);
            if (SwiftUtils.dirToIndex(item.direction) < m_itemStacks.length)
            {
                m_itemStacks[SwiftUtils.dirToIndex(item.direction)] = item.stack;
            	m_facing[SwiftUtils.dirToIndex(item.direction)] = item.facing;
            }
        }

        m_baseUpgradeInventory = upgradeInventorySupplier.get();

        m_sideUpgradeInventories = new UpgradeInventory[numSideUpgradeInventories];
        m_sideUpgradeInventoryStartingSlots = new int[numSideUpgradeInventories];
        for (int i = 0; i < numSideUpgradeInventories; ++i)
        {
            m_sideUpgradeInventories[i] = sideUpgradeInventorySupplier.build(i);
            m_sideUpgradeInventoryStartingSlots[i] = 0;
        }

        m_pos = BlockPos.STREAM_CODEC.decode(extraData);

        initUpgradeSlots();
    }

    protected PipeContainer(@Nullable MenuType<?> type, int windowID,
            Inventory playerInventory, PipeDataCache cache, BlockPos pos,
            UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories, int x, int y)
    {
        this(type, windowID, playerInventory, cache, null, pos, upgradeInventory, sideUpgradeInventories, x, y);
    }

    protected PipeContainer(@Nullable MenuType<?> type, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            BlockPos pos, UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories, int x, int y)
    {
        super(type, windowID, cache, playerInventory, x, y);

        m_refreshFilterCallback = refreshFilterCallback;
        m_sideSlotChangedCallback = null;
        m_filterSlotChangedCallback = null;

        m_itemStacks = null;
        m_startingDirection = null;
        m_startingPipeType = null;

        m_baseUpgradeInventory = upgradeInventory;
        m_sideUpgradeInventories = sideUpgradeInventories;
        m_sideUpgradeInventoryStartingSlots = new int[sideUpgradeInventories.length];
        
        m_pos = pos;

        initUpgradeSlots();
    }

    protected void initUpgradeSlots()
    {
        m_baseUpgradeInventoryStartingSlot = getNumSlots();
        SlotBase[] upgradeSlots = m_baseUpgradeInventory.createSlots(PipeContainerScreen.CHUNK_LOADER_UPGRADE_SLOT_X + 1,
                PipeContainerScreen.CHUNK_LOADER_UPGRADE_SLOT_Y + 1, 1, 1);
        addSlots(upgradeSlots);
    }

    public void setFilterSlotChangedCallback(DirectionalSlotChangedCallback callback)
    {
        m_filterSlotChangedCallback = callback;
    }
    
    protected int getFilterUpgradeSlot(UpgradeInventory inventory)
    {
        return inventory.getSlotForUpgrade(UpgradeType.WildcardFilterUpgrade);
    }

    protected WildcardFilterUpgradeDataCache getWildcardFilterCache(int transferIndex)
    {
        WildcardFilterUpgradeDataCache cache = new WildcardFilterUpgradeDataCache();
        UpgradeInventory inventory = m_sideUpgradeInventories[transferIndex];
        int slot = inventory.getSlotForUpgrade(UpgradeType.WildcardFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_wildcardFilterUpgradeItem.get())
            {
                cache.itemStack = itemStack;
            }
        }
        return cache;
    }

    public void enableBaseUpgradeSlots(boolean enable)
    {
        int start = m_baseUpgradeInventoryStartingSlot;
        int end = start + m_baseUpgradeInventory.getContainerSize();
        ArrayList<Integer> updatedSlots = new ArrayList<Integer>(end - start);
        for (int i = start; i < end; ++i)
        {
            Slot s = slots.get(i);
            if (s instanceof EnableableSlot)
            {
            	EnableableSlot sb = (EnableableSlot) s;
                sb.enable = enable;
                updatedSlots.add(i);
            }
        }
        sendSlotConfigurationUpdateToServer(updatedSlots, enable);
    }

    public void enableSideUpgradeSlots(int transferIndex, boolean enable)
    {
        int start = m_sideUpgradeInventoryStartingSlots[transferIndex];
        int end = start + m_sideUpgradeInventories[transferIndex].getContainerSize();
        ArrayList<Integer> updatedSlots = new ArrayList<Integer>(end - start);
        for (int i = start; i < end; ++i)
        {
            Slot s = slots.get(i);
            if (s instanceof EnableableSlot)
            {
            	EnableableSlot sb = (EnableableSlot) s;
                sb.enable = enable;
                updatedSlots.add(i);
            }
        }
        sendSlotConfigurationUpdateToServer(updatedSlots, enable);
    }

    public void disableSideUpgradeSlots()
    {
        int start = m_sideUpgradeInventoryStartingSlots[0];
        int lastIndex = m_sideUpgradeInventoryStartingSlots.length - 1;
        int end = m_sideUpgradeInventoryStartingSlots[lastIndex]
                + m_sideUpgradeInventories[lastIndex].getContainerSize();
        ArrayList<Integer> updatedSlots = new ArrayList<Integer>(end - start);
        for (int i = start; i < end; ++i)
        {
            Slot s = slots.get(i);
            if (s instanceof EnableableSlot)
            {
            	EnableableSlot sb = (EnableableSlot) s;
                sb.enable = false;
                updatedSlots.add(i);
            }
        }
        sendSlotConfigurationUpdateToServer(updatedSlots, false);
    }

    public boolean containsUpgradeInSlot(UpgradeType type)
    {
        int slot = m_baseUpgradeInventory.getSlotForUpgrade(type);
        if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
        {
            return !m_baseUpgradeInventory.getItem(slot).isEmpty();
        }
        else
        {
            return false;
        }
    }

    public boolean containsUpgrade(UpgradeType type)
    {
        int slot = m_baseUpgradeInventory.getSlotForUpgrade(type);
        if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
        {
            ItemStack stack = m_baseUpgradeInventory.getItem(slot);
            if (stack.isEmpty())
                return false;
            if (stack.getItem() instanceof UpgradeItem)
                return ((UpgradeItem) stack.getItem()).getType() == type;
            else
                return false;
        }
        else
        {
            return false;
        }
    }

    public boolean containsUpgradeInSlot(int index, UpgradeType type)
    {
        UpgradeInventory sideUpgradeInventory = m_sideUpgradeInventories[index];
        int slot = sideUpgradeInventory.getSlotForUpgrade(type);
        if (slot >= 0 && slot < sideUpgradeInventory.getContainerSize())
        {
            return !sideUpgradeInventory.getItem(slot).isEmpty();
        }
        else
        {
            return false;
        }
    }

    public boolean containsUpgrade(int index, UpgradeType type)
    {
        UpgradeInventory sideUpgradeInventory = m_sideUpgradeInventories[index];
        int slot = sideUpgradeInventory.getSlotForUpgrade(type);
        if (slot >= 0 && slot < sideUpgradeInventory.getContainerSize())
        {
            ItemStack stack = sideUpgradeInventory.getItem(slot);
            if (stack.isEmpty())
                return false;
            if (stack.getItem() instanceof UpgradeItem)
                return ((UpgradeItem) stack.getItem()).getType() == type;
            else
                return false;
        }
        else
        {
            return false;
        }
    }

    public void setTransferDirection(int transferIndex, TransferDirection transferDirection)
    {
        m_cache.setTransferDirection(transferIndex, transferDirection);
        sendTransferDirectionUpdateToServer(transferIndex, transferDirection);
    }

    public void setRedstoneControl(int transferIndex, RedstoneControl redstoneControl)
    {
        m_cache.setRedstoneControl(transferIndex, redstoneControl);
        sendRedstoneControlUpdateToServer(transferIndex, redstoneControl);
    }

    public void setColor(int transferIndex, Color color)
    {
        m_cache.setColor(transferIndex, color);
        sendColorUpdateToServer(transferIndex, color);
    }
    
    public void setPriority(int transferIndex, int priority)
    {
    	m_cache.setPriority(transferIndex, priority);
        sendPriorityUpdateToServer(transferIndex, priority);
    }
    
    public int getPriority(int transferIndex)
    {
    	return m_cache.getPriority(transferIndex);
    }

    public void sendTransferDirectionUpdateToServer(int transferIndex, TransferDirection transferDirection)
    {
        TransferDirectionConfigurationPacket updatePacket = new TransferDirectionConfigurationPacket();
        updatePacket.index = transferIndex;
        updatePacket.transferDirection = transferDirection;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void sendRedstoneControlUpdateToServer(int transferIndex, RedstoneControl redstoneControl)
    {
        RedstoneControlConfigurationPacket updatePacket = new RedstoneControlConfigurationPacket();
        updatePacket.index = transferIndex;
        updatePacket.redstoneControl = redstoneControl;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void sendColorUpdateToServer(int transferIndex, Color color)
    {
    	ColorConfigurationPacket updatePacket = new ColorConfigurationPacket();
        updatePacket.index = transferIndex;
        updatePacket.color = color;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void sendPriorityUpdateToServer(int transferIndex, int priority)
    {
    	PriorityConfigurationPacket updatePacket = new PriorityConfigurationPacket();
        updatePacket.index = transferIndex;
        updatePacket.priority = priority;
        PacketDistributor.sendToServer(updatePacket);
    }

    private void sendSlotConfigurationUpdateToServer(List<Integer> slots, boolean enable)
    {
        SlotConfigurationPacket packet = new SlotConfigurationPacket(slots, enable);
        PacketDistributor.sendToServer(packet);
    }

    @Override
    public void handle(ServerPlayer player, TransferDirectionConfigurationPacket packet)
    {
    	m_cache.setTransferDirection(packet.index, packet.transferDirection);
    }

    @Override
    public void handle(ServerPlayer player, RedstoneControlConfigurationPacket packet)
    {
    	m_cache.setRedstoneControl(packet.index, packet.redstoneControl);
    }

    @Override
    public void handle(ServerPlayer player, ColorConfigurationPacket packet)
    {
    	m_cache.setColor(packet.index, packet.color);
    }

    @Override
    public void handle(ServerPlayer player, SlotConfigurationPacket packet)
    {
        for (int i = 0; i < packet.slots.size(); ++i)
        {
            if (packet.slots.get(i) < slots.size())
            {
                Slot slot = slots.get(packet.slots.get(i));
                if (slot instanceof SlotBase)
                    ((SlotBase) slot).enable = packet.enable;
            }
        }
    }
    
    @Override
    public void handle(ServerPlayer player, PriorityConfigurationPacket packet)
    {
    	m_cache.setPriority(packet.index, packet.priority);
    }

    public void onFilterUpgradeSlotChanged(SlotBase slot, int transferIndex)
    {
        if (m_filterSlotChangedCallback != null)
            m_filterSlotChangedCallback.onChanged(slot, transferIndex);
    }

    public ItemStack getNeighbor(Direction dir)
    {
        if (m_itemStacks == null)
            return null;
        if (SwiftUtils.dirToIndex(dir) >= m_itemStacks.length)
            return null;
        return m_itemStacks[SwiftUtils.dirToIndex(dir)];
    }

    public Direction getNeighborFacing(Direction dir)
    {
        if (m_facing == null)
            return null;
        if (SwiftUtils.dirToIndex(dir) >= m_facing.length)
            return null;
        Direction facing = m_facing[SwiftUtils.dirToIndex(dir)];
        return facing != null ? facing : Direction.NORTH;
    }
    
    public Direction getStartingDirection()
    {
        return m_startingDirection;
    }
    
    public PipeType getStartingPipeType()
    {
        return m_startingPipeType;
    }
    
    protected void refreshFilter(int transferIndex)
    {
        if (m_refreshFilterCallback != null)
            m_refreshFilterCallback.refreshFilter(transferIndex);
    }

    protected RefreshFilterCallback m_refreshFilterCallback;
    protected DirectionalSlotChangedCallback m_sideSlotChangedCallback;
    protected DirectionalSlotChangedCallback m_filterSlotChangedCallback;

    protected ItemStack[] m_itemStacks;
    protected Direction[] m_facing;
    protected Direction m_startingDirection;
    protected PipeType m_startingPipeType;

    protected UpgradeInventory m_baseUpgradeInventory;
    protected UpgradeInventory[] m_sideUpgradeInventories;
    protected int m_baseUpgradeInventoryStartingSlot;
    protected int[] m_sideUpgradeInventoryStartingSlots;
    
    protected final BlockPos m_pos;
}
