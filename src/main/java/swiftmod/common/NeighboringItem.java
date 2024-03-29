package swiftmod.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

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
