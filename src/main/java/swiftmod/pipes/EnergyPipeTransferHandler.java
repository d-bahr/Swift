package swiftmod.pipes;

import net.neoforged.neoforge.energy.IEnergyStorage;
import swiftmod.common.SwiftUtils;

public class EnergyPipeTransferHandler extends PipeTransferHandler<Integer>
{
	public EnergyPipeTransferHandler()
	{
		super(PipeType.Energy);
	}
	
	@Override
	public int getSize()
	{
		// IEnergyStorage doesn't have a concepts of slots really,
		// so just say there's one storage location.
		return 1;
	}

	@Override
	public Integer getStack(int slot)
	{
		if (handler == null)
			return 0;
		else
			return handler.getMaxEnergyStored();
	}

	@Override
	public int getCount(Integer amount)
	{
        return amount;
	}
	
	@Override
	public boolean isEmpty(Integer amount)
	{
        return amount <= 0;
	}
	
	@Override
	public boolean hasHandler()
	{
		return handler != null;
	}

	@Override
	public int simulateInsertion(int slot, Integer amount)
	{
		if (handler == null)
			return 0;
        return handler.receiveEnergy(amount, true);
	}

	@Override
	public int transferTo(int extractionSlot,
						  PipeTransferHandler<Integer> inserter,
						  int insertionSlot,
						  Integer amount,
						  int numToTransfer)
	{
		if (!inserter.hasHandler() || handler == null)
			return 0;
		int extracted = handler.extractEnergy(numToTransfer, false);
        return inserter.insert(insertionSlot, extracted);
	}
	
	@Override
	public int insert(int insertionSlot, Integer amount)
	{
		return handler.receiveEnergy(amount, false);
	}
	
	@Override
	public void cacheHandler(int tick)
	{
		// Cache handler on each tick; should provide a modest optimization
		// if a single handler does a lot of insertion.
		if (this.tick != tick)
		{
			handler = SwiftUtils.getEnergyHandler(pipe.getLevel(), pipe.getNeighborPos(neighborDir), handlerDir);
			this.tick = tick;
		}
	}
	
	private IEnergyStorage handler;
}
