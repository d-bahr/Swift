package swiftmod.common;

import net.minecraft.world.item.ItemStack;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * An item stack with the capability to hold more than 255 items (which is the maximum a vanilla
 * ItemStack can hold).
 */
public class BigItemStack
{
    public BigItemStack()
    {
        clear();
    }

    public BigItemStack(ItemStack stack)
    {
        count = stack.getCount();
        itemStack = stack;
    }

    public BigItemStack(ItemStack stack, int c)
    {
        count = c;
        itemStack = stack;
    }

    public BigItemStack(HolderLookup.Provider provider, CompoundTag nbt)
    {
        read(provider, nbt);
    }

    public void clear()
    {
        count = 0;
        itemStack = ItemStack.EMPTY;
    }

    public void write(HolderLookup.Provider provider, CompoundTag nbt)
    {
        itemStack.save(provider, nbt);
        nbt.putInt(SwiftUtils.tagName("bigCount"), count);
    }

    public void read(HolderLookup.Provider provider, CompoundTag nbt)
    {
        itemStack = ItemStack.parseOptional(provider, nbt);
        count = nbt.getInt(SwiftUtils.tagName("bigCount"));
    }

    public void setCount(int c)
    {
        count = c;
    }

    public int getCount()
    {
        return count;
    }

    public int getMaxStackSize()
    {
       return itemStack.getMaxStackSize();
    }

    public boolean isEmpty()
    {
        return itemStack.isEmpty();
    }

    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public void setItemStack(ItemStack stack)
    {
        itemStack = stack;
    }

    private int count;
    private ItemStack itemStack;

    public static final Codec<BigItemStack> CODEC = RecordCodecBuilder.create(instance ->
    	instance.group(
    			ItemStack.OPTIONAL_CODEC.fieldOf("i").forGetter(BigItemStack::getItemStack),
    			Codec.INT.fieldOf("c").forGetter(BigItemStack::getCount))
    	.apply(instance, BigItemStack::new));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, BigItemStack> STREAM_CODEC =
    		StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC, BigItemStack::getItemStack,
    				ByteBufCodecs.VAR_INT, BigItemStack::getCount,
    				BigItemStack::new);
}
