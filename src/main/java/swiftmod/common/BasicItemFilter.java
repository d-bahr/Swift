package swiftmod.common;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Stream;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BasicItemFilter implements Filter<ItemStack>
{
    public BasicItemFilter()
    {
        super();
        filterStacks = new ArrayList<BigItemStack>();
        whiteListState = WhiteListState.WhiteList;
        matchCount = false;
        matchDamage = false;
        matchMod = false;
        matchNBT = false;
        matchOreDictionary = false;
    }

    public BasicItemFilter(BigItemStack f, WhiteListState w, boolean c, boolean d, boolean m, boolean n, boolean o)
    {
        super();
        filterStacks = new ArrayList<BigItemStack>();
        whiteListState = w;
        filterStacks.add(f);
        matchCount = c;
        matchDamage = d;
        matchMod = m;
        matchNBT = n;
        matchOreDictionary = o;
    }

    public BasicItemFilter(ArrayList<BigItemStack> f, WhiteListState w, boolean c, boolean d, boolean m, boolean n, boolean o)
    {
        super();
        whiteListState = w;
        filterStacks = f;
        matchCount = c;
        matchDamage = d;
        matchMod = m;
        matchNBT = n;
        matchOreDictionary = o;
    }

    public boolean getMatchCount()
    {
        return matchCount;
    }

    public FilterMatchResult<Filter<ItemStack>> filter(ItemStack itemStack)
    {
        return filter(itemStack, false);
    }

    public FilterMatchResult<Filter<ItemStack>> filter(ItemStack itemStack, boolean reduceFilter)
    {
        boolean hasAnyFilter = false;

        for (int i = 0; i < filterStacks.size(); ++i)
        {
            BigItemStack filterStack = filterStacks.get(i);
            if (filterStack.isEmpty())
                continue;

            hasAnyFilter = true;

            FilterMatchResult<Filter<ItemStack>> result = filter(itemStack, filterStack, reduceFilter);
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

    private FilterMatchResult<Filter<ItemStack>> filter(ItemStack itemStack, BigItemStack filterItemStack, boolean reduceFilter)
    {
        if (itemStack.isEmpty())
            return createReturnValue(false);

        ItemStack filterStack = filterItemStack.getItemStack();
        if (itemStack.getItem() != filterStack.getItem())
        {
            if (matchMod && ForgeRegistries.ITEMS.getKey(itemStack.getItem()).getNamespace() ==
            		ForgeRegistries.ITEMS.getKey(filterStack.getItem()).getNamespace())
            {
                return createReturnValue(reduceFilter, filterItemStack);
            }

            if (matchOreDictionary)
            {
            	Stream<TagKey<Item>> filterTags = filterStack.getTags();
            	Iterator<TagKey<Item>> iter = filterTags.iterator();
            	while (iter.hasNext())
            	{
            		TagKey<Item> tag = iter.next();
            		if (itemStack.is(tag))
            			return createReturnValue(reduceFilter, filterItemStack);
            	}
            }

            return createReturnValue(false);
        }

        if (matchDamage && itemStack.isDamageableItem() && itemStack.getDamageValue() != filterStack.getDamageValue())
        {
            return createReturnValue(false);
        }

        if (matchNBT && !SwiftUtils.itemTagsMatch(filterStack, itemStack))
        {
            return createReturnValue(false);
        }

        return createReturnValue(reduceFilter, filterItemStack);
    }

    private BasicItemFilter createReturnFilter(boolean reduceFilter, BigItemStack filter)
    {
        if (reduceFilter)
            return new BasicItemFilter(filter, whiteListState, matchCount, matchDamage, matchMod, matchNBT, matchOreDictionary);
        else
            return this;
    }

    private FilterMatchResult<Filter<ItemStack>> createReturnValue(boolean reduceFilter, BigItemStack filter)
    {
        return new FilterMatchResult<Filter<ItemStack>>(createReturnFilter(reduceFilter, filter), filter.getCount());
    }

    private FilterMatchResult<Filter<ItemStack>> createReturnValue(boolean matched)
    {
        return new FilterMatchResult<Filter<ItemStack>>(this, matched);
    }

    public ArrayList<BigItemStack> filterStacks;
    public WhiteListState whiteListState;
    public boolean matchCount;
    public boolean matchDamage;
    public boolean matchMod;
    public boolean matchNBT;
    public boolean matchOreDictionary;
}
