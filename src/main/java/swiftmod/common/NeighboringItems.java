package swiftmod.common;

import java.util.ArrayList;
import java.util.Optional;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import swiftmod.pipes.PipeType;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

public class NeighboringItems
{
    @FunctionalInterface
    public interface Predicate
    {
        public boolean test(Level level, BlockPos pos, Direction dir);
    }

    @FunctionalInterface
    public interface PipeTypeGetter
    {
        public PipeType get(Level level, BlockPos pos, Direction dir);
    }

    private NeighboringItems(int size)
    {
        m_stacks = new ArrayList<NeighboringItem>(size);
        m_startingDirection = null;
        m_startingPipeType = PipeType.Item;
    }

    public NeighboringItems(Level level, BlockPos pos)
    {
        this(level, pos, (lvl, blockPos, dir) -> true, null, null);
    }

    public NeighboringItems(Level level, BlockPos pos, Predicate predicate)
    {
    	this(level, pos, predicate, null, null);
    }

    public NeighboringItems(Level level, BlockPos pos, Predicate predicate, Direction startingDir, PipeTypeGetter getter)
    {
        m_stacks = new ArrayList<NeighboringItem>(Direction.values().length);

        for (Direction dir : Direction.values())
        {
        	ItemStack stack = null;
        	Optional<Direction> dirFacing = Optional.empty();

            BlockPos neighborPos = pos.relative(dir);
            if (predicate.test(level, neighborPos, dir.getOpposite()))
            {
            	BlockState blockState = level.getBlockState(neighborPos);
                stack = new ItemStack(blockState.getBlock());
            	
            	// There are a number of properties used to determine block placement.
            	// Check all of them, starting from general to specific.
            	dirFacing = blockState.getOptionalValue(BlockStateProperties.FACING);
            	if (dirFacing.isEmpty())
                	dirFacing = blockState.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING);
            	if (dirFacing.isEmpty())
                	dirFacing = blockState.getOptionalValue(BlockStateProperties.FACING_HOPPER);
            }

            Direction dirFacingReal;
        	if (dirFacing.isPresent())
        		dirFacingReal = dirFacing.get();
        	else
        		dirFacingReal = Direction.NORTH; // Default value; in this case the block is probably rendered the same on all sides.
            
            if (stack != null && stack.getCount() > 0)
            {
                NeighboringItem item = new NeighboringItem(dir, dirFacingReal, stack);
                m_stacks.add(item);
            }
        }

        m_startingDirection = startingDir;
        if (getter != null && m_startingDirection != null)
        	m_startingPipeType = getter.get(level, pos, m_startingDirection);
        else
        	m_startingPipeType = PipeType.Item;
    }

    public void setStartingDirection(Direction dir)
    {
        m_startingDirection = dir;
    }

    public Direction getStartingDirection()
    {
        return m_startingDirection;
    }

    public void setStartingPipeType(PipeType type)
    {
    	m_startingPipeType = type;
    }

    public PipeType getStartingPipeType()
    {
        return m_startingPipeType;
    }

    public ArrayList<NeighboringItem> getItems()
    {
        return m_stacks;
    }

    public void serialize(RegistryFriendlyByteBuf buffer)
    {
        buffer.writeInt(m_stacks.size());
        for (int i = 0; i < m_stacks.size(); ++i)
        {
        	NeighboringItem item = m_stacks.get(i);
            buffer.writeInt(SwiftUtils.dirToIndex(item.direction));
            if (item.facing == null)
            	buffer.writeInt(SwiftUtils.dirToIndex(Direction.NORTH));
            else
            	buffer.writeInt(SwiftUtils.dirToIndex(item.facing));
            ItemStack.OPTIONAL_STREAM_CODEC.encode(buffer, item.stack);
        }
        if (m_startingDirection == null)
            buffer.writeInt(-1);
        else
            buffer.writeInt(SwiftUtils.dirToIndex(m_startingDirection));
        buffer.writeInt(m_startingPipeType.getIndex());
    }

    public static NeighboringItems deserialize(RegistryFriendlyByteBuf buffer)
    {
        int size = buffer.readInt();
        NeighboringItems items = new NeighboringItems(size);
        for (int i = 0; i < size; ++i)
        {
            Direction dir = SwiftUtils.indexToDir(buffer.readInt());
            Direction facing = SwiftUtils.indexToDir(buffer.readInt());
            ItemStack stack = ItemStack.OPTIONAL_STREAM_CODEC.decode(buffer);
            NeighboringItem item = new NeighboringItem(dir, facing, stack);
            items.m_stacks.add(item);
        }
        int dirInt = buffer.readInt();
        if (dirInt < 0)
            items.setStartingDirection(null);
        else
            items.setStartingDirection(SwiftUtils.indexToDir(dirInt));
        items.setStartingPipeType(PipeType.fromIndex(buffer.readInt()));
        return items;
    }

    private ArrayList<NeighboringItem> m_stacks;
    private Direction m_startingDirection;
    private PipeType m_startingPipeType;
}
