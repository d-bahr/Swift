package swiftmod.common.upgrades;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import swiftmod.common.Filter;

public interface IFluidFilterUpgradeItem
{
    public Filter<FluidStack> createFluidFilter(ItemStack itemStack);
}
