package swiftmod.pipes;

import java.util.function.Supplier;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.RegistryFriendlyByteBuf;
import swiftmod.common.SlotBase;
import swiftmod.common.SwiftUtils;
import swiftmod.common.gui.SideIOConfigurationWidget;
import swiftmod.common.upgrades.UpgradeInventory;
import swiftmod.pipes.PipeTileEntity.SideUpgradeInventoryBuilder;

public class EnergyPipeContainer extends PipeContainer
{
    protected EnergyPipeContainer(@Nullable MenuType<?> type, int windowID, Inventory playerInventory,
    		RegistryFriendlyByteBuf extraData, Supplier<UpgradeInventory> upgradeInventorySupplier,
            int numSideUpgradeInventories, SideUpgradeInventoryBuilder sideUpgradeInventorySupplier, int x, int y)
    {
        super(type, windowID, playerInventory, extraData, upgradeInventorySupplier, numSideUpgradeInventories, sideUpgradeInventorySupplier, x, y);
        
        initSideUpgradeSlots();
    }

    protected EnergyPipeContainer(@Nullable MenuType<?> type, int windowID,
            Inventory playerInventory, PipeDataCache cache, BlockPos pos,
            UpgradeInventory upgradeInventory, UpgradeInventory[] sideUpgradeInventories, int x, int y)
    {
        super(type, windowID, playerInventory, cache, pos,
                upgradeInventory, sideUpgradeInventories, x, y);
        
        initSideUpgradeSlots();
    }

    protected void initSideUpgradeSlots()
    {
        int x = PipeContainerScreen.BASE_PANEL_X + SideIOConfigurationWidget.SPEED_UPGRADE_SLOT_X + 1;
        int y = PipeContainerScreen.BASE_PANEL_Y + SideIOConfigurationWidget.SPEED_UPGRADE_SLOT_Y + 1;

        for (Direction dir : Direction.values())
        {
            int i = SwiftUtils.dirToIndex(dir);

            m_sideUpgradeInventoryStartingSlots[i] = getNumSlots();
            SlotBase[] upgradeSlots = m_sideUpgradeInventories[i].createSlots(x, y, 2, 1);
            addSlots(upgradeSlots);
        }
    }
}
