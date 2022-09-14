package swiftmod.common.upgrades;

import javax.annotation.Nonnull;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import swiftmod.common.ContainerItemStackHandler;

public abstract class UpgradeItemStackHandler extends ContainerItemStackHandler
{
    public UpgradeItemStackHandler()
    {
        super(3);
    }

    public UpgradeItemStackHandler(int numSlots)
    {
        super(numSlots);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        if (slot >= getSlots())
            return false;
        Item item = stack.getItem();
        if (!(item instanceof UpgradeItem))
            return false;
        else
            return getSlotForUpgrade((UpgradeItem) item) == slot;
    }

    @Override
    public int getSlotLimit(int slot)
    {
        if (slot >= getSlots())
            return 0;
        else
            return 64;
    }

    public int getMaxUpgrades(UpgradeItem item)
    {
        return getMaxUpgrades(item.getType());
    }

    public abstract int getMaxUpgrades(UpgradeType type);

    public int getSlotForUpgrade(UpgradeItem item)
    {
        return getSlotForUpgrade(item.getType());
    }

    public abstract int getSlotForUpgrade(UpgradeType type);

    @Override
    public int getStackLimit(int slot, @Nonnull ItemStack stack)
    {
        if (slot >= getSlots())
            return 0;
        Item item = stack.getItem();
        if (!(item instanceof UpgradeItem))
            return 0;
        UpgradeItem upgradeItem = (UpgradeItem) item;
        if (getSlotForUpgrade(upgradeItem) != slot)
            return 0;
        int stackSize = stack.getMaxStackSize();
        return Math.min(getMaxUpgrades(upgradeItem.getType()), stackSize);
    }

    public int getStackLimit(@Nonnull ItemStack stack)
    {
        Item item = stack.getItem();
        if (!(item instanceof UpgradeItem))
            return 0;
        UpgradeItem upgradeItem = (UpgradeItem) item;
        if (getSlotForUpgrade(upgradeItem) < 0)
            return 0;
        int stackSize = stack.getMaxStackSize();
        return Math.min(getMaxUpgrades(upgradeItem.getType()), stackSize);
    }
}
