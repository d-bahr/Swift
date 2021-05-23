package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class BasicItemPipeContainer extends ItemPipeContainer
{
    private BasicItemPipeContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(SwiftContainers.s_basicItemPipeContainerType, windowID, playerInventory, extraData,
                BasicItemPipeTileEntity::createUpgradeInventory, BasicItemPipeTileEntity::createSideUpgradeInventory,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private BasicItemPipeContainer(TileEntity tileEntity, int windowID, PlayerInventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_basicItemPipeContainerType, tileEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static BasicItemPipeContainer createContainerServerSide(TileEntity tileEntity, int windowID,
            PlayerInventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new BasicItemPipeContainer(tileEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static BasicItemPipeContainer createContainerClientSide(int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData)
    {
        return new BasicItemPipeContainer(windowID, playerInventory, extraData);
    }
}
