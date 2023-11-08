package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedFluidPipeContainer extends FluidPipeContainer
{
    private AdvancedFluidPipeContainer(int windowID, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_advancedFluidPipeContainerType.get(), windowID, playerInventory, extraData,
                AdvancedFluidPipeTileEntity::createUpgradeInventory,
                AdvancedFluidPipeTileEntity::createSideUpgradeInventory,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private AdvancedFluidPipeContainer(BlockEntity blockEntity, int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_advancedFluidPipeContainerType.get(), blockEntity, windowID, playerInventory, cache,
                refreshFilterCallback, channelManagerCallback, upgradeInventory, sideUpgradeInventories,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static AdvancedFluidPipeContainer createContainerServerSide(BlockEntity blockEntity, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            ChannelManagerCallback channelManagerCallback, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        return new AdvancedFluidPipeContainer(blockEntity, windowID, playerInventory, cache, refreshFilterCallback,
                channelManagerCallback, upgradeInventory, sideUpgradeInventories);
    }

    public static AdvancedFluidPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
            FriendlyByteBuf extraData)
    {
        return new AdvancedFluidPipeContainer(windowID, playerInventory, extraData);
    }
}
