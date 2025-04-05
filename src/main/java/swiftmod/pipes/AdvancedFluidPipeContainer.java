package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedFluidPipeContainer extends FluidPipeContainer
{
    private AdvancedFluidPipeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_advancedFluidPipeContainerType.get(), windowID, playerInventory, extraData,
                AdvancedFluidPipeTileEntity::createUpgradeInventory,
                Direction.values().length * Direction.values().length,
                AdvancedFluidPipeTileEntity::createSideUpgradeInventory,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private AdvancedFluidPipeContainer(int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback, BlockPos pos, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_advancedFluidPipeContainerType.get(), windowID, playerInventory, cache,
                refreshFilterCallback, pos, upgradeInventory, sideUpgradeInventories,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedFluidPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static AdvancedFluidPipeContainer createContainerServerSide(int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            BlockPos pos, UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        return new AdvancedFluidPipeContainer(windowID, playerInventory, cache, refreshFilterCallback,
                pos, upgradeInventory, sideUpgradeInventories);
    }

    public static AdvancedFluidPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new AdvancedFluidPipeContainer(windowID, playerInventory, extraData);
    }
}
