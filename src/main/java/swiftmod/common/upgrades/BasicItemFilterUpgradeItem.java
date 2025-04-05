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
import swiftmod.common.BasicItemFilter;
import swiftmod.common.BigItemStack;
import swiftmod.common.Filter;
import swiftmod.common.ItemContainerProvider;
import swiftmod.common.SwiftTextUtils;
import swiftmod.common.WhiteListState;

public class BasicItemFilterUpgradeItem extends FilterUpgradeItem implements IItemFilterUpgradeItem
{
    public BasicItemFilterUpgradeItem()
    {
        super(UpgradeType.BasicItemFilterUpgrade);
    }

    public Filter<ItemStack> createItemFilter(ItemStack itemStack)
    {
    	return new BasicItemFilter(itemStack);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                BasicItemFilterUpgradeContainer::createContainerServerSide, BasicItemFilterUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Filters items.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        BasicItemFilterUpgradeDataCache cache = new BasicItemFilterUpgradeDataCache(stack);
        tooltip.add(Component.literal(SwiftTextUtils.color(cache.getWhiteListState() == WhiteListState.WhiteList ? "Whitelist" : "Blacklist", SwiftTextUtils.AQUA)));
        tooltip.add(Component.literal(SwiftTextUtils.color("Match count: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchCount())));
        tooltip.add(Component.literal(SwiftTextUtils.color("Match damage: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchDamage())));
        tooltip.add(Component.literal(SwiftTextUtils.color("Match mod: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchMod())));
        tooltip.add(Component.literal(SwiftTextUtils.color("Match NBT: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchNBT())));
        tooltip.add(Component.literal(SwiftTextUtils.color("Match ore dict: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchOreDictionary())));
        List<BigItemStack> filters = cache.getFilters();
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

    public static final String REGISTRY_NAME = "basic_item_filter_upgrade";
}
