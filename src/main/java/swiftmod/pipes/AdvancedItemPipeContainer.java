package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedItemPipeContainer extends ItemPipeContainer
{
    private AdvancedItemPipeContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(SwiftContainers.s_advancedItemPipeContainerType, windowID, playerInventory, extraData,
                AdvancedItemPipeTileEntity::createUpgradeInventory,
                AdvancedItemPipeTileEntity::createSideUpgradeInventory,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private AdvancedItemPipeContainer(TileEntity tileEntity, int windowID, PlayerInventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_advancedItemPipeContainerType, tileEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static AdvancedItemPipeContainer createContainerServerSide(TileEntity tileEntity, int windowID,
            PlayerInventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new AdvancedItemPipeContainer(tileEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static AdvancedItemPipeContainer createContainerClientSide(int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData)
    {
        return new AdvancedItemPipeContainer(windowID, playerInventory, extraData);
    }
}
