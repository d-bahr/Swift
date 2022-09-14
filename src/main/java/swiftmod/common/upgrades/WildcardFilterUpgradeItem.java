package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
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
        CompoundTag nbt = stack.getTagElement(FilterUpgradeItem.NBT_TAG);
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
        CompoundTag nbt = stack.getTagElement(FilterUpgradeItem.NBT_TAG);
        if (nbt == null)
            return new WildcardFluidFilter();

        List<String> filters = WildcardFilterUpgradeDataCache.getFilters(stack);
        return new WildcardFluidFilter(filters);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                WildcardFilterUpgradeContainer::createContainerServerSide, WildcardFilterUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(new TextComponent(SwiftTextUtils.color("Filters items or fluids using ore dictionary tags.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(new TextComponent(SwiftTextUtils.color("Supported wildcard patterns:", SwiftTextUtils.AQUA)));
        tooltip.add(new TextComponent(SwiftTextUtils.color("*", SwiftTextUtils.ORANGE) + SwiftTextUtils.color(" - zero or more characters", SwiftTextUtils.AQUA)));
        tooltip.add(new TextComponent(SwiftTextUtils.color("?", SwiftTextUtils.ORANGE) + SwiftTextUtils.color(" - exactly one character", SwiftTextUtils.AQUA)));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Current filters: " + WildcardFilterUpgradeDataCache.numFilters(stack), SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static final String REGISTRY_NAME = "wildcard_filter_upgrade";
}
