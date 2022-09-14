package swiftmod.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;

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

    public BigItemStack(CompoundTag nbt)
    {
        read(nbt);
    }

    public BigItemStack(FriendlyByteBuf buffer)
    {
        read(buffer);
    }

    public void clear()
    {
        count = 0;
        itemStack = ItemStack.EMPTY;
    }

    public void write(CompoundTag nbt)
    {
        itemStack.save(nbt);
        nbt.putInt(SwiftUtils.tagName("bigCount"), count);
    }

    public void read(CompoundTag nbt)
    {
        itemStack = ItemStack.of(nbt);
        count = nbt.getInt(SwiftUtils.tagName("bigCount"));
    }

    public void write(FriendlyByteBuf buffer)
    {
        buffer.writeItemStack(itemStack, false);
        buffer.writeInt(count);
    }

    public void read(FriendlyByteBuf buffer)
    {
        itemStack = buffer.readItem();
        count = buffer.readInt();
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
}
