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
import swiftmod.common.BasicFluidFilter;
import swiftmod.common.Filter;
import swiftmod.common.ItemContainerProvider;
import swiftmod.common.SwiftTextUtils;
import swiftmod.common.WhiteListState;

public class BasicFluidFilterUpgradeItem extends FilterUpgradeItem implements IFluidFilterUpgradeItem
{
    public BasicFluidFilterUpgradeItem()
    {
        super(UpgradeType.BasicFluidFilterUpgrade);
    }

    public Filter<FluidStack> createFluidFilter(ItemStack itemStack)
    {
    	return new BasicFluidFilter(itemStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                BasicFluidFilterUpgradeContainer::createContainerServerSide, BasicFluidFilterUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Filters fluids.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        BasicFluidFilterUpgradeDataCache cache = new BasicFluidFilterUpgradeDataCache(stack);
        tooltip.add(Component.literal(SwiftTextUtils.color(cache.getWhiteListState() == WhiteListState.WhiteList ? "Whitelist" : "Blacklist", SwiftTextUtils.AQUA)));
        tooltip.add(Component.literal(SwiftTextUtils.color("Match amount: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchCount())));
        tooltip.add(Component.literal(SwiftTextUtils.color("Match mod: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchMod())));
        tooltip.add(Component.literal(SwiftTextUtils.color("Match ore dict: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchOreDictionary())));
        List<FluidStack> filters = cache.getFilters();
        int count = 0;
        for (int i = 0; i < filters.size(); ++i)
            if (!filters.get(i).isEmpty())
                count++;
        tooltip.add(Component.literal(SwiftTextUtils.color("Filters: " + count, SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static final int NUM_SLOTS = 18;

    public static final String REGISTRY_NAME = "basic_fluid_filter_upgrade";
}
