package swiftmod.common;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;

public class NeighboringItem
{
    public NeighboringItem()
    {
        direction = Direction.NORTH;
        stack = ItemStack.EMPTY;
    }

    public NeighboringItem(Direction d, ItemStack s)
    {
        direction = d;
        stack = s;
    }

    public Direction direction;
    public ItemStack stack;
}
