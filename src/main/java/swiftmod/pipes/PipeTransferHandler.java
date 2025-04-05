package swiftmod.pipes;

import java.util.Objects;

import net.minecraft.core.Direction;

public abstract class PipeTransferHandler<Type>
{
	public PipeTransferHandler(PipeType t)
	{
		type = t;
		pipe = null;
		neighborDir = Direction.DOWN;
		handlerDir = Direction.DOWN;
		priority = 0;
		tick = -1;
		tickOfLastTransfer = -1;
	}

	public PipeTransferHandler(PipeType t, PipeTileEntity p, Direction d)
	{
		type = t;
		pipe = p;
		neighborDir = d;
		handlerDir = d.getOpposite();
		priority = 0;
		tick = -1;
		tickOfLastTransfer = -1;
	}

	public PipeTransferHandler(PipeType t, PipeTileEntity p, Direction nd, Direction hd)
	{
		type = t;
		pipe = p;
		neighborDir = nd;
		handlerDir = hd;
		priority = 0;
		tick = -1;
		tickOfLastTransfer = -1;
	}
	
	public final PipeType type;
	public PipeTileEntity pipe;
	public Direction neighborDir;
	public Direction handlerDir;
	public int priority;
	public int tick;
	public int tickOfLastTransfer;
	
	public PipeTransferData<?> lookupTransferData()
	{
		return pipe.getTransferData(type, neighborDir, handlerDir);
	}
	
	public abstract int getSize();
	
	public abstract Type getStack(int slot);
	
	public abstract int getCount(Type stack);
	
	public abstract boolean isEmpty(Type stack);
	
	public abstract boolean hasHandler();
	
	public abstract int simulateInsertion(int slot, Type stack);
	
	public abstract int transferTo(int extractionSlot,
			PipeTransferHandler<Type> inserter,
			int insertionSlot,
			Type stack,
			int numToTransfer);

	protected abstract int insert(int slot, Type stack);
	
	public abstract void cacheHandler(int tick);
	
	@Override
	public int hashCode()
	{
		return Objects.hash(type, pipe, neighborDir, handlerDir, priority);
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (this == other)
			return true;
		else if (other == null || getClass() != other.getClass())
			return false;
		else
		{
			PipeTransferHandler<?> otherHandler = (PipeTransferHandler<?>)other;
			return type == otherHandler.type &&
				   pipe == otherHandler.pipe &&
				   neighborDir == otherHandler.neighborDir &&
				   handlerDir == otherHandler.handlerDir &&
				   priority == otherHandler.priority;
		}
	}
}
