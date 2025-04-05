package swiftmod.common;

import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;

public class NeighboringItem
{
    public NeighboringItem()
    {
        direction = Direction.NORTH;
        facing = Direction.NORTH;
        stack = ItemStack.EMPTY;
    }

    public NeighboringItem(Direction d, Direction f, ItemStack s)
    {
        direction = d;
        facing = f;
        stack = s;
    }

    public Direction direction;
    public Direction facing;
    public ItemStack stack;
}
