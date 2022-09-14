package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class BasicItemPipeContainer extends ItemPipeContainer
{
    private BasicItemPipeContainer(int windowID, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_basicItemPipeContainerType, windowID, playerInventory, extraData,
                BasicItemPipeTileEntity::createUpgradeInventory, BasicItemPipeTileEntity::createSideUpgradeInventory,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private BasicItemPipeContainer(BlockEntity blockEntity, int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_basicItemPipeContainerType, blockEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static BasicItemPipeContainer createContainerServerSide(BlockEntity blockEntity, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new BasicItemPipeContainer(blockEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static BasicItemPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
            FriendlyByteBuf extraData)
    {
        return new BasicItemPipeContainer(windowID, playerInventory, extraData);
    }
}
