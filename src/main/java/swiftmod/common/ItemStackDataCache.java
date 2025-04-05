package swiftmod.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;

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

    public void write(HolderLookup.Provider provider, CompoundTag nbt)
    {
    	Tag child = itemStack.save(provider);
        nbt.put(SwiftUtils.tagName("cacheItemStack"), child);
    }

    public void read(HolderLookup.Provider provider, CompoundTag nbt)
    {
        CompoundTag child = nbt.getCompound(SwiftUtils.tagName("cacheItemStack"));
        itemStack = ItemStack.parseOptional(provider, child);
    }

    public void write(RegistryFriendlyByteBuf buffer)
    {
    	ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, itemStack);
    }

    public void read(RegistryFriendlyByteBuf buffer)
    {
    	itemStack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
    }

    public ItemStack itemStack;
}
