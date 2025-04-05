package swiftmod.pipes;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import swiftmod.common.BigItemStack;
import swiftmod.common.SlotBase;
import swiftmod.common.SwiftItems;
import swiftmod.common.WhiteListState;
import swiftmod.common.client.ItemClearFilterPacket;
import swiftmod.common.client.ItemFilterConfigurationPacket;
import swiftmod.common.client.ItemFilterSlotPacket;
import swiftmod.common.client.ItemWildcardFilterPacket;
import swiftmod.common.gui.SideIOConfigurationWidget;
import swiftmod.common.upgrades.BasicItemFilterUpgradeDataCache;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.common.upgrades.WildcardFilterUpgradeDataCache;
import swiftmod.pipes.PipeTileEntity.SideUpgradeInventoryBuilder;

public class ItemPipeContainer extends PipeContainer implements ItemFilterConfigurationPacket.Handler,
        ItemFilterSlotPacket.Handler, ItemClearFilterPacket.Handler, ItemWildcardFilterPacket.Handler
{
    protected ItemPipeContainer(@Nullable MenuType<?> type, int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData, Supplier<UpgradeInventory> upgradeInventorySupplier,
    		int numSideUpgradeInventories, SideUpgradeInventoryBuilder sideUpgradeInventorySupplier, int x, int y)
    {
        super(type, windowID, playerInventory, extraData, upgradeInventorySupplier, numSideUpgradeInventories, sideUpgradeInventorySupplier, x, y);
        
        initSideUpgradeSlots();
    }

    protected ItemPipeContainer(@Nullable MenuType<?> type, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback, BlockPos pos,
            UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories, int x, int y)
    {
        super(type, windowID, playerInventory, cache, refreshFilterCallback, pos,
                upgradeInventory, sideUpgradeInventories, x, y);
        
        initSideUpgradeSlots();
    }

    protected void initSideUpgradeSlots()
    {
        int x = PipeContainerScreen.BASE_PANEL_X + SideIOConfigurationWidget.SPEED_UPGRADE_SLOT_X + 1;
        int y = PipeContainerScreen.BASE_PANEL_Y + SideIOConfigurationWidget.SPEED_UPGRADE_SLOT_Y + 1;

        for (int i = 0; i < m_sideUpgradeInventories.length; ++i)
        {
            m_sideUpgradeInventoryStartingSlots[i] = getNumSlots();
            SlotBase[] upgradeSlots = m_sideUpgradeInventories[i].createSlots(x, y, 2, 1);
            SlotBase filterUpgradeSlot = m_sideUpgradeInventories[i].createSlot(upgradeSlots.length,
            		PipeContainerScreen.BASE_PANEL_X + SideIOConfigurationWidget.FILTER_UPGRADE_SLOT_X + 1,
            		PipeContainerScreen.BASE_PANEL_Y + SideIOConfigurationWidget.FILTER_UPGRADE_SLOT_Y + 1);

        	int transferIndex = i;
            filterUpgradeSlot.setChangedCallback((slot) -> onFilterUpgradeSlotChanged(slot, transferIndex));

            addSlots(upgradeSlots);
            addSlot(filterUpgradeSlot);
        }
    }

    protected BasicItemFilterUpgradeDataCache getBasicFilterCache(int index)
    {
        BasicItemFilterUpgradeDataCache cache = new BasicItemFilterUpgradeDataCache();
        UpgradeInventory inventory = m_sideUpgradeInventories[index];
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
    
    protected void updateFilter(int index, int slot, ItemStack itemStack, int quantity)
    {
        ItemFilterSlotPacket updatePacket = new ItemFilterSlotPacket();
        updatePacket.index = index;
        updatePacket.slot = slot;
        updatePacket.itemStack = new BigItemStack(itemStack, quantity);
        PacketDistributor.sendToServer(updatePacket);
    }

    protected void clearAllFilters(int index)
    {
    	ItemClearFilterPacket updatePacket = new ItemClearFilterPacket();
        updatePacket.index = index;
        PacketDistributor.sendToServer(updatePacket);
    }

    protected void addWildcardFilter(int index, String filter)
    {
        getWildcardFilterCache(index).addFilter(filter);

        ItemWildcardFilterPacket updatePacket = new ItemWildcardFilterPacket();
        updatePacket.index = index;
        updatePacket.filter = filter;
        updatePacket.add = true;
        PacketDistributor.sendToServer(updatePacket);
    }

    protected void removeWildcardFilter(int index, String filter)
    {
        getWildcardFilterCache(index).removeFilter(filter);

        ItemWildcardFilterPacket updatePacket = new ItemWildcardFilterPacket();
        updatePacket.index = index;
        updatePacket.filter = filter;
        updatePacket.add = false;
        PacketDistributor.sendToServer(updatePacket);
    }

    protected void updateFilterConfiguration(int index, WhiteListState whitelist,
    		boolean matchCount, boolean matchDamage, boolean matchMod, boolean matchNBT, boolean matchOreDict)
    {
        ItemFilterConfigurationPacket updatePacket = new ItemFilterConfigurationPacket();
        updatePacket.index = index;
        updatePacket.whiteListState = whitelist;
        updatePacket.matchCount = matchCount;
        updatePacket.matchDamage = matchDamage;
        updatePacket.matchMod = matchMod;
        updatePacket.matchNBT = matchNBT;
        updatePacket.matchOreDictionary = matchOreDict;
        PacketDistributor.sendToServer(updatePacket);
    }

    @Override
    public void handle(ServerPlayer player, ItemFilterConfigurationPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[packet.index];
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

                refreshFilter(packet.index);
            }
        }
    }

    @Override
    public void handle(ServerPlayer player, ItemFilterSlotPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[packet.index];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicItemFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem.get())
            {
                BasicItemFilterUpgradeDataCache.setFilterSlot(packet.slot, packet.itemStack, itemStack);

                refreshFilter(packet.index);
            }
        }
    }

    @Override
    public void handle(ServerPlayer player, ItemClearFilterPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[packet.index];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicItemFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicItemFilterUpgradeItem.get())
            {
                BasicItemFilterUpgradeDataCache.clearAllFilters(itemStack);

                refreshFilter(packet.index);
            }
        }
    }

    @Override
    public void handle(ServerPlayer player, ItemWildcardFilterPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[packet.index];
        int slot = inventory.getSlotForUpgrade(UpgradeType.WildcardFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_wildcardFilterUpgradeItem.get())
            {
                if (packet.add)
                    WildcardFilterUpgradeDataCache.addFilter(packet.filter, itemStack);
                else
                    WildcardFilterUpgradeDataCache.removeFilter(packet.filter, itemStack);

                refreshFilter(packet.index);
            }
        }
    }
}
