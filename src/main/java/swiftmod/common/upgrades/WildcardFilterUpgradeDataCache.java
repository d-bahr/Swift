package swiftmod.common.upgrades;

import java.util.ArrayList;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import swiftmod.common.ItemStackDataCache;
import swiftmod.common.SwiftUtils;

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

    public ArrayList<String> getFilters()
    {
        return getFilters(itemStack);
    }

    public static ArrayList<String> getFilters(ItemStack itemStack)
    {
        ArrayList<String> filters = new ArrayList<String>();
        if (itemStack == null)
            return filters;
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return filters;
        CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
        if (nbt == null)
            return filters;
        ListTag filterNBT = nbt.getList(TAG_FILTERS, Tag.TAG_STRING);
        if (filterNBT == null || filterNBT.isEmpty())
            return filters;
        for (int i = 0; i < filterNBT.size(); ++i)
            filters.add(filterNBT.getString(i));
        return filters;
    }

    public int numFilters()
    {
        return numFilters(itemStack);
    }

    public static int numFilters(ItemStack itemStack)
    {
        if (itemStack == null)
            return 0;
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return 0;
        CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
        if (nbt == null)
            return 0;
        ListTag filterNBT = nbt.getList(TAG_FILTERS, Tag.TAG_STRING);
        if (filterNBT == null)
            return 0;
        return filterNBT.size();
    }

    public void removeFilter(String filter)
    {
        removeFilter(filter, itemStack);
    }
    
    public static void removeFilter(String filter, ItemStack itemStack)
    {
        if (!filter.isEmpty() && itemStack != null && !itemStack.isEmpty())
        {
            CompoundTag nbt = itemStack.getOrCreateTagElement(FilterUpgradeItem.NBT_TAG);
            ListTag filterNBT = nbt.getList(TAG_FILTERS, Tag.TAG_STRING);
            if (filterNBT == null || filterNBT.isEmpty())
                return;

            for (int i = 0; i < filterNBT.size(); ++i)
            {
                if (filterNBT.getString(i).equals(filter))
                {
                    filterNBT.remove(i);
                    return;
                }
            }
        }
    }
    
    public void removeFilter(int index)
    {
        removeFilter(index, itemStack);
    }

    public static void removeFilter(int index, ItemStack itemStack)
    {
        if (index >= 0 && itemStack != null && !itemStack.isEmpty())
        {
            CompoundTag nbt = itemStack.getOrCreateTagElement(FilterUpgradeItem.NBT_TAG);
            ListTag filterNBT = nbt.getList(TAG_FILTERS, Tag.TAG_STRING);
            if (filterNBT == null || index >= filterNBT.size())
                return;

            filterNBT.remove(index);
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
            CompoundTag nbt = itemStack.getOrCreateTagElement(FilterUpgradeItem.NBT_TAG);
            ListTag filterNBT = nbt.getList(TAG_FILTERS, Tag.TAG_STRING);
            if (filterNBT == null || filterNBT.isEmpty())
            {
                filterNBT = new ListTag();
                nbt.put(TAG_FILTERS, filterNBT);
            }

            for (int i = 0; i < filterNBT.size(); ++i)
            {
                if (filterNBT.getString(i).equals(filter))
                {
                    // Already exists; don't add another.
                    return;
                }
            }

            filterNBT.add(StringTag.valueOf(filter));
        }
    }

    public void clearAllFilters()
    {
        clearAllFilters(itemStack);
    }

    public static void clearAllFilters(ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty() && !itemStack.hasTag())
        {
            CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
            if (nbt != null)
                nbt.remove(TAG_FILTERS);
        }
    }

    // TODO: Move this to WildcardFilterUpgradeItem.
    public static final String TAG_FILTERS = SwiftUtils.tagName("filters");
}
