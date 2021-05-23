package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class UltimateItemPipeContainer extends ItemPipeContainer
{
    private UltimateItemPipeContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(SwiftContainers.s_ultimateItemPipeContainerType, windowID, playerInventory, extraData,
                UltimateItemPipeTileEntity::createUpgradeInventory,
                UltimateItemPipeTileEntity::createSideUpgradeInventory,
                UltimateItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                UltimateItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private UltimateItemPipeContainer(TileEntity tileEntity, int windowID, PlayerInventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_ultimateItemPipeContainerType, tileEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                UltimateItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                UltimateItemPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static UltimateItemPipeContainer createContainerServerSide(TileEntity tileEntity, int windowID,
            PlayerInventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new UltimateItemPipeContainer(tileEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static UltimateItemPipeContainer createContainerClientSide(int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData)
    {
        return new UltimateItemPipeContainer(windowID, playerInventory, extraData);
    }
}
