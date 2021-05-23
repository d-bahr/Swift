package swiftmod.common;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class ItemStackDataCache implements DataCache
{
    public ItemStackDataCache()
    {
        itemStack = ItemStack.EMPTY;
    }

    public ItemStackDataCache(ItemStack itemStack)
    {
        this.itemStack = itemStack;
    }

    public CompoundNBT write(CompoundNBT nbt)
    {
        CompoundNBT child = itemStack.serializeNBT();
        nbt.put(SwiftUtils.tagName("cacheItemStack"), child);
        return nbt;
    }

    public void read(CompoundNBT nbt)
    {
        CompoundNBT child = nbt.getCompound(SwiftUtils.tagName("cacheItemStack"));
        itemStack = ItemStack.of(child);
    }

    public PacketBuffer write(PacketBuffer buffer)
    {
        return buffer.writeItemStack(itemStack, false);
    }

    public void read(PacketBuffer buffer)
    {
        itemStack = buffer.readItem();
    }

    public ItemStack itemStack;
}
