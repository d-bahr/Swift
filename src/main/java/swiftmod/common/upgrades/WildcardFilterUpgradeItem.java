package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import swiftmod.common.Filter;
import swiftmod.common.ItemContainerProvider;
import swiftmod.common.SwiftTextUtils;
import swiftmod.common.WildcardFluidFilter;
import swiftmod.common.WildcardItemFilter;

public class WildcardFilterUpgradeItem extends FilterUpgradeItem implements IItemFilterUpgradeItem, IFluidFilterUpgradeItem
{
    public WildcardFilterUpgradeItem()
    {
        super(UpgradeType.WildcardFilterUpgrade);
    }

    public Filter<ItemStack> createItemFilter(ItemStack stack)
    {
    	return new WildcardItemFilter(stack);
    }

    public Filter<FluidStack> createFluidFilter(ItemStack stack)
    {
    	return new WildcardFluidFilter(stack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                WildcardFilterUpgradeContainer::createContainerServerSide, WildcardFilterUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Filters items or fluids using ore dictionary tags.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Supported wildcard patterns:", SwiftTextUtils.AQUA)));
        tooltip.add(Component.literal(SwiftTextUtils.color("*", SwiftTextUtils.ORANGE) + SwiftTextUtils.color(" - zero or more characters", SwiftTextUtils.AQUA)));
        tooltip.add(Component.literal(SwiftTextUtils.color("?", SwiftTextUtils.ORANGE) + SwiftTextUtils.color(" - exactly one character", SwiftTextUtils.AQUA)));
        tooltip.add(Component.literal(SwiftTextUtils.color("Current filters: " + WildcardFilterUpgradeDataCache.numFilters(stack), SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static final String REGISTRY_NAME = "wildcard_filter_upgrade";
}
