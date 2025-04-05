package swiftmod.pipes;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import swiftmod.common.BigItemStack;
import swiftmod.common.SlotBase;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftUtils;
import swiftmod.common.WhiteListState;
import swiftmod.common.client.FluidClearFilterPacket;
import swiftmod.common.client.FluidFilterConfigurationPacket;
import swiftmod.common.client.FluidFilterSlotPacket;
import swiftmod.common.client.FluidWildcardFilterPacket;
import swiftmod.common.client.ItemClearFilterPacket;
import swiftmod.common.client.ItemFilterConfigurationPacket;
import swiftmod.common.client.ItemFilterSlotPacket;
import swiftmod.common.client.ItemWildcardFilterPacket;
import swiftmod.common.gui.SideIOConfigurationWidget;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeDataCache;
import swiftmod.common.upgrades.BasicItemFilterUpgradeDataCache;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.common.upgrades.WildcardFilterUpgradeDataCache;
import swiftmod.pipes.PipeTileEntity.SideUpgradeInventoryBuilder;

public class OmniPipeContainer extends PipeContainer implements ItemFilterConfigurationPacket.Handler,
	ItemFilterSlotPacket.Handler, ItemClearFilterPacket.Handler, ItemWildcardFilterPacket.Handler,
	FluidFilterConfigurationPacket.Handler,	FluidFilterSlotPacket.Handler, FluidClearFilterPacket.Handler,
	FluidWildcardFilterPacket.Handler
{
    protected OmniPipeContainer(@Nullable MenuType<?> type, int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData, Supplier<UpgradeInventory> upgradeInventorySupplier,
    		int numSideUpgradeInventories, SideUpgradeInventoryBuilder sideUpgradeInventorySupplier, int x, int y)
    {
        super(type, windowID, playerInventory, extraData, upgradeInventorySupplier,
        		numSideUpgradeInventories, sideUpgradeInventorySupplier, x, y);
        
        initSideUpgradeSlots();
    }

    protected OmniPipeContainer(@Nullable MenuType<?> type, int windowID,
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

        int numSideInventories = Direction.values().length * PipeType.numTypes();
        for (int i = 0; i < numSideInventories; ++i)
        {
            m_sideUpgradeInventoryStartingSlots[i] = getNumSlots();
            SlotBase[] upgradeSlots = m_sideUpgradeInventories[i].createSlots(x, y, 2, 1);
            addSlots(upgradeSlots);
            
            if (i < OmniPipeTileEntity.MIN_ENERGY_INDEX)
            {
	            SlotBase filterUpgradeSlot = m_sideUpgradeInventories[i].createSlot(upgradeSlots.length,
	            		PipeContainerScreen.BASE_PANEL_X + SideIOConfigurationWidget.FILTER_UPGRADE_SLOT_X + 1,
	            		PipeContainerScreen.BASE_PANEL_Y + SideIOConfigurationWidget.FILTER_UPGRADE_SLOT_Y + 1);
	
	        	int transferIndex = i;
	            filterUpgradeSlot.setChangedCallback((slot) -> onFilterUpgradeSlotChanged(slot, transferIndex));
	            addSlot(filterUpgradeSlot);
            }
        }
    }

    public BasicItemFilterUpgradeDataCache getBasicItemFilterCache(Direction direction)
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

    public BasicFluidFilterUpgradeDataCache getBasicFluidFilterCache(Direction direction)
    {
        BasicFluidFilterUpgradeDataCache cache = new BasicFluidFilterUpgradeDataCache();
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_FLUID_INDEX];
        int slot = getFilterUpgradeSlot(inventory);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem.get())
            {
                cache.itemStack = itemStack;
            }
        }
        return cache;
    }

    public WildcardFilterUpgradeDataCache getItemWildcardFilterCache(Direction direction)
    {
		return getWildcardFilterCache(SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_ITEM_INDEX);
    }

    public WildcardFilterUpgradeDataCache getFluidWildcardFilterCache(Direction direction)
    {
		return getWildcardFilterCache(SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_FLUID_INDEX);
    }

    public void updateItemFilter(Direction direction, int slot, ItemStack itemStack, int quantity)
    {
    	ItemFilterSlotPacket updatePacket = new ItemFilterSlotPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_ITEM_INDEX;
        updatePacket.slot = slot;
        updatePacket.itemStack = new BigItemStack(itemStack, quantity);
        PacketDistributor.sendToServer(updatePacket);
    }

    public void clearAllItemFilters(Direction direction)
    {
    	ItemClearFilterPacket updatePacket = new ItemClearFilterPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_ITEM_INDEX;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void addItemWildcardFilter(Direction direction, String filter)
    {
        getItemWildcardFilterCache(direction).addFilter(filter);

        ItemWildcardFilterPacket updatePacket = new ItemWildcardFilterPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_ITEM_INDEX;
        updatePacket.filter = filter;
        updatePacket.add = true;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void removeItemWildcardFilter(Direction direction, String filter)
    {
        getItemWildcardFilterCache(direction).removeFilter(filter);

        ItemWildcardFilterPacket updatePacket = new ItemWildcardFilterPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_ITEM_INDEX;
        updatePacket.filter = filter;
        updatePacket.add = false;
        PacketDistributor.sendToServer(updatePacket);
    }

    protected void updateItemFilterConfiguration(Direction direction, WhiteListState whitelist,
    		boolean matchCount, boolean matchDamage, boolean matchMod, boolean matchNBT, boolean matchOreDict)
    {
        ItemFilterConfigurationPacket updatePacket = new ItemFilterConfigurationPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_ITEM_INDEX;
        updatePacket.whiteListState = whitelist;
        updatePacket.matchCount = matchCount;
        updatePacket.matchDamage = matchDamage;
        updatePacket.matchMod = matchMod;
        updatePacket.matchNBT = matchNBT;
        updatePacket.matchOreDictionary = matchOreDict;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void updateFluidFilter(Direction direction, int slot, FluidStack fluidStack)
    {
        FluidFilterSlotPacket updatePacket = new FluidFilterSlotPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_FLUID_INDEX;
        updatePacket.slot = slot;
        updatePacket.fluidStack = fluidStack;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void clearAllFluidFilters(Direction direction)
    {
    	FluidClearFilterPacket updatePacket = new FluidClearFilterPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_FLUID_INDEX;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void addFluidWildcardFilter(Direction direction, String filter)
    {
        getFluidWildcardFilterCache(direction).addFilter(filter);

        FluidWildcardFilterPacket updatePacket = new FluidWildcardFilterPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_FLUID_INDEX;
        updatePacket.filter = filter;
        updatePacket.add = true;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void removeFluidWildcardFilter(Direction direction, String filter)
    {
        getFluidWildcardFilterCache(direction).removeFilter(filter);

        FluidWildcardFilterPacket updatePacket = new FluidWildcardFilterPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_FLUID_INDEX;
        updatePacket.filter = filter;
        updatePacket.add = false;
        PacketDistributor.sendToServer(updatePacket);
    }
    
    public void updateFluidFilterConfiguration(Direction direction, WhiteListState whitelist,
    		boolean matchCount, boolean matchMod, boolean matchOreDict)
    {
        FluidFilterConfigurationPacket updatePacket = new FluidFilterConfigurationPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction) + OmniPipeTileEntity.MIN_FLUID_INDEX;
        updatePacket.whiteListState = whitelist;
        updatePacket.matchCount = matchCount;
        updatePacket.matchMod = matchMod;
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

    @Override
    public void handle(ServerPlayer player, FluidFilterConfigurationPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[packet.index];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicFluidFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem.get())
            {
                BasicFluidFilterUpgradeDataCache.setWhiteListState(packet.whiteListState, itemStack);
                BasicFluidFilterUpgradeDataCache.setMatchCount(packet.matchCount, itemStack);
                BasicFluidFilterUpgradeDataCache.setMatchMod(packet.matchMod, itemStack);
                BasicFluidFilterUpgradeDataCache.setMatchOreDictionary(packet.matchOreDictionary, itemStack);

                refreshFilter(packet.index);
            }
        }
    }

    @Override
    public void handle(ServerPlayer player, FluidFilterSlotPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[packet.index];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicFluidFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem.get())
            {
                BasicFluidFilterUpgradeDataCache.setFilterSlot(packet.slot, packet.fluidStack, itemStack);

                refreshFilter(packet.index);
            }
        }
    }

    @Override
    public void handle(ServerPlayer player, FluidClearFilterPacket packet)
    {
        UpgradeInventory inventory = m_sideUpgradeInventories[packet.index];
        int slot = inventory.getSlotForUpgrade(UpgradeType.BasicFluidFilterUpgrade);
        if (slot >= 0 && slot < inventory.getContainerSize())
        {
            ItemStack itemStack = inventory.getItem(slot);
            if (itemStack.getItem() == SwiftItems.s_basicFluidFilterUpgradeItem.get())
            {
                BasicFluidFilterUpgradeDataCache.clearAllFilters(itemStack);

                refreshFilter(packet.index);
            }
        }
    }

    @Override
    public void handle(ServerPlayer player, FluidWildcardFilterPacket packet)
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
