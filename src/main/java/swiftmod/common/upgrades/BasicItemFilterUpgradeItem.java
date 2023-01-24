package swiftmod.common.upgrades;

import java.util.List;

import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.Level;
import net.minecraft.nbt.Tag;
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
        super(UpgradeType.BasicItemFilterUpgrade, REGISTRY_NAME);
    }

    public Filter<ItemStack> createItemFilter(ItemStack itemStack)
    {
        if (itemStack == null)
            return new BasicItemFilter();
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return new BasicItemFilter();
        CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
        if (nbt == null)
            return new BasicItemFilter();

        BasicItemFilter filter = new BasicItemFilter();
        filter.whiteListState = WhiteListState.read(nbt);
        filter.matchCount = nbt.getBoolean(BasicItemFilterUpgradeDataCache.TAG_MATCH_COUNT);
        filter.matchDamage = nbt.getBoolean(BasicItemFilterUpgradeDataCache.TAG_MATCH_DAMAGE);
        filter.matchMod = nbt.getBoolean(BasicItemFilterUpgradeDataCache.TAG_MATCH_MOD);
        filter.matchNBT = nbt.getBoolean(BasicItemFilterUpgradeDataCache.TAG_MATCH_NBT);
        filter.matchOreDictionary = nbt.getBoolean(BasicItemFilterUpgradeDataCache.TAG_MATCH_ORE_DICTIONARY);

        ListTag filterNBT = nbt.getList(BasicItemFilterUpgradeDataCache.TAG_SLOTS, Tag.TAG_COMPOUND);
        if (filterNBT == null)
            return filter;

        for (int i = 0; i < filterNBT.size(); ++i)
        {
            CompoundTag slotNBT = filterNBT.getCompound(i);
            BigItemStack stack = new BigItemStack(slotNBT);
            if (!stack.isEmpty())
                filter.filterStacks.add(stack);
        }

        return filter;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                BasicItemFilterUpgradeContainer::createContainerServerSide, BasicItemFilterUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(new TextComponent(SwiftTextUtils.color("Filters items.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        BasicItemFilterUpgradeDataCache cache = new BasicItemFilterUpgradeDataCache(stack);
        tooltip.add(new TextComponent(SwiftTextUtils.color(cache.getWhiteListState() == WhiteListState.WhiteList ? "Whitelist" : "Blacklist", SwiftTextUtils.AQUA)));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Match count: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchCount())));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Match damage: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchDamage())));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Match mod: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchMod())));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Match NBT: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchNBT())));
        tooltip.add(new TextComponent(SwiftTextUtils.color("Match ore dict: ", SwiftTextUtils.AQUA) + SwiftTextUtils.colorBoolean(cache.getMatchOreDictionary())));
        List<BigItemStack> filters = cache.getFilters();
        int count = 0;
        for (int i = 0; i < filters.size(); ++i)
            if (!filters.get(i).isEmpty())
                count++;
        tooltip.add(new TextComponent(SwiftTextUtils.color("Filters: " + count, SwiftTextUtils.AQUA)));
    }

    @Override
    public boolean hasShiftInformation()
    {
        return true;
    }

    public static final int NUM_SLOTS = 18;

    public static final String REGISTRY_NAME = "basic_item_filter_upgrade";
}
