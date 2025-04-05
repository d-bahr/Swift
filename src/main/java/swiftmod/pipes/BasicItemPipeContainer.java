package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import swiftmod.common.SwiftContainers;
import swiftmod.common.SwiftUtils;
import swiftmod.common.WhiteListState;
import swiftmod.common.upgrades.BasicItemFilterUpgradeDataCache;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.common.upgrades.WildcardFilterUpgradeDataCache;

public class BasicItemPipeContainer extends ItemPipeContainer
{
    private BasicItemPipeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_basicItemPipeContainerType.get(), windowID, playerInventory, extraData,
                BasicItemPipeTileEntity::createUpgradeInventory,
                Direction.values().length,
                BasicItemPipeTileEntity::createSideUpgradeInventory,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private BasicItemPipeContainer(int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback, BlockPos pos,
            UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_basicItemPipeContainerType.get(), windowID, playerInventory, cache,
                refreshFilterCallback, pos, upgradeInventory, sideUpgradeInventories,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static BasicItemPipeContainer createContainerServerSide(int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            BlockPos pos, UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        return new BasicItemPipeContainer(windowID, playerInventory, cache, refreshFilterCallback,
                pos, upgradeInventory, sideUpgradeInventories);
    }

    public static BasicItemPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new BasicItemPipeContainer(windowID, playerInventory, extraData);
    }

    public BasicItemFilterUpgradeDataCache getBasicFilterCache(Direction direction)
    {
		return getBasicFilterCache(SwiftUtils.dirToIndex(direction));
    }

    public WildcardFilterUpgradeDataCache getWildcardFilterCache(Direction direction)
    {
		return getWildcardFilterCache(SwiftUtils.dirToIndex(direction));
    }

    public void updateFilter(Direction direction, int slot, ItemStack itemStack, int quantity)
    {
		updateFilter(SwiftUtils.dirToIndex(direction), slot, itemStack, quantity);
    }

    public void clearAllFilters(Direction direction)
    {
    	clearAllFilters(SwiftUtils.dirToIndex(direction));
    }

    public void addWildcardFilter(Direction direction, String filter)
    {
    	addWildcardFilter(SwiftUtils.dirToIndex(direction), filter);
    }

    public void removeWildcardFilter(Direction direction, String filter)
    {
    	removeWildcardFilter(SwiftUtils.dirToIndex(direction), filter);
    }

    public void updateFilterConfiguration(Direction direction, WhiteListState whitelist,
    		boolean matchCount, boolean matchDamage, boolean matchMod, boolean matchNBT, boolean matchOreDict)
    {
    	updateFilterConfiguration(SwiftUtils.dirToIndex(direction), whitelist, matchCount,
    			matchDamage, matchMod, matchNBT, matchOreDict);
    }
}
