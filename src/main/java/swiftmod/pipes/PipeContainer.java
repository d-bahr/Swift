package swiftmod.pipes;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import swiftmod.common.ContainerBase;
import swiftmod.common.NeighboringItem;
import swiftmod.common.NeighboringItems;
import swiftmod.common.RedstoneControl;
import swiftmod.common.SlotBase;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftNetwork;
import swiftmod.common.SwiftUtils;
import swiftmod.common.TransferDirection;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.common.client.ClearFilterPacket;
import swiftmod.common.client.ChannelConfigurationPacket;
import swiftmod.common.client.RedstoneControlConfigurationPacket;
import swiftmod.common.client.SideConfigurationPacket;
import swiftmod.common.client.SlotConfigurationPacket;
import swiftmod.common.client.TransferDirectionConfigurationPacket;
import swiftmod.common.client.WildcardFilterPacket;
import swiftmod.common.upgrades.ChannelConfigurationDataCache;
import swiftmod.common.upgrades.SideUpgradeDataCache;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeItem;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.common.upgrades.WildcardFilterUpgradeDataCache;

public class PipeContainer extends ContainerBase<PipeDataCache>
        implements RedstoneControlConfigurationPacket.Handler, TransferDirectionConfigurationPacket.Handler,
        SlotConfigurationPacket.Handler, SideConfigurationPacket.Handler, WildcardFilterPacket.Handler
{
    @FunctionalInterface
    public interface RefreshFilterCallback
    {
        void refreshFilter(Direction direction);
    };

    @FunctionalInterface
    public interface ChannelManagerCallback
    {
        void manage(ChannelSpec spec);
    };

    protected PipeContainer(@Nullable ContainerType<?> type, int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData, Supplier<UpgradeInventory> upgradeInventorySupplier,
            Supplier<UpgradeInventory> sideUpgradeInventorySupplier, int x, int y)
    {
        super(type, windowID, new PipeDataCache(), playerInventory, x, y);

        m_refreshFilterCallback = null;
        m_channelManagerCallback = null;

        int len = Direction.values().length;

        m_itemStacks = new ItemStack[len];
        for (int i = 0; i < len; ++i)
            m_itemStacks[i] = null;

        NeighboringItems neighbors = m_cache.deserialize(extraData);
        ArrayList<NeighboringItem> items = neighbors.getItems();
        for (int i = 0; i < items.size(); ++i)
        {
            NeighboringItem item = items.get(i);
            if (SwiftUtils.dirToIndex(item.direction) < m_itemStacks.length)
                m_itemStacks[SwiftUtils.dirToIndex(item.direction)] = item.stack;
        }

        m_baseUpgradeInventory = upgradeInventorySupplier.get();

        m_sideUpgradeInventories = new UpgradeInventory[len];
        m_sideUpgradeInventoryStartingSlots = new int[len];
        for (int i = 0; i < len; ++i)
        {
            m_sideUpgradeInventories[i] = sideUpgradeInventorySupplier.get();
            m_sideUpgradeInventoryStartingSlots[i] = 0;
        }

        initUpgradeSlots();
        initSideUpgradeSlots();
    }

    protected PipeContainer(@Nullable ContainerType<?> type, TileEntity tileEntity, int windowID,
            PlayerInventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories, int x, int y)
    {
        super(type, windowID, cache, playerInventory, x, y);

        m_refreshFilterCallback = refreshFilterCallback;
        m_channelManagerCallback = channelManagerCallback;

        m_itemStacks = null;

        m_baseUpgradeInventory = upgradeInventory;
        m_sideUpgradeInventories = sideUpgradeInventories;
        m_sideUpgradeInventoryStartingSlots = new int[sideUpgradeInventories.length];

        initUpgradeSlots();
        initSideUpgradeSlots();
    }

    protected void initUpgradeSlots()
    {
        m_baseUpgradeInventoryStartingSlot = getNumSlots();
        SlotBase[] upgradeSlots = m_baseUpgradeInventory.createSlots(PipeContainerScreen.UPGRADE_PANEL_X + 1,
                PipeContainerScreen.UPGRADE_PANEL_Y + 1, 2, 2);
        addSlots(upgradeSlots);
    }

    protected void initSideUpgradeSlots()
    {
        int x = PipeContainerScreen.SIDE_UPGRADE_PANEL_X + PipeContainerScreen.SIDE_UPGRADE_PANEL_SLOT_START_X
                + 1;
        int y = PipeContainerScreen.SIDE_UPGRADE_PANEL_Y + PipeContainerScreen.SIDE_UPGRADE_PANEL_SLOT_START_Y
                + 1;
        int width = PipeContainerScreen.SIDE_UPGRADE_PANEL_SLOT_OFFSET_X;
        int height = PipeContainerScreen.SIDE_UPGRADE_PANEL_SLOT_OFFSET_Y;

        for (Direction dir : Direction.values())
        {
            int i = SwiftUtils.dirToIndex(dir);

            m_sideUpgradeInventoryStartingSlots[i] = getNumSlots();
            SlotBase[] upgradeSlots = m_sideUpgradeInventories[i].createSlots(x, y, 1,
                    m_sideUpgradeInventories[i].getContainerSize(), width, height);
            addSlots(upgradeSlots);
        }
    }

    public WildcardFilterUpgradeDataCache getWildcardFilterCache(Direction direction)
    {
        WildcardFilterUpgradeDataCache cache = new WildcardFilterUpgradeDataCache();
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.WildcardFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_wildcardFilterUpgradeItem)
            {
                cache.itemStack = itemStack;
            }
        }
        return cache;
    }

    public SideUpgradeDataCache getSideUpgradeCache(Direction direction)
    {
        SideUpgradeDataCache cache = new SideUpgradeDataCache();
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.SideUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_sideUpgradeItem)
            {
                cache.itemStack = itemStack;
            }
        }
        return cache;
    }

    public ChannelConfigurationDataCache getChannelConfigurationCache()
    {
        return getCache().channelConfiguration;
    }

    public ChannelSpec getCurrentChannelConfiguration()
    {
        int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.TeleportUpgrade);
        if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
        {
            ItemStack stack = m_baseUpgradeInventory.getItem(slot);
            return ChannelConfigurationDataCache.getChannel(stack);
        }
        else
        {
            return null;
        }
    }

    public void enableBaseUpgradeSlots(boolean enable)
    {
        int start = m_baseUpgradeInventoryStartingSlot;
        int end = start + m_baseUpgradeInventory.getContainerSize();
        ArrayList<Integer> updatedSlots = new ArrayList<Integer>(end - start);
        for (int i = start; i < end; ++i)
        {
            Slot s = slots.get(i);
            if (s instanceof SlotBase)
            {
                SlotBase sb = (SlotBase) s;
                sb.enable = enable;
                updatedSlots.add(i);
            }
        }
        sendSlotConfigurationUpdateToServer(updatedSlots, enable);
    }

    public void enableSideUpgradeSlots(Direction dir, boolean enable)
    {
        int start = m_sideUpgradeInventoryStartingSlots[SwiftUtils.dirToIndex(dir)];
        int end = start + m_sideUpgradeInventories[SwiftUtils.dirToIndex(dir)].getContainerSize();
        ArrayList<Integer> updatedSlots = new ArrayList<Integer>(end - start);
        for (int i = start; i < end; ++i)
        {
            Slot s = slots.get(i);
            if (s instanceof SlotBase)
            {
                SlotBase sb = (SlotBase) s;
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
            if (s instanceof SlotBase)
            {
                SlotBase sb = (SlotBase) s;
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

    public boolean containsUpgradeInSlot(Direction dir, UpgradeType type)
    {
        UpgradeInventory sideUpgradeInventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(dir)];
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

    public boolean containsUpgrade(Direction dir, UpgradeType type)
    {
        UpgradeInventory sideUpgradeInventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(dir)];
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

    public void setTransferDirection(Direction direction, TransferDirection transferDirection)
    {
        m_cache.setTransferDirection(direction, transferDirection);
        sendTransferDirectionUpdateToServer(direction, transferDirection);
    }

    public void setRedstoneControl(Direction direction, RedstoneControl redstoneControl)
    {
        m_cache.setRedstoneControl(direction, redstoneControl);
        sendRedstoneControlUpdateToServer(direction, redstoneControl);
    }

    public void sendTransferDirectionUpdateToServer(Direction direction, TransferDirection transferDirection)
    {
        TransferDirectionConfigurationPacket updatePacket = new TransferDirectionConfigurationPacket();
        updatePacket.direction = direction;
        updatePacket.transferDirection = transferDirection;
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void sendRedstoneControlUpdateToServer(Direction direction, RedstoneControl redstoneControl)
    {
        RedstoneControlConfigurationPacket updatePacket = new RedstoneControlConfigurationPacket();
        updatePacket.direction = direction;
        updatePacket.redstoneControl = redstoneControl;
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    private void sendSlotConfigurationUpdateToServer(List<Integer> slots, boolean enable)
    {
        SlotConfigurationPacket packet = new SlotConfigurationPacket(slots, enable);
        SwiftNetwork.mainChannel.sendToServer(packet);
    }

    public void clearAllFilters(Direction direction)
    {
        ClearFilterPacket updatePacket = new ClearFilterPacket();
        updatePacket.direction = direction;
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void addChannel(ChannelSpec spec)
    {
        getChannelConfigurationCache().addChannel(spec);
        sendUpdatePacket(spec, ChannelConfigurationPacket.Type.Add);
    }

    public void deleteChannel(ChannelSpec spec)
    {
        getChannelConfigurationCache().deleteChannel(spec);
        sendUpdatePacket(spec, ChannelConfigurationPacket.Type.Delete);
    }

    public void setChannel(ChannelSpec spec)
    {
        getChannelConfigurationCache().setChannel(spec);
        sendUpdatePacket(spec, ChannelConfigurationPacket.Type.Set);
    }

    public void unsetChannel()
    {
        getChannelConfigurationCache().clearChannel();
        sendUpdatePacket(new ChannelSpec(), ChannelConfigurationPacket.Type.Unset);
    }

    private void sendUpdatePacket(ChannelSpec spec, ChannelConfigurationPacket.Type type)
    {
        ChannelConfigurationPacket updatePacket = new ChannelConfigurationPacket(spec, type);
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void sendUpdatePacketToServer(SideConfigurationPacket packet)
    {
        SwiftNetwork.mainChannel.sendToServer(packet);
    }

    public void addFilter(Direction direction, String filter)
    {
        getWildcardFilterCache(direction).addFilter(filter);

        WildcardFilterPacket updatePacket = new WildcardFilterPacket();
        updatePacket.direction = direction;
        updatePacket.filter = filter;
        updatePacket.add = true;
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void removeFilter(Direction direction, String filter)
    {
        getWildcardFilterCache(direction).removeFilter(filter);

        WildcardFilterPacket updatePacket = new WildcardFilterPacket();
        updatePacket.direction = direction;
        updatePacket.filter = filter;
        updatePacket.add = false;
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    @Override
    public void handle(ServerPlayerEntity player, TransferDirectionConfigurationPacket packet)
    {
        m_cache.transferDirections[SwiftUtils.dirToIndex(packet.direction)] = packet.transferDirection;
    }

    @Override
    public void handle(ServerPlayerEntity player, RedstoneControlConfigurationPacket packet)
    {
        m_cache.redstoneControls[SwiftUtils.dirToIndex(packet.direction)] = packet.redstoneControl;
    }

    @Override
    public void handle(ServerPlayerEntity player, SlotConfigurationPacket packet)
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
    public void handle(ServerPlayerEntity player, WildcardFilterPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(packet.direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.WildcardFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_wildcardFilterUpgradeItem)
            {
                if (packet.add)
                    WildcardFilterUpgradeDataCache.addFilter(packet.filter, itemStack);
                else
                    WildcardFilterUpgradeDataCache.removeFilter(packet.filter, itemStack);

                if (m_refreshFilterCallback != null)
                    m_refreshFilterCallback.refreshFilter(packet.direction);
            }
        }
    }

    @Override
    public void handle(ServerPlayerEntity player, SideConfigurationPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(packet.direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.SideUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_sideUpgradeItem)
            {
                SideUpgradeDataCache.setStates(packet.directionStates, itemStack);
            }
        }
    }

    public ItemStack getNeighbor(Direction dir)
    {
        if (m_itemStacks == null)
            return null;
        if (SwiftUtils.dirToIndex(dir) >= m_itemStacks.length)
            return null;
        return m_itemStacks[SwiftUtils.dirToIndex(dir)];
    }
    
    protected void refreshFilter(Direction dir)
    {
        if (m_refreshFilterCallback != null)
            m_refreshFilterCallback.refreshFilter(dir);
    }

    protected RefreshFilterCallback m_refreshFilterCallback;
    protected ChannelManagerCallback m_channelManagerCallback;

    protected ItemStack[] m_itemStacks;

    protected UpgradeInventory m_baseUpgradeInventory;
    protected UpgradeInventory[] m_sideUpgradeInventories;
    protected int m_baseUpgradeInventoryStartingSlot;
    protected int[] m_sideUpgradeInventoryStartingSlots;
}
