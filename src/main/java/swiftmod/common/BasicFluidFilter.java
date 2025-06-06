package swiftmod.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class BasicFluidFilter implements Filter<FluidStack>
{
    public BasicFluidFilter()
    {
        super();
        filterStacks = new ArrayList<FluidStack>();
        whiteListState = WhiteListState.WhiteList;
        matchCount = false;
        matchMod = false;
        matchOreDictionary = false;
    }

    public BasicFluidFilter(ItemStack itemStack)
    {
        super();
        
        if (itemStack == null || itemStack.isEmpty())
        {
            filterStacks = new ArrayList<FluidStack>();
            whiteListState = WhiteListState.WhiteList;
            matchCount = false;
            matchMod = false;
            matchOreDictionary = false;
        }
        else
        {
            List<ImmutableFluidStack> fluidStacks = itemStack.get(SwiftDataComponents.FLUID_STACK_LIST_DATA_COMPONENT);
            if (fluidStacks != null)
            {
	            filterStacks = new ArrayList<FluidStack>(fluidStacks.size());
	            for (ImmutableFluidStack fluidStack : fluidStacks)
	            	filterStacks.add(fluidStack.fluidStack());
            }
            else
            {
                filterStacks = new ArrayList<FluidStack>();
            }
	        
	        whiteListState = itemStack.get(SwiftDataComponents.WHITELIST_DATA_COMPONENT);
	        if (whiteListState == null)
	        	whiteListState = WhiteListState.WhiteList;
	        
	        Boolean match = itemStack.get(SwiftDataComponents.MATCH_COUNT_DATA_COMPONENT);
	        matchCount = match != null ? match : false;
	        
	        match = itemStack.get(SwiftDataComponents.MATCH_MOD_DATA_COMPONENT);
	        matchMod = match != null ? match : false;
	        
	        match = itemStack.get(SwiftDataComponents.MATCH_ORE_DICT_DATA_COMPONENT);
	        matchOreDictionary = match != null ? match : false;
        }
    }

    public BasicFluidFilter(FluidStack f, WhiteListState w, boolean c, boolean m, boolean o)
    {
        super();
        filterStacks = new ArrayList<FluidStack>();
        whiteListState = w;
        filterStacks.add(f);
        matchCount = c;
        matchMod = m;
        matchOreDictionary = o;
    }

    public BasicFluidFilter(ArrayList<FluidStack> f, WhiteListState w, boolean c, boolean m, boolean o)
    {
        super();
        whiteListState = w;
        filterStacks = f;
        matchCount = c;
        matchMod = m;
        matchOreDictionary = o;
    }

    public boolean getMatchCount()
    {
        return matchCount;
    }

    public FilterMatchResult<Filter<FluidStack>> filter(FluidStack fluidStack)
    {
        return filter(fluidStack, false);
    }

    public FilterMatchResult<Filter<FluidStack>> filter(FluidStack fluidStack, boolean reduceFilter)
    {
        boolean hasAnyFilter = false;

        for (int i = 0; i < filterStacks.size(); ++i)
        {
            FluidStack filterStack = filterStacks.get(i);
            if (filterStack.isEmpty())
                continue;

            hasAnyFilter = true;

            FilterMatchResult<Filter<FluidStack>> result = filter(fluidStack, filterStack, reduceFilter);
            if (whiteListState == WhiteListState.BlackList)
            {
                result.matches = !result.matches;

                if (result.matches)
                {
                    // Can't match count with a blacklist...
                    result.matchCount = 0;
                    return result;
                }
            }
            else if (result.matches)
            {
                return result;
            }
        }

        return createReturnValue(!hasAnyFilter);
    }

    @SuppressWarnings("deprecation")
    private FilterMatchResult<Filter<FluidStack>> filter(FluidStack fluidStack, FluidStack filterStack, boolean reduceFilter)
    {
        if (fluidStack.isEmpty())
            return createReturnValue(false);

        if (fluidStack.getFluid() != filterStack.getFluid())
        {
            if (matchMod && NeoForgeRegistries.FLUID_TYPES.getKey(fluidStack.getFluidType()).getNamespace() ==
            		NeoForgeRegistries.FLUID_TYPES.getKey(filterStack.getFluidType()).getNamespace())
            {
                return createReturnValue(reduceFilter, filterStack);
            }

            if (matchOreDictionary)
            {
            	Stream<TagKey<Fluid>> filterTags = filterStack.getFluid().builtInRegistryHolder().tags();
            	Iterator<TagKey<Fluid>> iter = filterTags.iterator();
            	while (iter.hasNext())
            	{
            		TagKey<Fluid> tag = iter.next();
            		if (fluidStack.getFluid().is(tag))
            			return createReturnValue(reduceFilter, filterStack);
            	}
            }

            return createReturnValue(false);
        }

        return createReturnValue(reduceFilter, filterStack);
    }

    private BasicFluidFilter createReturnFilter(boolean reduceFilter, FluidStack filter)
    {
        if (reduceFilter)
            return new BasicFluidFilter(filter, whiteListState, matchCount, matchMod, matchOreDictionary);
        else
            return this;
    }

    private FilterMatchResult<Filter<FluidStack>> createReturnValue(boolean reduceFilter, FluidStack filter)
    {
        return new FilterMatchResult<Filter<FluidStack>>(createReturnFilter(reduceFilter, filter), filter.getAmount());
    }

    private FilterMatchResult<Filter<FluidStack>> createReturnValue(boolean matched)
    {
        return new FilterMatchResult<Filter<FluidStack>>(this, matched);
    }

    public List<FluidStack> filterStacks;
    public WhiteListState whiteListState;
    public boolean matchCount;
    public boolean matchMod;
    public boolean matchOreDictionary;
}
