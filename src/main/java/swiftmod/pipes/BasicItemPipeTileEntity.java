package swiftmod.pipes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import swiftmod.common.Swift;
import swiftmod.common.SwiftTileEntities;
import swiftmod.common.upgrades.BasicSideUpgradeItemStackHandler;
import swiftmod.common.upgrades.BasicUpgradeItemStackHandler;
import swiftmod.common.upgrades.UpgradeInventory;

public class BasicItemPipeTileEntity extends ItemPipeTileEntity
{
    public BasicItemPipeTileEntity(BlockPos pos, BlockState state)
    {
        super(SwiftTileEntities.s_basicItemPipeTileEntityType.get(), pos, state, createUpgradeInventory(),
                BasicItemPipeTileEntity::createSideUpgradeInventory);
    }

    @Override
    protected int maxEffectiveSpeedUpgrades()
    {
        return 0;
    }

    @Override
    protected int maxEffectiveStackUpgrades()
    {
        return 0;
    }

    @Override
    protected int maxEffectiveSpeedDowngrades()
    {
        return 0;
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
        return "basic_item_pipe";
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
        return BasicItemPipeContainer.createContainerServerSide(this, windowID, playerInventory, m_cache,
                this::refreshFilter, this::onChannelUpdate, m_baseUpgradeInventory, m_sideUpgradeInventories);
    }

    public static UpgradeInventory createUpgradeInventory()
    {
        return new UpgradeInventory(new BasicUpgradeItemStackHandler());
    }

    public static UpgradeInventory createSideUpgradeInventory()
    {
        return new UpgradeInventory(new BasicSideUpgradeItemStackHandler());
    }

    public static int NUM_BASE_UPGRADE_SLOTS = 0;
    public static int NUM_SIDE_UPGRADE_SLOTS = 0;

    private static final String DISPLAY_NAME = "container." + Swift.MOD_NAME + "." + getRegistryName();
}
