package swiftmod.common;

import java.util.ArrayList;

import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class NeighboringItems
{
    @FunctionalInterface
    public interface Predicate
    {
        public boolean test(BlockEntity blockEntity, Direction dir);
    }

    private NeighboringItems(int size)
    {
        m_stacks = new ArrayList<NeighboringItem>(size);
        m_startingDirection = null;
    }

    public NeighboringItems(BlockGetter blockGetter, BlockPos pos)
    {
        this(blockGetter, pos, (blockEntity, dir) -> true);
    }

    public NeighboringItems(BlockGetter blockGetter, BlockPos pos, Predicate predicate)
    {
        m_stacks = new ArrayList<NeighboringItem>(Direction.values().length);

        Direction[] dirs = Direction.values();
        for (int i = 0; i < dirs.length; ++i)
        {
            ItemStack stack = createItemStackForNeighbor(blockGetter, pos, dirs[i], predicate);
            if (stack != null && stack.getCount() > 0)
            {
                NeighboringItem item = new NeighboringItem(dirs[i], stack);
                m_stacks.add(item);
            }
        }
    }

    private static ItemStack createItemStackForNeighbor(BlockGetter blockGetter, BlockPos pos, Direction dir,
            Predicate predicate)
    {
        BlockPos neighborPos = pos.relative(dir);
        BlockEntity blockEntity = blockGetter.getBlockEntity(neighborPos);
        if (predicate.test(blockEntity, dir.getOpposite()))
        {
            Block neighborBlock = blockGetter.getBlockState(neighborPos).getBlock();
            ItemStack stack = new ItemStack(neighborBlock);
            if (stack.getCount() > 0)
                return stack;
            else
                return null;
        }
        else
        {
            return null;
        }
    }

    public void setStartingDirection(Direction dir)
    {
        m_startingDirection = dir;
    }

    public Direction getStartingDirection()
    {
        return m_startingDirection;
    }

    public ArrayList<NeighboringItem> getItems()
    {
        return m_stacks;
    }

    public void serialize(FriendlyByteBuf buffer)
    {
        buffer.writeInt(m_stacks.size());
        for (int i = 0; i < m_stacks.size(); ++i)
        {
            buffer.writeInt(SwiftUtils.dirToIndex(m_stacks.get(i).direction));
            buffer.writeItemStack(m_stacks.get(i).stack, false);
        }
        if (m_startingDirection == null)
            buffer.writeInt(-1);
        else
            buffer.writeInt(SwiftUtils.dirToIndex(m_startingDirection));
    }

    public static NeighboringItems deserialize(FriendlyByteBuf buffer)
    {
        int size = buffer.readInt();
        NeighboringItems items = new NeighboringItems(size);
        for (int i = 0; i < size; ++i)
        {
            Direction dir = SwiftUtils.indexToDir(buffer.readInt());
            ItemStack stack = buffer.readItem();
            NeighboringItem item = new NeighboringItem(dir, stack);
            items.m_stacks.add(item);
        }
        int dirInt = buffer.readInt();
        if (dirInt < 0)
            items.setStartingDirection(null);
        else
            items.setStartingDirection(SwiftUtils.indexToDir(dirInt));
        return items;
    }

    private ArrayList<NeighboringItem> m_stacks;
    private Direction m_startingDirection;
}
