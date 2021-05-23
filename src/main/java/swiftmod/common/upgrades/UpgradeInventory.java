package swiftmod.common.upgrades;

import net.minecraft.item.ItemStack;
import swiftmod.common.ContainerInventory;

public class UpgradeInventory extends ContainerInventory
{
    public UpgradeInventory(UpgradeItemStackHandler itemStackHandler)
    {
        super(itemStackHandler);
    }

    public int getStackLimit(ItemStack stack)
    {
        return ((UpgradeItemStackHandler)m_contents).getStackLimit(stack);
    }

    public int getMaxUpgrades(UpgradeType type)
    {
        return ((UpgradeItemStackHandler)m_contents).getMaxUpgrades(type);
    }

    public int getSlotForUpgrade(UpgradeType type)
    {
        return ((UpgradeItemStackHandler)m_contents).getSlotForUpgrade(type);
    }
}
