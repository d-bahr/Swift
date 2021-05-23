package swiftmod.pipes;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class UltimateFluidPipeContainer extends FluidPipeContainer
{
    private UltimateFluidPipeContainer(int windowID, PlayerInventory playerInventory, PacketBuffer extraData)
    {
        super(SwiftContainers.s_ultimateFluidPipeContainerType, windowID, playerInventory, extraData,
                UltimateFluidPipeTileEntity::createUpgradeInventory,
                UltimateFluidPipeTileEntity::createSideUpgradeInventory,
                UltimateFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                UltimateFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private UltimateFluidPipeContainer(TileEntity tileEntity, int windowID, PlayerInventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_ultimateFluidPipeContainerType, tileEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                UltimateFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                UltimateFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static UltimateFluidPipeContainer createContainerServerSide(TileEntity tileEntity, int windowID,
            PlayerInventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new UltimateFluidPipeContainer(tileEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static UltimateFluidPipeContainer createContainerClientSide(int windowID, PlayerInventory playerInventory,
            PacketBuffer extraData)
    {
        return new UltimateFluidPipeContainer(windowID, playerInventory, extraData);
    }
}
