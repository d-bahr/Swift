package swiftmod.pipes;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import swiftmod.common.SwiftContainers;
import swiftmod.common.upgrades.UpgradeInventory;

public class BasicOmniPipeContainer extends OmniPipeContainer
{
    private BasicOmniPipeContainer(int windowID, Inventory playerInventory, RegistryFriendlyByteBuf extraData)
    {
        super(SwiftContainers.s_basicOmniPipeContainerType.get(), windowID, playerInventory, extraData,
                BasicOmniPipeTileEntity::createUpgradeInventory,
                Direction.values().length * PipeType.numTypes(),
                BasicOmniPipeTileEntity::createSideUpgradeInventory,
                BasicOmniPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicOmniPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    private BasicOmniPipeContainer(int windowID, Inventory playerInventory,
            PipeDataCache cache, RefreshFilterCallback refreshFilterCallback, BlockPos pos,
            UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        super(SwiftContainers.s_basicOmniPipeContainerType.get(), windowID, playerInventory, cache,
                refreshFilterCallback, pos, upgradeInventory, sideUpgradeInventories,
                BasicOmniPipeContainerScreen.PLAYER_INVENTORY_OFFSET_X,
                BasicOmniPipeContainerScreen.PLAYER_INVENTORY_OFFSET_Y);
    }

    public static BasicOmniPipeContainer createContainerServerSide(BlockEntity blockEntity, int windowID,
            Inventory playerInventory, PipeDataCache cache, RefreshFilterCallback refreshFilterCallback,
            BlockPos pos, UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories)
    {
        return new BasicOmniPipeContainer(windowID, playerInventory, cache, refreshFilterCallback, pos,
                upgradeInventory, sideUpgradeInventories);
    }

    public static BasicOmniPipeContainer createContainerClientSide(int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData)
    {
        return new BasicOmniPipeContainer(windowID, playerInventory, extraData);
    }
}
