package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedOmniPipeContainer extends OmniPipeContainer
{
    private AdvancedOmniPipeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_advancedOmniPipeContainerType.get(), windowID, playerInventory, extraData,
                AdvancedOmniPipeTileEntity::createUpgradeInventory,
                Direction.values().length * Direction.values().length * PipeType.numTypes(),
                AdvancedOmniPipeTileEntity::createSideUpgradeInventory,
                AdvancedOmniPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedOmniPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private AdvancedOmniPipeContainer(int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback, BlockPos pos, UpgradeInventory upgradeInventory,
            UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_advancedOmniPipeContainerType.get(), windowID, playerInventory, cache,
                refreshFilterCallback, pos, upgradeInventory, sideUpgradeInventories,
                AdvancedOmniPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                AdvancedOmniPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static AdvancedOmniPipeContainer createContainerServerSide(int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            BlockPos pos,UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        return new AdvancedOmniPipeContainer(windowID, playerInventory, cache, refreshFilterCallback,
                pos, upgradeInventory, sideUpgradeInventories);
    }

    public static AdvancedOmniPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new AdvancedOmniPipeContainer(windowID, playerInventory, extraData);
    }
}
