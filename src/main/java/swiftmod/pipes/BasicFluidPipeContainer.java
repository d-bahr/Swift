package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class BasicFluidPipeContainer extends FluidPipeContainer
{
    private BasicFluidPipeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_basicFluidPipeContainerType.get(), windowID, playerInventory, extraData,
                BasicFluidPipeTileEntity::createUpgradeInventory,
                Direction.values().length,
                BasicFluidPipeTileEntity::createSideUpgradeInventory,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private BasicFluidPipeContainer(int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback, BlockPos pos,
            UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_basicFluidPipeContainerType.get(), windowID, playerInventory, cache,
                refreshFilterCallback, pos, upgradeInventory, sideUpgradeInventories,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static BasicFluidPipeContainer createContainerServerSide(BlockEntity blockEntity, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            BlockPos pos, UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        return new BasicFluidPipeContainer(windowID, playerInventory, cache, refreshFilterCallback, pos,
                upgradeInventory, sideUpgradeInventories);
    }

    public static BasicFluidPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new BasicFluidPipeContainer(windowID, playerInventory, extraData);
    }
}
