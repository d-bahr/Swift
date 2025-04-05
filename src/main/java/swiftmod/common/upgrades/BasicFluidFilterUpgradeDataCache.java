package swiftmod.common.upgrades;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import swiftmod.common.ImmutableFluidStack;
import swiftmod.common.ItemStackDataCache;
import swiftmod.common.SwiftDataComponents;
import swiftmod.common.WhiteListState;

public class BasicFluidFilterUpgradeDataCache extends ItemStackDataCache
{
    public BasicFluidFilterUpgradeDataCache()
    {
        super();
    }

    public BasicFluidFilterUpgradeDataCache(ItemStack itemStack)
    {
        super(itemStack);
    }

    public WhiteListState getWhiteListState()
    {
        if (itemStack == null || itemStack.isEmpty())
            return WhiteListState.WhiteList;
        return itemStack.getOrDefault(SwiftDataComponents.WHITELIST_DATA_COMPONENT, WhiteListState.WhiteList);
    }

    public void setWhiteListState(WhiteListState state)
    {
        setWhiteListState(state, itemStack);
    }
    
    public static void setWhiteListState(WhiteListState state, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        	itemStack.set(SwiftDataComponents.WHITELIST_DATA_COMPONENT, state);
    }

    public boolean getMatchCount()
    {
        if (itemStack == null || itemStack.isEmpty())
            return false;
        return itemStack.getOrDefault(SwiftDataComponents.MATCH_COUNT_DATA_COMPONENT, false);
    }

    public void setMatchCount(boolean match)
    {
        setMatchCount(match, itemStack);
    }

    public static void setMatchCount(boolean match, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        	itemStack.set(SwiftDataComponents.MATCH_COUNT_DATA_COMPONENT, match);
    }

    public boolean getMatchMod()
    {
        if (itemStack == null || itemStack.isEmpty())
            return false;
        return itemStack.getOrDefault(SwiftDataComponents.MATCH_MOD_DATA_COMPONENT, false);
    }

    public void setMatchMod(boolean match)
    {
        setMatchMod(match, itemStack);
    }

    public static void setMatchMod(boolean match, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        	itemStack.set(SwiftDataComponents.MATCH_MOD_DATA_COMPONENT, match);
    }

    public boolean getMatchOreDictionary()
    {
        if (itemStack == null || itemStack.isEmpty())
            return false;
        return itemStack.getOrDefault(SwiftDataComponents.MATCH_ORE_DICT_DATA_COMPONENT, false);
    }

    public void setMatchOreDictionary(boolean match)
    {
        setMatchOreDictionary(match, itemStack);
    }

    public static void setMatchOreDictionary(boolean match, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        	itemStack.set(SwiftDataComponents.MATCH_ORE_DICT_DATA_COMPONENT, match);
    }
    
    private static List<FluidStack> createNewFilters()
    {
        ArrayList<FluidStack> filters = new ArrayList<FluidStack>(BasicFluidFilterUpgradeItem.NUM_SLOTS);
        for (int i = 0; i < BasicItemFilterUpgradeItem.NUM_SLOTS; ++i)
            filters.add(FluidStack.EMPTY);
        return filters;
    }
    
    private static List<ImmutableFluidStack> createNewImmutableFilters()
    {
        ArrayList<ImmutableFluidStack> filters = new ArrayList<ImmutableFluidStack>(BasicFluidFilterUpgradeItem.NUM_SLOTS);
        for (int i = 0; i < BasicItemFilterUpgradeItem.NUM_SLOTS; ++i)
            filters.add(new ImmutableFluidStack(FluidStack.EMPTY));
        return filters;
    }

    public List<FluidStack> getFilters()
    {
        if (itemStack == null || itemStack.isEmpty())
            return createNewFilters();
        List<ImmutableFluidStack> fluidStacks = itemStack.get(SwiftDataComponents.FLUID_STACK_LIST_DATA_COMPONENT);
        if (fluidStacks == null)
        	return createNewFilters();
        List<FluidStack> ret = new ArrayList<FluidStack>(fluidStacks.size());
        for (ImmutableFluidStack fluidStack : fluidStacks)
        	ret.add(fluidStack.fluidStack());
        return ret;
    }

    public void setFilterSlot(int slot, FluidStack filter)
    {
        setFilterSlot(slot, filter, itemStack);
    }

    public static void setFilterSlot(int slot, FluidStack filter, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        {
        	List<ImmutableFluidStack> fluidStacks = itemStack.get(SwiftDataComponents.FLUID_STACK_LIST_DATA_COMPONENT);
        	List<ImmutableFluidStack> newFluidStacks;
        	if (fluidStacks == null)
        		newFluidStacks = createNewImmutableFilters();
        	else
        		newFluidStacks = new ArrayList<ImmutableFluidStack>(fluidStacks); // Need to create a copy because data components are immutable.
        	newFluidStacks.set(slot, new ImmutableFluidStack(filter));
        	itemStack.set(SwiftDataComponents.FLUID_STACK_LIST_DATA_COMPONENT, newFluidStacks);
        }
    }

    public void clearAllFilters()
    {
        clearAllFilters(itemStack);
    }

    public static void clearAllFilters(ItemStack itemStack)
    {
    	itemStack.remove(SwiftDataComponents.FLUID_STACK_LIST_DATA_COMPONENT);
    }
}
