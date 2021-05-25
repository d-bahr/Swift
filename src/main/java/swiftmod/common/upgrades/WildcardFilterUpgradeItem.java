package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import swiftmod.common.Filter;
import swiftmod.common.ItemContainerProvider;
import swiftmod.common.SwiftTextUtils;
import swiftmod.common.WildcardFluidFilter;
import swiftmod.common.WildcardItemFilter;

public class WildcardFilterUpgradeItem extends FilterUpgradeItem implements IItemFilterUpgradeItem, IFluidFilterUpgradeItem
{
    public WildcardFilterUpgradeItem()
    {
        super(UpgradeType.WildcardFilterUpgrade, REGISTRY_NAME);
    }

    public Filter<ItemStack> createItemFilter(ItemStack stack)
    {
        if (stack == null)
            return new WildcardItemFilter();
        if (stack.isEmpty() || !stack.hasTag())
            return new WildcardItemFilter();
        CompoundNBT nbt = stack.getTagElement(FilterUpgradeItem.NBT_TAG);
        if (nbt == null)
            return new WildcardItemFilter();

        List<String> filters = WildcardFilterUpgradeDataCache.getFilters(stack);
        return new WildcardItemFilter(filters);
    }

    public Filter<FluidStack> createFluidFilter(ItemStack stack)
    {
        if (stack == null)
            return new WildcardFluidFilter();
        if (stack.isEmpty() || !stack.hasTag())
            return new WildcardFluidFilter();
        CompoundNBT nbt = stack.getTagElement(FilterUpgradeItem.NBT_TAG);
        if (nbt == null)
            return new WildcardFluidFilter();

        List<String> filters = WildcardFilterUpgradeDataCache.getFilters(stack);
        return new WildcardFluidFilter(filters);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                WildcardFilterUpgradeContainer::createContainerServerSide, WildcardFilterUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Filters items or fluids using ore dictionary tags.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, World world, List<ITextComponent> tooltip, ITooltipFlag flag)
    {
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Supported wildcard patterns:", SwiftTextUtils.AQUA)));
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("*", SwiftTextUtils.ORANGE) + SwiftTextUtils.color(" - zero or more characters", SwiftTextUtils.AQUA)));
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("?", SwiftTextUtils.ORANGE) + SwiftTextUtils.color(" - exactly one character", SwiftTextUtils.AQUA)));
        tooltip.add(new StringTextComponent(SwiftTextUtils.color("Current filters: " + WildcardFilterUpgradeDataCache.numFilters(stack), SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static final String REGISTRY_NAME = "wildcard_filter_upgrade";
}
