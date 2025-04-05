package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class BasicEnergyPipeContainer extends EnergyPipeContainer
{
    private BasicEnergyPipeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_basicEnergyPipeContainerType.get(), windowID, playerInventory, extraData,
                BasicEnergyPipeTileEntity::createUpgradeInventory,
                Direction.values().length,
                BasicEnergyPipeTileEntity::createSideUpgradeInventory,
                BasicEnergyPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicEnergyPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private BasicEnergyPipeContainer(int windowID, Inventory playerInventory,
            PipeDataCache cache,  BlockPos pos,
            UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_basicEnergyPipeContainerType.get(), windowID, playerInventory, cache,
                pos, upgradeInventory, sideUpgradeInventories,
                BasicEnergyPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicEnergyPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static BasicEnergyPipeContainer createContainerServerSide(BlockEntity blockEntity, int windowID,
            Inventory playerInventory, PipeDataCache cache, BlockPos pos,
            UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        return new BasicEnergyPipeContainer(windowID, playerInventory, cache, pos,
                upgradeInventory, sideUpgradeInventories);
    }

    public static BasicEnergyPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new BasicEnergyPipeContainer(windowID, playerInventory, extraData);
    }
}
