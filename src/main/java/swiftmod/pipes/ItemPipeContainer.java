package swiftmod.pipes;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import swiftmod.common.BigItemStack;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftNetwork;
import swiftmod.common.SwiftUtils;
import swiftmod.common.channels.BaseChannelManager;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.common.client.ChannelConfigurationPacket;
import swiftmod.common.client.ClearFilterPacket;
import swiftmod.common.client.ItemFilterConfigurationPacket;
import swiftmod.common.client.ItemFilterSlotPacket;
import swiftmod.common.upgrades.BasicItemFilterUpgradeDataCache;
import swiftmod.common.upgrades.ChannelConfigurationDataCache;
import swiftmod.common.upgrades.TeleporterUpgradeItem;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeType;

public class ItemPipeContainer extends PipeContainer implements ItemFilterConfigurationPacket.Handler,
        ItemFilterSlotPacket.Handler, ClearFilterPacket.Handler, ChannelConfigurationPacket.Handler
{
    protected ItemPipeContainer(@Nullable MenuType<?> type, int windowID, Inventory playerInventory,
            FriendlyByteBuf extraData, Supplier<UpgradeInventory> upgradeInventorySupplier,
            Supplier<UpgradeInventory> sideUpgradeInventorySupplier, int x, int y)
    {
        super(type, windowID, playerInventory, extraData, upgradeInventorySupplier, sideUpgradeInventorySupplier, x, y);
    }

    protected ItemPipeContainer(@Nullable MenuType<?> type, BlockEntity blockEntity, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories, int x, int y)
    {
        super(type, blockEntity, windowID, playerInventory, cache, refreshFilterCallback, channelManagerCallback,
                upgradeInventory, sideUpgradeInventories, x, y);
    }

    public BasicItemFilterUpgradeDataCache getBasicFilterCache(Direction direction)
    {
        BasicItemFilterUpgradeDataCache cache = new BasicItemFilterUpgradeDataCache();
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(direction)];
        int slot = getFilterUpgradeSlot(inventory);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem.get())
            {
                cache.itemStack = itemStack;
            }
        }
        return cache;
    }
    
    @Override
    public int getFilterUpgradeSlot(UpgradeInventory inventory)
    {
    	return inventory.getSlotForUpgrade(UpgradeType.BasicItemFilterUpgrade);
    }

    public void updateFilter(Direction direction, int slot, ItemStack itemStack, int quantity)
    {
        ItemFilterSlotPacket updatePacket = new ItemFilterSlotPacket();
        updatePacket.direction = direction;
        updatePacket.slot = slot;
        updatePacket.itemStack = new BigItemStack(itemStack, quantity);
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void clearAllFilters(Direction direction)
    {
        ClearFilterPacket updatePacket = new ClearFilterPacket();
        updatePacket.direction = direction;
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    public void sendUpdatePacketToServer(ItemFilterConfigurationPacket updatePacket)
    {
        SwiftNetwork.mainChannel.sendToServer(updatePacket);
    }

    @Override
    public void handle(ServerPlayer player, ItemFilterConfigurationPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(packet.direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicItemFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem.get())
            {
                BasicItemFilterUpgradeDataCache.setWhiteListState(packet.whiteListState, itemStack);
                BasicItemFilterUpgradeDataCache.setMatchCount(packet.matchCount, itemStack);
                BasicItemFilterUpgradeDataCache.setMatchDamage(packet.matchDamage, itemStack);
                BasicItemFilterUpgradeDataCache.setMatchMod(packet.matchMod, itemStack);
                BasicItemFilterUpgradeDataCache.setMatchNBT(packet.matchNBT, itemStack);
                BasicItemFilterUpgradeDataCache.setMatchOreDictionary(packet.matchOreDictionary, itemStack);

                refreshFilter(packet.direction);
            }
        }
    }

    @Override
    public void handle(ServerPlayer player, ItemFilterSlotPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(packet.direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicItemFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem.get())
            {
                BasicItemFilterUpgradeDataCache.setFilterSlot(packet.slot, packet.itemStack, itemStack);

                refreshFilter(packet.direction);
            }
        }
    }

    @Override
    public void handle(ServerPlayer player, ClearFilterPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(packet.direction)];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicItemFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem.get())
            {
                BasicItemFilterUpgradeDataCache.clearAllFilters(itemStack);

                refreshFilter(packet.direction);
            }
        }
    }

    @Override
    public void handle(ServerPlayer player, ChannelConfigurationPacket packet)
    {
        int slot = m_baseUpgradeInventory.getSlotForUpgrade(UpgradeType.TeleportUpgrade);
        if (slot >= 0 && slot < m_baseUpgradeInventory.getContainerSize())
        {
            ItemStack itemStack = m_baseUpgradeInventory.getItem(slot);
            if (itemStack.getItem() instanceof TeleporterUpgradeItem)
            {
                packet.channel.spec.tag = ChannelSpec.TAG_ITEMS;
                switch (packet.type)
                {
                case Add:
                    {
                        BaseChannelManager manager = BaseChannelManager.getManager();
                        manager.put(packet.channel);
                        manager.save();
                    }
                    break;
                case Delete:
                    {
                        BaseChannelManager manager = BaseChannelManager.getManager();
                        manager.delete(packet.channel.spec);
                        manager.save();
                    }
                    break;
                case Set:
                    ChannelConfigurationDataCache.setChannel(itemStack, packet.channel.spec);
                    m_channelManagerCallback.manage(packet.channel.spec);
                    break;
                case Unset:
                    ChannelConfigurationDataCache.clearChannel(itemStack);
                    m_channelManagerCallback.manage(null);
                    break;
                }
            }
        }
    }
}
