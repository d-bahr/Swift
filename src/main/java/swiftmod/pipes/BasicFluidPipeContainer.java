package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class BasicFluidPipeContainer extends FluidPipeContainer
{
    private BasicFluidPipeContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(SwiftContainers.s_basicFluidPipeContainerType, windowID, playerInventory, extraData,
                BasicFluidPipeTileEntity::createUpgradeInventory, BasicFluidPipeTileEntity::createSideUpgradeInventory,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private BasicFluidPipeContainer(TileEntity tileEntity, int windowID, PlayerInventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_basicFluidPipeContainerType, tileEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static BasicFluidPipeContainer createContainerServerSide(TileEntity tileEntity, int windowID,
            PlayerInventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new BasicFluidPipeContainer(tileEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static BasicFluidPipeContainer createContainerClientSide(int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData)
    {
        return new BasicFluidPipeContainer(windowID, playerInventory, extraData);
    }
}
