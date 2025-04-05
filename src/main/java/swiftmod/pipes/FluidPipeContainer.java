package swiftmod.pipes;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.PacketDistributor;
import swiftmod.common.SlotBase;
import swiftmod.common.SwiftItems;
import swiftmod.common.SwiftUtils;
import swiftmod.common.WhiteListState;
import swiftmod.common.client.FluidClearFilterPacket;
import swiftmod.common.client.FluidFilterConfigurationPacket;
import swiftmod.common.client.FluidFilterSlotPacket;
import swiftmod.common.client.FluidWildcardFilterPacket;
import swiftmod.common.gui.SideIOConfigurationWidget;
import swiftmod.common.upgrades.BasicFluidFilterUpgradeDataCache;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.UpgradeType;
import swiftmod.common.upgrades.WildcardFilterUpgradeDataCache;
import swiftmod.pipes.PipeTileEntity.SideUpgradeInventoryBuilder;

public class FluidPipeContainer extends PipeContainer implements FluidFilterConfigurationPacket.Handler,
        FluidFilterSlotPacket.Handler, FluidClearFilterPacket.Handler, FluidWildcardFilterPacket.Handler
{
    protected FluidPipeContainer(@Nullable MenuType<?> type, int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData, Supplier<UpgradeInventory> upgradeInventorySupplier,
            int numSideUpgradeInventories, SideUpgradeInventoryBuilder sideUpgradeInventorySupplier, int x, int y)
    {
        super(type, windowID, playerInventory, extraData, upgradeInventorySupplier, numSideUpgradeInventories, sideUpgradeInventorySupplier, x, y);
        
        initSideUpgradeSlots();
    }

    protected FluidPipeContainer(@Nullable MenuType<?> type, int windowID,
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

        for (Direction dir : Direction.values())
        {
            int i = SwiftUtils.dirToIndex(dir);

            m_sideUpgradeInventoryStartingSlots[i] = getNumSlots();
            SlotBase[] upgradeSlots = m_sideUpgradeInventories[i].createSlots(x, y, 2, 1);
            SlotBase filterUpgradeSlot = m_sideUpgradeInventories[i].createSlot(upgradeSlots.length,
            		PipeContainerScreen.BASE_PANEL_X + SideIOConfigurationWidget.FILTER_UPGRADE_SLOT_X + 1,
            		PipeContainerScreen.BASE_PANEL_Y + SideIOConfigurationWidget.FILTER_UPGRADE_SLOT_Y + 1);
            filterUpgradeSlot.setChangedCallback((slot) -> onFilterUpgradeSlotChanged(slot, i));

            addSlots(upgradeSlots);
            addSlot(filterUpgradeSlot);
        }
    }

    public BasicFluidFilterUpgradeDataCache getBasicFilterCache(Direction direction)
    {
        BasicFluidFilterUpgradeDataCache cache = new BasicFluidFilterUpgradeDataCache();
        UpgradeInventory inventory = m_sideUpgradeInventories[SwiftUtils.dirToIndex(direction)];
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

    public WildcardFilterUpgradeDataCache getWildcardFilterCache(Direction direction)
    {
		return getWildcardFilterCache(SwiftUtils.dirToIndex(direction));
    }

    public void updateFilter(Direction direction, int slot, FluidStack fluidStack)
    {
        FluidFilterSlotPacket updatePacket = new FluidFilterSlotPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction);
        updatePacket.slot = slot;
        updatePacket.fluidStack = fluidStack;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void clearAllFilters(Direction direction)
    {
    	FluidClearFilterPacket updatePacket = new FluidClearFilterPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction);
        PacketDistributor.sendToServer(updatePacket);
    }

    public void addWildcardFilter(Direction direction, String filter)
    {
        getWildcardFilterCache(SwiftUtils.dirToIndex(direction)).addFilter(filter);

        FluidWildcardFilterPacket updatePacket = new FluidWildcardFilterPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction);
        updatePacket.filter = filter;
        updatePacket.add = true;
        PacketDistributor.sendToServer(updatePacket);
    }

    public void removeWildcardFilter(Direction direction, String filter)
    {
        getWildcardFilterCache(SwiftUtils.dirToIndex(direction)).removeFilter(filter);

        FluidWildcardFilterPacket updatePacket = new FluidWildcardFilterPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction);
        updatePacket.filter = filter;
        updatePacket.add = false;
        PacketDistributor.sendToServer(updatePacket);
    }
    
    public void updateFilterConfiguration(Direction direction, WhiteListState whitelist,
    		boolean matchCount, boolean matchMod, boolean matchOreDict)
    {
        FluidFilterConfigurationPacket updatePacket = new FluidFilterConfigurationPacket();
        updatePacket.index = SwiftUtils.dirToIndex(direction);
        updatePacket.whiteListState = whitelist;
        updatePacket.matchCount = matchCount;
        updatePacket.matchMod = matchMod;
        updatePacket.matchOreDictionary = matchOreDict;
        PacketDistributor.sendToServer(updatePacket);
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
