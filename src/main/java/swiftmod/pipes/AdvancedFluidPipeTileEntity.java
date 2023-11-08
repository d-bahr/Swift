package swiftmod.pipes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import swiftmod.common.Swift;
import swiftmod.common.SwiftTileEntities;
import swiftmod.common.upgrades.AdvancedSideFluidUpgradeItemStackHandler;
import swiftmod.common.upgrades.AdvancedUpgradeItemStackHandler;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedFluidPipeTileEntity extends FluidPipeTileEntity
{
    public AdvancedFluidPipeTileEntity(BlockPos pos, BlockState state)
    {
        super(SwiftTileEntities.s_advancedFluidPipeTileEntityType.get(), pos, state, createUpgradeInventory(),
                AdvancedFluidPipeTileEntity::createSideUpgradeInventory);
    }

    @Override
    protected int maxEffectiveSpeedUpgrades()
    {
        return 19;
    }

    @Override
    protected int maxEffectiveStackUpgrades()
    {
        return 1;
    }

    @Override
    protected int maxEffectiveSpeedDowngrades()
    {
        return 64;
    }

    @Override
    protected boolean canAcceptTeleportUpgrade()
    {
        return false;
    }

    @Override
    public Component getDisplayName()
    {
        return Component.translatable(DISPLAY_NAME);
    }

    public static String getRegistryName()
    {
        return "advanced_fluid_pipe";
    }

    /**
     * This function has nothing to do with GUI; it is called by Forge to create the server-side
     * container.
     * 
     * @param windowID
     * @param playerInventory
     * @param playerEntity
     * @return
     */
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory playerInventory, Player playerEntity)
    {
        return AdvancedFluidPipeContainer.createContainerServerSide(this, windowID, playerInventory, m_cache,
                this::refreshFilter, this::onChannelUpdate, m_baseUpgradeInventory, m_sideUpgradeInventories);
    }

    public static UpgradeInventory createUpgradeInventory()
    {
        return new UpgradeInventory(new AdvancedUpgradeItemStackHandler());
    }

    public static UpgradeInventory createSideUpgradeInventory()
    {
        return new UpgradeInventory(new AdvancedSideFluidUpgradeItemStackHandler());
    }

    public static int NUM_BASE_UPGRADE_SLOTS = 3;
    public static int NUM_SIDE_UPGRADE_SLOTS = 2;

    private static final String DISPLAY_NAME = "container." + Swift.MOD_NAME + "." + getRegistryName();
}
