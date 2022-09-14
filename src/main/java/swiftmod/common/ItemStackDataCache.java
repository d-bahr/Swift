package swiftmod.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

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

    public void write(CompoundTag nbt)
    {
        CompoundTag child = itemStack.serializeNBT();
        nbt.put(SwiftUtils.tagName("cacheItemStack"), child);
    }

    public void read(CompoundTag nbt)
    {
        CompoundTag child = nbt.getCompound(SwiftUtils.tagName("cacheItemStack"));
        itemStack = ItemStack.of(child);
    }

    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeItemStack(itemStack, false);
    }

    public void read(FriendlyByteBuf buffer)
    {
        itemStack = buffer.readItem();
    }

    public ItemStack itemStack;
}
