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
import net.minecraft.world.level.Level;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;
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
        if (itemStack == null)
            return new BasicFluidFilter();
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return new BasicFluidFilter();
        CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
        if (nbt == null)
            return new BasicFluidFilter();

        BasicFluidFilter filter = new BasicFluidFilter();
        filter.whiteListState = WhiteListState.read(nbt);
        filter.matchCount = nbt.getBoolean(BasicFluidFilterUpgradeDataCache.TAG_MATCH_COUNT);
        filter.matchMod = nbt.getBoolean(BasicFluidFilterUpgradeDataCache.TAG_MATCH_MOD);
        filter.matchOreDictionary = nbt.getBoolean(BasicFluidFilterUpgradeDataCache.TAG_MATCH_ORE_DICTIONARY);

        ListTag filterNBT = nbt.getList(BasicFluidFilterUpgradeDataCache.TAG_SLOTS, Tag.TAG_COMPOUND);
        if (filterNBT == null)
            return filter;

        for (int i = 0; i < filterNBT.size(); ++i)
        {
            CompoundTag slotNBT = filterNBT.getCompound(i);
            FluidStack stack = FluidStack.loadFluidStackFromNBT(slotNBT);
            if (!stack.isEmpty())
                filter.filterStacks.add(stack);
        }

        return filter;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand)
    {
        return ItemContainerProvider.openContainerGui(world, player, hand,
                BasicFluidFilterUpgradeContainer::createContainerServerSide, BasicFluidFilterUpgradeContainer::encode);
    }

    @Override
    public void addStandardInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
    {
        tooltip.add(Component.literal(SwiftTextUtils.color("Filters fluids.", SwiftTextUtils.AQUA)));
    }

    @Override
    public void addShiftInformation(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag flag)
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
