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
import swiftmod.common.upgrades.UltimateUpgradeItemStackHandler;
import swiftmod.common.upgrades.UpgradeInventory;

public class UltimateFluidPipeTileEntity extends FluidPipeTileEntity
{
    public UltimateFluidPipeTileEntity(BlockPos pos, BlockState state)
    {
        super(SwiftTileEntities.s_ultimateFluidPipeTileEntityType.get(), pos, state, createUpgradeInventory(),
                UltimateFluidPipeTileEntity::createSideUpgradeInventory);
    }

    @Override
    protected int maxEffectiveSpeedUpgrades()
    {
        return 19;
    }

    @Override
    protected int maxEffectiveStackUpgrades()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    protected int maxEffectiveSpeedDowngrades()
    {
        return Integer.MAX_VALUE;
    }

    @Override
    protected boolean canAcceptTeleportUpgrade()
    {
        return true;
    }

    @Override
    public Component getDisplayName()
    {
        return Component.translatable(DISPLAY_NAME);
    }

    public static String getRegistryName()
    {
        return "ultimate_fluid_pipe";
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
        return UltimateFluidPipeContainer.createContainerServerSide(this, windowID, playerInventory, m_cache,
                this::refreshFilter, this::onChannelUpdate, m_baseUpgradeInventory, m_sideUpgradeInventories);
    }

    public static UpgradeInventory createUpgradeInventory()
    {
        return new UpgradeInventory(new UltimateUpgradeItemStackHandler());
    }

    public static UpgradeInventory createSideUpgradeInventory()
    {
        return new UpgradeInventory(new AdvancedSideFluidUpgradeItemStackHandler());
    }

    public static int NUM_BASE_UPGRADE_SLOTS = 4;
    public static int NUM_SIDE_UPGRADE_SLOTS = 2;

    private static final String DISPLAY_NAME = "container." + Swift.MOD_NAME + "." + getRegistryName();
}
