package swiftmod.common;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

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

    public BigItemStack(CompoundNBT nbt)
    {
        read(nbt);
    }

    public BigItemStack(PacketBuffer buffer)
    {
        read(buffer);
    }

    public void clear()
    {
        count = 0;
        itemStack = ItemStack.EMPTY;
    }

    public CompoundNBT write(CompoundNBT nbt)
    {
        itemStack.save(nbt);
        nbt.putInt(SwiftUtils.tagName("bigCount"), count);
        return nbt;
    }

    public void read(CompoundNBT nbt)
    {
        itemStack = ItemStack.of(nbt);
        count = nbt.getInt(SwiftUtils.tagName("bigCount"));
    }

    public PacketBuffer write(PacketBuffer buffer)
    {
        buffer.writeItemStack(itemStack, false);
        buffer.writeInt(count);
        return buffer;
    }

    public void read(PacketBuffer buffer)
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
