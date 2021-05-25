package swiftmod.common;

import java.util.ArrayList;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class NeighboringItems
{
    @FunctionalInterface
    public interface Predicate
    {
        public boolean test(TileEntity tileEntity, Direction dir);
    }

    private NeighboringItems(int size)
    {
        m_stacks = new ArrayList<NeighboringItem>(size);
        m_startingDirection = null;
    }

    public NeighboringItems(IBlockReader blockReader, BlockPos pos)
    {
        this(blockReader, pos, (tileEntity, dir) -> true);
    }

    public NeighboringItems(IBlockReader blockReader, BlockPos pos, Predicate predicate)
    {
        m_stacks = new ArrayList<NeighboringItem>(Direction.values().length);

        Direction[] dirs = Direction.values();
        for (int i = 0; i < dirs.length; ++i)
        {
            ItemStack stack = createItemStackForNeighbor(blockReader, pos, dirs[i], predicate);
            if (stack != null && stack.getCount() > 0)
            {
                NeighboringItem item = new NeighboringItem(dirs[i], stack);
                m_stacks.add(item);
            }
        }
    }

    private static ItemStack createItemStackForNeighbor(IBlockReader blockReader, BlockPos pos, Direction dir,
            Predicate predicate)
    {
        BlockPos neighborPos = pos.relative(dir);
        TileEntity tileEntity = blockReader.getBlockEntity(neighborPos);
        if (predicate.test(tileEntity, dir.getOpposite()))
        {
            Block neighborBlock = blockReader.getBlockState(neighborPos).getBlock();
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

    public PacketBuffer serialize(PacketBuffer buffer)
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
        return buffer;
    }

    public static NeighboringItems deserialize(PacketBuffer buffer)
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
