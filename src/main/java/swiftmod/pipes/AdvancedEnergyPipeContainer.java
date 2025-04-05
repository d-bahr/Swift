package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedEnergyPipeContainer extends EnergyPipeContainer
{
    private AdvancedEnergyPipeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_advancedEnergyPipeContainerType.get(), windowID, playerInventory, extraData,
                AdvancedEnergyPipeTileEntity::createUpgradeInventory,
                Direction.values().length * Direction.values().length,
                AdvancedEnergyPipeTileEntity::createSideUpgradeInventory,
                AdvancedEnergyPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedEnergyPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private AdvancedEnergyPipeContainer(int windowID, Inventory playerInventory,
            PipeDataCache cache, BlockPos pos, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_advancedEnergyPipeContainerType.get(), windowID, playerInventory, cache,
        		pos, upgradeInventory, sideUpgradeInventories,
                AdvancedEnergyPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedEnergyPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static AdvancedEnergyPipeContainer createContainerServerSide(int windowID,
            Inventory playerInventory, PipeDataCache cache, BlockPos pos,
            UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        return new AdvancedEnergyPipeContainer(windowID, playerInventory, cache, pos,
                upgradeInventory, sideUpgradeInventories);
    }

    public static AdvancedEnergyPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new AdvancedEnergyPipeContainer(windowID, playerInventory, extraData);
    }
}
