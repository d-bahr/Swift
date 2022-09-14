package swiftmod.common;

import java.util.Collection;
import java.util.regex.Pattern;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder.Reference;

public class WildcardItemFilter extends WildcardFilter<ItemStack, Item>
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

    @SuppressWarnings("deprecation")
    public FilterMatchResult<Filter<ItemStack>> filter(ItemStack stack, boolean reduceFilter)
    {
    	if (stack.isEmpty())
            return new FilterMatchResult<Filter<ItemStack>>(this, false);
    	
    	Reference<Item> registryRef = stack.getItem().builtInRegistryHolder();
        FilterMatchResult<Filter<ItemStack>> result = filter(registryRef.tags(), reduceFilter);
        if (result.matches)
        	return result;
        else
        	return filter(registryRef.key(), reduceFilter);
    }

    protected WildcardItemFilter createReducedFilter(Pattern regex)
    {
        return new WildcardItemFilter(regex);
    }
}
