package swiftmod.pipes;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import swiftmod.common.Color;
import swiftmod.common.Swift;
import swiftmod.common.SwiftTileEntities;
import swiftmod.common.SwiftUtils;
import swiftmod.common.upgrades.AdvancedSideItemUpgradeItemStackHandler;
import swiftmod.common.upgrades.AdvancedUpgradeItemStackHandler;
import swiftmod.common.upgrades.UpgradeInventory;

public class AdvancedItemPipeTileEntity extends ItemPipeTileEntity
{
    public AdvancedItemPipeTileEntity(BlockPos pos, BlockState state)
    {
        super(SwiftTileEntities.s_advancedItemPipeTileEntityType.get(), pos, state, createUpgradeInventory(),
                Direction.values().length * Direction.values().length, AdvancedItemPipeTileEntity::createSideUpgradeInventory);
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
    public Component getDisplayName()
    {
        return Component.translatable(DISPLAY_NAME);
    }

    public static String getRegistryName()
    {
        return "advanced_item_pipe";
    }

	@Override
    public PipeTransferData<?> getTransferData(PipeType type, Direction neighborDir, Direction handlerDir)
    {
		int index = toTransferIndex(neighborDir, handlerDir);
		PipeTransferData<ItemStack> td = new PipeTransferData<ItemStack>();
    	td.maxTransferQuantity = m_transferQuantities[index];
    	td.tickRate = m_tickRates[index];
    	td.redstoneControl = m_cache.redstoneControls[index];
    	td.color = m_cache.colors[index];
		td.filter = m_filters[index];
    	return td;
    }

    @Override
    protected PipeTransferHandler<ItemStack> createTransferHandler(int transferIndex)
    {
    	ItemPipeTransferHandler newHandler = new ItemPipeTransferHandler();
    	newHandler.pipe = this;
    	newHandler.neighborDir = SwiftUtils.indexToDir(transferIndex / 6);
    	newHandler.handlerDir = SwiftUtils.indexToDir(transferIndex % 6);
    	return newHandler;
    }
	
    @Override
	public Color getRenderColorForSide(Direction dir)
	{
    	if (m_cache == null)
    		return Color.Transparent;
    	// Returns the first non-transparent color used on a given side.
        int start = SwiftUtils.dirToIndex(dir) * 6;
        int end = start + 6;
        for (int i = start; i < end; ++i)
        {
        	Color c = m_cache.getColor(i);
        	if (c != Color.Transparent)
        		return c;
        }
        return Color.Transparent;
	}
    
    public static int toTransferIndex(Direction neighborDir, Direction handlerDir)
    {
    	return SwiftUtils.dirToIndex(neighborDir) * 6 + SwiftUtils.dirToIndex(handlerDir);
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
        return AdvancedItemPipeContainer.createContainerServerSide(windowID, playerInventory, m_cache,
                this::refreshFilter, getBlockPos(), m_baseUpgradeInventory, m_sideUpgradeInventories);
    }

    public static UpgradeInventory createUpgradeInventory()
    {
        return new UpgradeInventory(new AdvancedUpgradeItemStackHandler());
    }

    public static UpgradeInventory createSideUpgradeInventory(int index)
    {
        return new UpgradeInventory(new AdvancedSideItemUpgradeItemStackHandler());
    }

    private static final String DISPLAY_NAME = "container." + Swift.MOD_NAME + "." + getRegistryName();
}
