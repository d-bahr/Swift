package swiftmod.pipes;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.IItemHandler;
import swiftmod.common.SwiftUtils;

public class ItemPipeTransferHandler extends PipeTransferHandler<ItemStack>
{
	public ItemPipeTransferHandler()
	{
		super(PipeType.Item);
	}
	
	@Override
	public int getSize()
	{
		if (handler == null)
			return 0;
        return handler.getSlots();
	}

	@Override
	public ItemStack getStack(int slot)
	{
		if (handler == null)
			return ItemStack.EMPTY;
        return handler.getStackInSlot(slot);
	}

	@Override
	public int getCount(ItemStack stack)
	{
        return stack.getCount();
	}
	
	@Override
	public boolean isEmpty(ItemStack stack)
	{
        return stack.isEmpty();
	}
	
	@Override
	public boolean hasHandler()
	{
		return handler != null;
	}

	@Override
	public int simulateInsertion(int slot, ItemStack stack)
	{
		if (handler == null)
			return 0;
        ItemStack simulation = handler.insertItem(slot, stack, true);
        return stack.getCount() - simulation.getCount();
	}

	@Override
	public int transferTo(int extractionSlot,
						  PipeTransferHandler<ItemStack> inserter,
						  int insertionSlot,
						  ItemStack stack,
						  int numToTransfer)
	{
		if (!inserter.hasHandler() || handler == null)
			return 0;
        ItemStack extractedStack = handler.extractItem(extractionSlot, numToTransfer, false);
        return inserter.insert(insertionSlot, extractedStack);
	}
	
	@Override
	public int insert(int insertionSlot, ItemStack stack)
	{
		int before = stack.getCount(); // Cache before; not sure if insertItem will modify this.
		ItemStack remaining = handler.insertItem(insertionSlot, stack, false);
		return before - remaining.getCount();
	}
	
	@Override
	public void cacheHandler(int tick)
	{
		// Cache handler on each tick; should provide a modest optimization
		// if a single handler does a lot of insertion.
		if (this.tick != tick)
		{
			handler = SwiftUtils.getItemHandler(pipe.getLevel(), pipe.getNeighborPos(neighborDir), handlerDir);
			this.tick = tick;
		}
	}
	
	private IItemHandler handler;
}
