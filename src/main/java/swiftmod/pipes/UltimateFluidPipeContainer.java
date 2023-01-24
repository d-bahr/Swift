package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class UltimateFluidPipeContainer extends FluidPipeContainer
{
    private UltimateFluidPipeContainer(int windowID, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_ultimateFluidPipeContainerType, windowID, playerInventory, extraData,
                UltimateFluidPipeTileEntity::createUpgradeInventory,
                UltimateFluidPipeTileEntity::createSideUpgradeInventory,
                UltimateFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                UltimateFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private UltimateFluidPipeContainer(BlockEntity blockEntity, int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_ultimateFluidPipeContainerType, blockEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                UltimateFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                UltimateFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static UltimateFluidPipeContainer createContainerServerSide(BlockEntity blockEntity, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new UltimateFluidPipeContainer(blockEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static UltimateFluidPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
            FriendlyByteBuf extraData)
    {
        return new UltimateFluidPipeContainer(windowID, playerInventory, extraData);
    }
}
