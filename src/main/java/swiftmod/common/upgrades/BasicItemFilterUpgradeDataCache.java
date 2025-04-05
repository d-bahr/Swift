package swiftmod.common.upgrades;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.world.item.ItemStack;
import swiftmod.common.BigItemStack;
import swiftmod.common.ItemStackDataCache;
import swiftmod.common.SwiftDataComponents;
import swiftmod.common.WhiteListState;

public class BasicItemFilterUpgradeDataCache extends ItemStackDataCache
{
    public BasicItemFilterUpgradeDataCache()
    {
        super();
    }

    public BasicItemFilterUpgradeDataCache(ItemStack itemStack)
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

    public boolean getMatchDamage()
    {
        if (itemStack == null || itemStack.isEmpty())
            return false;
        return itemStack.getOrDefault(SwiftDataComponents.MATCH_DAMAGE_DATA_COMPONENT, false);
    }

    public void setMatchDamage(boolean match)
    {
        setMatchDamage(match, itemStack);
    }

    public static void setMatchDamage(boolean match, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        	itemStack.set(SwiftDataComponents.MATCH_DAMAGE_DATA_COMPONENT, match);
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

    public boolean getMatchNBT()
    {
        if (itemStack == null || itemStack.isEmpty())
            return false;
        return itemStack.getOrDefault(SwiftDataComponents.MATCH_NBT_DATA_COMPONENT, false);
    }

    public void setMatchNBT(boolean match)
    {
        setMatchNBT(match, itemStack);
    }

    public static void setMatchNBT(boolean match, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        	itemStack.set(SwiftDataComponents.MATCH_NBT_DATA_COMPONENT, match);
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
    
    private static ArrayList<BigItemStack> createNewFilters()
    {
        ArrayList<BigItemStack> filters = new ArrayList<BigItemStack>(BasicItemFilterUpgradeItem.NUM_SLOTS);
        for (int i = 0; i < BasicItemFilterUpgradeItem.NUM_SLOTS; ++i)
            filters.add(new BigItemStack());
        return filters;
    }

    public List<BigItemStack> getFilters()
    {
        if (itemStack == null || itemStack.isEmpty())
            return createNewFilters();
        List<BigItemStack> filters = itemStack.get(SwiftDataComponents.BIG_ITEM_STACK_LIST_DATA_COMPONENT);
        if (filters != null)
        	return filters;
        else
        	return createNewFilters();
    }

    public void setFilterSlot(int slot, ItemStack itemStack, int quantity)
    {
        setFilterSlot(slot, new BigItemStack(itemStack, quantity));
    }

    public void setFilterSlot(int slot, BigItemStack filter)
    {
        setFilterSlot(slot, filter, itemStack);
    }

    public static void setFilterSlot(int slot, BigItemStack filter, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        {
        	List<BigItemStack> itemStacks = itemStack.get(SwiftDataComponents.BIG_ITEM_STACK_LIST_DATA_COMPONENT);
        	List<BigItemStack> newItemStacks;
        	if (itemStacks == null)
        		newItemStacks = createNewFilters();
        	else
        		newItemStacks = new ArrayList<BigItemStack>(itemStacks); // Need to create a copy because data components are immutable.
        	newItemStacks.set(slot, filter);
        	itemStack.set(SwiftDataComponents.BIG_ITEM_STACK_LIST_DATA_COMPONENT, newItemStacks);
        }
    }

    public void clearAllFilters()
    {
        clearAllFilters(itemStack);
    }

    public static void clearAllFilters(ItemStack itemStack)
    {
    	itemStack.remove(SwiftDataComponents.BIG_ITEM_STACK_LIST_DATA_COMPONENT);
    }
}
