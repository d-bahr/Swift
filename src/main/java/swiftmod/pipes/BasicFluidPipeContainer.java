package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class BasicFluidPipeContainer extends FluidPipeContainer
{
    private BasicFluidPipeContainer(int windowID, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_basicFluidPipeContainerType, windowID, playerInventory, extraData,
                BasicFluidPipeTileEntity::createUpgradeInventory, BasicFluidPipeTileEntity::createSideUpgradeInventory,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private BasicFluidPipeContainer(BlockEntity blockEntity, int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_basicFluidPipeContainerType, blockEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static BasicFluidPipeContainer createContainerServerSide(BlockEntity blockEntity, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new BasicFluidPipeContainer(blockEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static BasicFluidPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
            FriendlyByteBuf extraData)
    {
        return new BasicFluidPipeContainer(windowID, playerInventory, extraData);
    }
}
