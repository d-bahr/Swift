package swiftmod.common.upgrades;

import net.minecraft.item.ItemStack;
import swiftmod.common.Filter;

public interface IItemFilterUpgradeItem
{
    public Filter<ItemStack> createItemFilter(ItemStack itemStack);
}
