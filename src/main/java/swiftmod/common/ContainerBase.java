package swiftmod.common;

import java.util.ArrayList;

import javax.annotation.Nullable;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerBase<T extends DataCache> extends PlayerInventoryContainer implements IDataCacheContainer<T>
{
    protected ContainerBase(@Nullable MenuType<?> type, int id, Inventory playerInventory, int x, int y)
    {
        super(type, id, playerInventory, x, y);
        m_cache = null;
    }

    protected ContainerBase(@Nullable MenuType<?> type, int id, T cache, Inventory playerInventory, int x, int y)
    {
        super(type, id, playerInventory, x, y);
        m_cache = cache;
    }

    public void addSlots(Slot[] slots)
    {
        for (int i = 0; i < slots.length; ++i)
            addSlot(slots[i]);
    }

    public void addSlots(ArrayList<Slot> slots)
    {
        for (int i = 0; i < slots.size(); ++i)
            addSlot(slots.get(i));
    }

    /*
     * Base class is stupid and doesn't call isEnabled to check if a slot can accept an item,
     * so we have to override it here...
     * This is basically a straight copy of the base class with a little bit added.
     */
    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection)
    {
        boolean flag = false;
        int i = startIndex;
        if (reverseDirection)
        {
            i = endIndex - 1;
        }

        if (stack.isStackable())
        {
            while (!stack.isEmpty())
            {
                if (reverseDirection)
                {
                    if (i < startIndex)
                    {
                        break;
                    }
                }
                else if (i >= endIndex)
                {
                    break;
                }

                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();
                boolean isSlotEnabled = true;
                if (slot instanceof EnableableSlot)
                	isSlotEnabled = ((EnableableSlot)slot).enable;
                if (!itemstack.isEmpty() && isSlotEnabled && ItemStack.isSameItemSameTags(stack, itemstack))
                {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(stack), stack.getMaxStackSize());
                    if (j <= maxSize)
                    {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;
                    }
                    else if (itemstack.getCount() < maxSize)
                    {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if (reverseDirection)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        if (!stack.isEmpty())
        {
            if (reverseDirection)
            {
                i = endIndex - 1;
            }
            else
            {
                i = startIndex;
            }

            while (true)
            {
                if (reverseDirection)
                {
                    if (i < startIndex)
                    {
                        break;
                    }
                }
                else if (i >= endIndex)
                {
                    break;
                }

                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();
                boolean isSlotEnabled = true;
                if (slot1 instanceof EnableableSlot)
                	isSlotEnabled = ((EnableableSlot)slot1).enable;
                if (itemstack1.isEmpty() && isSlotEnabled && slot1.mayPlace(stack))
                {
                    int limit = slot1.getMaxStackSize(stack);
                    if (stack.getCount() > limit)
                    {
                        slot1.set(stack.split(limit));
                    }
                    else
                    {
                        slot1.set(stack.split(stack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection)
                {
                    --i;
                }
                else
                {
                    ++i;
                }
            }
        }

        return flag;
    }

    @Override
    public boolean stillValid(Player playerIn)
    {
        return true;
    }

    @Override
    public T getCache()
    {
        return m_cache;
    }

    protected final T m_cache;
}
