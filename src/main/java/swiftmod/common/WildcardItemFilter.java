package swiftmod.common;

import java.util.Collection;
import java.util.Set;
import java.util.regex.Pattern;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class WildcardItemFilter extends WildcardFilter<ItemStack>
{
    public WildcardItemFilter()
    {
        super();
    }

    public WildcardItemFilter(String filter)
    {
        super(filter);
    }

    public WildcardItemFilter(Pattern regex)
    {
        super(regex);
    }

    public WildcardItemFilter(Collection<String> filters)
    {
        super(filters);
    }

    public FilterMatchResult<Filter<ItemStack>> filter(ItemStack stack)
    {
        return filter(stack, false);
    }

    public FilterMatchResult<Filter<ItemStack>> filter(ItemStack stack, boolean reduceFilter)
    {
        Set<ResourceLocation> tags = stack.getItem().getTags();
        return filter(tags, reduceFilter);
    }

    protected WildcardItemFilter createReducedFilter(Pattern regex)
    {
        return new WildcardItemFilter(regex);
    }
}
