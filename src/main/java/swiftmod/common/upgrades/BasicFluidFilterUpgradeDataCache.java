package swiftmod.common.upgrades;

import java.util.ArrayList;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;
import swiftmod.common.ItemStackDataCache;
import swiftmod.common.SwiftUtils;
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
        if (itemStack == null)
            return WhiteListState.WhiteList;
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return WhiteListState.WhiteList;
        CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
        return nbt != null ? WhiteListState.read(nbt) : WhiteListState.WhiteList;
    }

    public void setWhiteListState(WhiteListState state)
    {
        setWhiteListState(state, itemStack);
    }
    
    public static void setWhiteListState(WhiteListState state, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        {
            CompoundTag nbt = itemStack.getOrCreateTagElement(FilterUpgradeItem.NBT_TAG);
            WhiteListState.write(nbt, state);
        }
    }

    public boolean getMatchCount()
    {
        if (itemStack == null)
            return false;
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return false;
        CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
        return nbt != null ? nbt.getBoolean(TAG_MATCH_COUNT) : false;
    }

    public void setMatchCount(boolean match)
    {
        setMatchCount(match, itemStack);
    }

    public static void setMatchCount(boolean match, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        {
            CompoundTag nbt = itemStack.getOrCreateTagElement(FilterUpgradeItem.NBT_TAG);
            nbt.putBoolean(TAG_MATCH_COUNT, match);
        }
    }

    public boolean getMatchMod()
    {
        if (itemStack == null)
            return false;
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return false;
        CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
        return nbt != null ? nbt.getBoolean(TAG_MATCH_MOD) : false;
    }

    public void setMatchMod(boolean match)
    {
        setMatchMod(match, itemStack);
    }

    public static void setMatchMod(boolean match, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        {
            CompoundTag nbt = itemStack.getOrCreateTagElement(FilterUpgradeItem.NBT_TAG);
            nbt.putBoolean(TAG_MATCH_MOD, match);
        }
    }

    public boolean getMatchOreDictionary()
    {
        if (itemStack == null)
            return false;
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return false;
        CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
        return nbt != null ? nbt.getBoolean(TAG_MATCH_ORE_DICTIONARY) : false;
    }

    public void setMatchOreDictionary(boolean match)
    {
        setMatchOreDictionary(match, itemStack);
    }

    public static void setMatchOreDictionary(boolean match, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        {
            CompoundTag nbt = itemStack.getOrCreateTagElement(FilterUpgradeItem.NBT_TAG);
            nbt.putBoolean(TAG_MATCH_ORE_DICTIONARY, match);
        }
    }
    
    private ArrayList<FluidStack> createNewFilters()
    {
        ArrayList<FluidStack> filters = new ArrayList<FluidStack>(BasicFluidFilterUpgradeItem.NUM_SLOTS);
        for (int i = 0; i < BasicItemFilterUpgradeItem.NUM_SLOTS; ++i)
            filters.add(FluidStack.EMPTY);
        return filters;
    }

    public ArrayList<FluidStack> getFilters()
    {
        if (itemStack == null)
            return createNewFilters();
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return createNewFilters();
        CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
        if (nbt == null)
            return createNewFilters();
        ListTag filterNBT = nbt.getList(TAG_SLOTS, Tag.TAG_COMPOUND);
        ArrayList<FluidStack> filters = new ArrayList<FluidStack>();
        if (filterNBT == null || filterNBT.isEmpty())
        {
            return createNewFilters();
        }
        else
        {
            for (int i = 0; i < filterNBT.size(); ++i)
            {
                CompoundTag slotNBT = filterNBT.getCompound(i);
                FluidStack stack = FluidStack.loadFluidStackFromNBT(slotNBT);
                filters.add(stack);
            }
        }
        return filters;
    }

    public void setFilterSlot(int slot, FluidStack filter)
    {
        setFilterSlot(slot, filter, itemStack);
    }

    public static void setFilterSlot(int slot, FluidStack filter, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        {
            CompoundTag nbt = itemStack.getOrCreateTagElement(FilterUpgradeItem.NBT_TAG);
            ListTag filterNBT = nbt.getList(TAG_SLOTS, Tag.TAG_COMPOUND);
            if (filterNBT == null || filterNBT.isEmpty())
            {
                filterNBT = new ListTag();
                FluidStack empty = FluidStack.EMPTY;
                for (int i = 0; i < BasicItemFilterUpgradeItem.NUM_SLOTS; ++i)
                {
                    CompoundTag slotNBT = new CompoundTag();
                    empty.writeToNBT(slotNBT);
                    filterNBT.add(slotNBT);
                }
                nbt.put(TAG_SLOTS, filterNBT);
            }

            if (slot < filterNBT.size())
            {
                CompoundTag slotNBT = new CompoundTag();
                filter.writeToNBT(slotNBT);
                filterNBT.set(slot, slotNBT);
            }
        }
    }

    public void clearAllFilters()
    {
        clearAllFilters(itemStack);
    }

    public static void clearAllFilters(ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty() && itemStack.hasTag())
        {
            CompoundTag nbt = itemStack.getTagElement(FilterUpgradeItem.NBT_TAG);
            if (nbt != null)
                nbt.remove(TAG_SLOTS);
        }
    }

    // TODO: Move this to BasicFluidFilterUpgradeItem.
    public static final String TAG_MATCH_COUNT = SwiftUtils.tagName("matchCount");
    public static final String TAG_MATCH_MOD = SwiftUtils.tagName("matchMod");
    public static final String TAG_MATCH_ORE_DICTIONARY = SwiftUtils.tagName("matchOreDictionary");
    public static final String TAG_SLOTS = SwiftUtils.tagName("slots");
}
