package swiftmod.pipes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import swiftmod.common.Swift;
import swiftmod.common.SwiftTileEntities;
import swiftmod.common.upgrades.AdvancedSideEnergyUpgradeItemStackHandler;
import swiftmod.common.upgrades.AdvancedUpgradeItemStackHandler;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedEnergyPipeTileEntity extends EnergyPipeTileEntity
{
    public AdvancedEnergyPipeTileEntity(BlockPos pos, BlockState state)
    {
        super(SwiftTileEntities.s_advancedEnergyPipeTileEntityType.get(), pos, state, createUpgradeInventory(),
        		Direction.values().length * Direction.values().length, AdvancedEnergyPipeTileEntity::createSideUpgradeInventory);
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
    public Component getDisplayName()
    {
        return Component.translatable(DISPLAY_NAME);
    }

    public static String getRegistryName()
    {
        return "advanced_energy_pipe";
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
        return AdvancedEnergyPipeContainer.createContainerServerSide(windowID, playerInventory, m_cache,
                getBlockPos(), m_baseUpgradeInventory, m_sideUpgradeInventories);
    }

    public static UpgradeInventory createUpgradeInventory()
    {
        return new UpgradeInventory(new AdvancedUpgradeItemStackHandler());
    }

    public static UpgradeInventory createSideUpgradeInventory(int index)
    {
        return new UpgradeInventory(new AdvancedSideEnergyUpgradeItemStackHandler());
    }

    private static final String DISPLAY_NAME = "container." + Swift.MOD_NAME + "." + getRegistryName();
}
