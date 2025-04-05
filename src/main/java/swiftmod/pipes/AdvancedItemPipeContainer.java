package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftItems;
import swiftmod.common.WhiteListState;
import swiftmod.common.upgrades.BasicItemFilterUpgradeDataCache;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.WildcardFilterUpgradeDataCache;

public class AdvancedItemPipeContainer extends ItemPipeContainer
{
    private AdvancedItemPipeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_advancedItemPipeContainerType.get(), windowID, playerInventory, extraData,
                AdvancedItemPipeTileEntity::createUpgradeInventory,
                Direction.values().length * Direction.values().length,
                AdvancedItemPipeTileEntity::createSideUpgradeInventory,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private AdvancedItemPipeContainer(int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback, BlockPos pos,
            UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_advancedItemPipeContainerType.get(), windowID, playerInventory, cache,
                refreshFilterCallback, pos, upgradeInventory, sideUpgradeInventories,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static AdvancedItemPipeContainer createContainerServerSide(int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            BlockPos pos, UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        return new AdvancedItemPipeContainer(windowID, playerInventory, cache, refreshFilterCallback,
                pos, upgradeInventory, sideUpgradeInventories);
    }

    public static AdvancedItemPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new AdvancedItemPipeContainer(windowID, playerInventory, extraData);
    }

    public BasicItemFilterUpgradeDataCache getBasicFilterCache(Direction neighborDir, Direction handlerDir)
    {
		int index = AdvancedItemPipeTileEntity.toTransferIndex(neighborDir, handlerDir);
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
    
    public BlockPos getNeighborPos(Direction neighborDir)
    {
    	return m_pos.relative(neighborDir);
    }

    public WildcardFilterUpgradeDataCache getWildcardFilterCache(Direction neighborDir, Direction handlerDir)
    {
		int index = AdvancedItemPipeTileEntity.toTransferIndex(neighborDir, handlerDir);
		return getWildcardFilterCache(index);
    }

    public void updateFilter(Direction neighborDir, Direction handlerDir, int slot, ItemStack itemStack, int quantity)
    {
		int index = AdvancedItemPipeTileEntity.toTransferIndex(neighborDir, handlerDir);
		updateFilter(index, slot, itemStack, quantity);
    }

    public void clearAllFilters(Direction neighborDir, Direction handlerDir)
    {
		int index = AdvancedItemPipeTileEntity.toTransferIndex(neighborDir, handlerDir);
		clearAllFilters(index);
    }

    public void addWildcardFilter(Direction neighborDir, Direction handlerDir, String filter)
    {
		int index = AdvancedItemPipeTileEntity.toTransferIndex(neighborDir, handlerDir);
		addWildcardFilter(index, filter);
    }

    public void removeWildcardFilter(Direction neighborDir, Direction handlerDir, String filter)
    {
		int index = AdvancedItemPipeTileEntity.toTransferIndex(neighborDir, handlerDir);
		removeWildcardFilter(index, filter);
    }

    public void updateFilterConfiguration(Direction neighborDir, Direction handlerDir, WhiteListState whitelist,
    		boolean matchCount, boolean matchDamage, boolean matchMod, boolean matchNBT, boolean matchOreDict)
    {
		int index = AdvancedItemPipeTileEntity.toTransferIndex(neighborDir, handlerDir);
		updateFilterConfiguration(index, whitelist, matchCount, matchDamage, matchMod, matchNBT, matchOreDict);
    }
}
