package swiftmod.common;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class WildcardFluidFilter extends WildcardFilter<FluidStack>
{
    public WildcardFluidFilter()
    {
        super();
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

    public FilterMatchResult<Filter<FluidStack>> filter(FluidStack stack, boolean reduceFilter)
    {
        Set<ResourceLocation> tags = stack.getFluid().getTags();
        return filter(tags, reduceFilter);
    }

    protected WildcardFluidFilter createReducedFilter(Pattern regex)
    {
        return new WildcardFluidFilter(regex);
    }
}
