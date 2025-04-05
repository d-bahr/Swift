package swiftmod.common.upgrades;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import swiftmod.common.ItemStackDataCache;
import swiftmod.common.SwiftDataComponents;

public class WildcardFilterUpgradeDataCache extends ItemStackDataCache
{
    public WildcardFilterUpgradeDataCache()
    {
        super();
    }

    public WildcardFilterUpgradeDataCache(ItemStack itemStack)
    {
        super(itemStack);
    }

    public List<String> getFilters()
    {
        return getFilters(itemStack);
    }

    public static List<String> getFilters(ItemStack itemStack)
    {
        if (itemStack == null || itemStack.isEmpty())
            return new ArrayList<String>();
        return itemStack.getOrDefault(SwiftDataComponents.WILDCARD_LIST_DATA_COMPONENT, new ArrayList<String>());
    }

    public int numFilters()
    {
        return numFilters(itemStack);
    }

    public static int numFilters(ItemStack itemStack)
    {
        return getFilters(itemStack).size();
    }

    public void removeFilter(String filter)
    {
        removeFilter(filter, itemStack);
    }
    
    public static void removeFilter(String filter, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        {
        	itemStack.update(SwiftDataComponents.WILDCARD_LIST_DATA_COMPONENT, new ArrayList<String>(), (f) -> 
        	{
        		f.remove(filter);
        		return f;
        	});
        }
    }
    
    public void removeFilter(int index)
    {
        removeFilter(index, itemStack);
    }

    public static void removeFilter(int index, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        {
        	itemStack.update(SwiftDataComponents.WILDCARD_LIST_DATA_COMPONENT, new ArrayList<String>(), (f) -> 
        	{
        		if (index >= 0 && index < f.size())
        			f.remove(index);
        		return f;
        	});
        }
    }

    public void addFilter(String filter)
    {
        addFilter(filter, itemStack);
    }

    public static void addFilter(String filter, ItemStack itemStack)
    {
        if (!filter.isEmpty() && itemStack != null && !itemStack.isEmpty())
        {
        	itemStack.update(SwiftDataComponents.WILDCARD_LIST_DATA_COMPONENT, new ArrayList<String>(), (f) -> 
        	{
        		// Prevent duplicates.
        		if (!f.contains(filter))
        			f.add(filter);
        		return f;
        	});
        }
    }

    public void clearAllFilters()
    {
        clearAllFilters(itemStack);
    }

    public static void clearAllFilters(ItemStack itemStack)
    {
    	itemStack.remove(SwiftDataComponents.WILDCARD_LIST_DATA_COMPONENT);
    }
}
