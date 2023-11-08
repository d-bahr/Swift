package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedItemPipeContainer extends ItemPipeContainer
{
    private AdvancedItemPipeContainer(int windowID, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_advancedItemPipeContainerType.get(), windowID, playerInventory, extraData,
                AdvancedItemPipeTileEntity::createUpgradeInventory,
                AdvancedItemPipeTileEntity::createSideUpgradeInventory,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private AdvancedItemPipeContainer(BlockEntity blockEntity, int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_advancedItemPipeContainerType.get(), blockEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static AdvancedItemPipeContainer createContainerServerSide(BlockEntity blockEntity, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new AdvancedItemPipeContainer(blockEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static AdvancedItemPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
            FriendlyByteBuf extraData)
    {
        return new AdvancedItemPipeContainer(windowID, playerInventory, extraData);
    }
}
