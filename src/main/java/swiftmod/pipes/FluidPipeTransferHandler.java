package swiftmod.pipes;

import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
import swiftmod.common.SwiftUtils;

public class FluidPipeTransferHandler extends PipeTransferHandler<FluidStack>
{
	public FluidPipeTransferHandler()
	{
		super(PipeType.Fluid);
	}
	
	@Override
	public int getSize()
	{
		if (handler == null)
			return 0;
        return handler.getTanks();
	}

	@Override
	public FluidStack getStack(int slot)
	{
		if (handler == null)
			return FluidStack.EMPTY;
        return handler.getFluidInTank(slot);
	}

	@Override
	public int getCount(FluidStack stack)
	{
        return stack.getAmount();
	}
	
	@Override
	public boolean isEmpty(FluidStack stack)
	{
        return stack.isEmpty();
	}
	
	@Override
	public boolean hasHandler()
	{
		return handler != null;
	}

	@Override
	public int simulateInsertion(int slot, FluidStack stack)
	{
		if (handler == null)
			return 0;
        return handler.fill(stack, FluidAction.SIMULATE);
	}

	@Override
	public int transferTo(int extractionSlot,
						  PipeTransferHandler<FluidStack> inserter,
						  int insertionSlot,
						  FluidStack stack,
						  int numToTransfer)
	{
		if (!inserter.hasHandler() || handler == null)
			return 0;
        FluidStack copy = stack.copy();
        copy.setAmount(numToTransfer);
        FluidStack extractedStack = handler.drain(copy, FluidAction.EXECUTE);
        return inserter.insert(insertionSlot, extractedStack);
	}
	
	@Override
	public int insert(int insertionSlot, FluidStack stack)
	{
		return handler.fill(stack, FluidAction.EXECUTE);
	}
	
	@Override
	public void cacheHandler(int tick)
	{
		// Cache handler on each tick; should provide a modest optimization
		// if a single handler does a lot of insertion.
		if (this.tick != tick)
		{
			handler = SwiftUtils.getFluidHandler(pipe.getLevel(), pipe.getNeighborPos(neighborDir), handlerDir);
			this.tick = tick;
		}
	}
	
	private IFluidHandler handler;
}
