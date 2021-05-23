package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedFluidPipeContainer extends FluidPipeContainer
{
    private AdvancedFluidPipeContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(SwiftContainers.s_advancedFluidPipeContainerType, windowID, playerInventory, extraData,
                AdvancedFluidPipeTileEntity::createUpgradeInventory,
                AdvancedFluidPipeTileEntity::createSideUpgradeInventory,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private AdvancedFluidPipeContainer(TileEntity tileEntity, int windowID, PlayerInventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_advancedFluidPipeContainerType, tileEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static AdvancedFluidPipeContainer createContainerServerSide(TileEntity tileEntity, int windowID,
            PlayerInventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new AdvancedFluidPipeContainer(tileEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static AdvancedFluidPipeContainer createContainerClientSide(int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData)
    {
        return new AdvancedFluidPipeContainer(windowID, playerInventory, extraData);
    }
}
