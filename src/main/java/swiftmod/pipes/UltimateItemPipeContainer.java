package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class UltimateItemPipeContainer extends ItemPipeContainer
{
    private UltimateItemPipeContainer(int windowID, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_ultimateItemPipeContainerType.get(), windowID, playerInventory, extraData,
                UltimateItemPipeTileEntity::createUpgradeInventory,
                UltimateItemPipeTileEntity::createSideUpgradeInventory,
                UltimateItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                UltimateItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private UltimateItemPipeContainer(BlockEntity blockEntity, int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_ultimateItemPipeContainerType.get(), blockEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                UltimateItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                UltimateItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static UltimateItemPipeContainer createContainerServerSide(BlockEntity blockEntity, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new UltimateItemPipeContainer(blockEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static UltimateItemPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
            FriendlyByteBuf extraData)
    {
        return new UltimateItemPipeContainer(windowID, playerInventory, extraData);
    }
}
