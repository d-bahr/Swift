package swiftmod.common.upgrades;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.Constants;
import swiftmod.common.ItemStackDataCache;
import swiftmod.common.SwiftUtils;

public class SideUpgradeDataCache extends ItemStackDataCache
{
    public SideUpgradeDataCache()
    {
        super();
    }

    public SideUpgradeDataCache(ItemStack itemStack)
    {
        super(itemStack);
    }

    public void setStates(byte[] states)
    {
        setStates(states, itemStack);
    }

    public static void setStates(byte[] states, ItemStack itemStack)
    {
        if (itemStack != null && !itemStack.isEmpty())
        {
            // TODO: Cleanup? Don't need multiple tag names here.
            CompoundNBT nbt = itemStack.getOrCreateTagElement(SideUpgradeItem.NBT_TAG);
            nbt.putByteArray(TAG_SIDES, states);
        }
    }

    public byte[] getStates()
    {
        return getStates(itemStack);
    }

    public static byte[] getStates(ItemStack itemStack)
    {
        if (itemStack == null)
            return new byte[0];
        if (itemStack.isEmpty() || !itemStack.hasTag())
            return new byte[0];
        CompoundNBT nbt = itemStack.getTagElement(SideUpgradeItem.NBT_TAG);
        if (nbt == null)
            return new byte[0];
        if (nbt.getTagType(TAG_SIDES) == Constants.NBT.TAG_BYTE_ARRAY)
        {
            return nbt.getByteArray(TAG_SIDES);
        }
        else
        {
            // For backward compatibility only.
            boolean[] b = SwiftUtils.getBooleanArray(nbt, TAG_SIDES);
            byte[] ret = new byte[b.length];
            for (int i = 0; i < b.length; ++i)
                ret[i] = b[i] ? (byte)1 : (byte)0;
            return ret;
        }
    }

    // TODO: Move this to SideUpgradeItem.
    public static final String TAG_SIDES = SwiftUtils.tagName("sides");
}
