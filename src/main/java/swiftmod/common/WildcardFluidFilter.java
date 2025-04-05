package swiftmod.common;

import java.util.Collection;
import java.util.regex.Pattern;

import net.minecraft.core.Holder.Reference;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;

public class WildcardFluidFilter extends WildcardFilter<FluidStack, Fluid>
{
    public WildcardFluidFilter()
    {
        super();
    }

    public WildcardFluidFilter(ItemStack itemStack)
    {
        super(itemStack);
    }

    public WildcardFluidFilter(String filter)
    {
        super(filter);
    }

    public WildcardFluidFilter(Pattern regex)
    {
        super(regex);
    }

    public WildcardFluidFilter(Collection<String> filters)
    {
        super(filters);
    }

    public FilterMatchResult<Filter<FluidStack>> filter(FluidStack stack)
    {
        return filter(stack, false);
    }

    @SuppressWarnings("deprecation")
    public FilterMatchResult<Filter<FluidStack>> filter(FluidStack stack, boolean reduceFilter)
    {
    	if (stack.isEmpty())
            return new FilterMatchResult<Filter<FluidStack>>(this, false);

    	Reference<Fluid> registryRef = stack.getFluid().builtInRegistryHolder();
        FilterMatchResult<Filter<FluidStack>> result = filter(registryRef.tags(), reduceFilter);
        if (result.matches)
        	return result;
        else
        	return filter(registryRef.key(), reduceFilter);
    }

    protected WildcardFluidFilter createReducedFilter(Pattern regex)
    {
        return new WildcardFluidFilter(regex);
    }
}
