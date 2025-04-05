package swiftmod.pipes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.datafix.fixes.ChunkPalettedStorageFix.Direction;
import swiftmod.common.Swift;
import swiftmod.common.SwiftTileEntities;
import swiftmod.common.upgrades.AdvancedSideEnergyUpgradeItemStackHandler;
import swiftmod.common.upgrades.AdvancedSideFluidUpgradeItemStackHandler;
import swiftmod.common.upgrades.AdvancedSideItemUpgradeItemStackHandler;
import swiftmod.common.upgrades.AdvancedUpgradeItemStackHandler;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedOmniPipeTileEntity extends OmniPipeTileEntity
{
    public AdvancedOmniPipeTileEntity(BlockPos pos, BlockState state)
    {
        super(SwiftTileEntities.s_advancedOmniPipeTileEntityType.get(), pos, state, createUpgradeInventory(),
        		Direction.values().length * Direction.values().length * PipeType.numTypes(), AdvancedOmniPipeTileEntity::createSideUpgradeInventory);
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
        return "advanced_omni_pipe";
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
        return AdvancedOmniPipeContainer.createContainerServerSide(windowID, playerInventory, m_cache,
                this::refreshFilter, getBlockPos(), m_baseUpgradeInventory, m_sideUpgradeInventories);
    }

    public static UpgradeInventory createUpgradeInventory()
    {
        return new UpgradeInventory(new AdvancedUpgradeItemStackHandler());
    }

    public static UpgradeInventory createSideUpgradeInventory(int index)
    {
    	int numDir = Direction.values().length;
    	if (index < OmniPipeTileEntity.MAX_ITEM_INDEX * numDir)
    		return new UpgradeInventory(new AdvancedSideItemUpgradeItemStackHandler());
    	else if (index < OmniPipeTileEntity.MAX_FLUID_INDEX * numDir)
    		return new UpgradeInventory(new AdvancedSideFluidUpgradeItemStackHandler());
    	else
    		return new UpgradeInventory(new AdvancedSideEnergyUpgradeItemStackHandler());
    }

    private static final String DISPLAY_NAME = "container." + Swift.MOD_NAME + "." + getRegistryName();
}
