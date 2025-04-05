package swiftmod.pipes;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import swiftmod.common.Color;
import swiftmod.common.Filter;
import swiftmod.common.FilterMatchResult;
import swiftmod.common.RedstoneControl;
import swiftmod.common.Swift;
import swiftmod.pipes.networks.PipeHandlerMap;

public class PipeTransfer
{
	@SuppressWarnings("unchecked")
	public static void transfer(PipeHandlerMap<?> extractors, PipeHandlerMap<?> inserters, int tick)
	{
		if (extractors.getType() != inserters.getType())
		{
			// If other logic is correct, this shouldn't be possible.
			Swift.LOGGER.warn("Unexpected transfer between networks of different types.");
			return;
		}
		
		switch (extractors.getType())
		{
		case Item:
			doTransfer((PipeHandlerMap<ItemStack>)extractors, (PipeHandlerMap<ItemStack>)inserters, tick);
			break;
		case Fluid:
			doTransfer((PipeHandlerMap<FluidStack>)extractors, (PipeHandlerMap<FluidStack>)inserters, tick);
			break;
		case Energy:
			doTransfer((PipeHandlerMap<Integer>)extractors, (PipeHandlerMap<Integer>)inserters, tick);
			break;
		default:
			Swift.LOGGER.warn("Unhandled transfer type.");
			break;
		}
	}
    
	private static <Type> void doTransfer(PipeHandlerMap<Type> extractors, PipeHandlerMap<Type> inserters, int tick)
    {
		Set<Entry<Integer, HashSet<PipeTransferHandler<Type>>>> set = extractors.entrySet();
		for (Entry<Integer, HashSet<PipeTransferHandler<Type>>> entry : set)
		{
	        Iterator<PipeTransferHandler<Type>> iter = entry.getValue().iterator();
	        while (iter.hasNext())
	        {
	        	PipeTransferHandler<Type> extractor = iter.next();
	        	// Make a copy because we will modify.
	        	@SuppressWarnings("unchecked")
				PipeTransferData<Type> extractData = (PipeTransferData<Type>)extractor.lookupTransferData();
	        	if ((extractor.tickOfLastTransfer + extractData.tickRate <= tick) &&
	        	    isRedstoneValid(extractData.redstoneControl, extractor.pipe.hasRedstoneSignal()))
	        	{
	        		doTransfer(extractor, extractData, inserters,
	        				      extractData.maxTransferQuantity.quantity,
	        				      extractData.maxTransferQuantity.moveStacks, tick);
	        	}
	        }
		}
    }

	private static <Type> int doTransfer(PipeTransferHandler<Type> extractor,
    		PipeTransferData<Type> extractData,
    		TreeMap<Integer, HashSet<PipeTransferHandler<Type>>> inserters,
    		int itemMoveCount,
    		boolean moveStacks,
    		int tick)
    {
    	extractor.cacheHandler(tick);
    	if (!extractor.hasHandler())
    		return itemMoveCount;

        // Use the tick counter to set the "start" slot for extraction.
    	// This allows extraction to start on a new slot every tick, which
    	// is a nice workaround for cases where a small amount of items
    	// are inserted into the first slot every tick.
        int size = extractor.getSize();
        int startingSlot = tick % size;
        
        // TODO: Keep track of which handlers have already been inserted into, and avoid those. Would save some time.
        // Probably would require a full graph of extractions/insertions though.

        for (int extractionSlot = startingSlot; extractionSlot < size; ++extractionSlot)
        {
            itemMoveCount = tryInsert(extractor, extractData, extractionSlot, inserters, itemMoveCount, moveStacks, tick);

            if (itemMoveCount <= 0)
                return 0;
        }

        for (int extractionSlot = 0; extractionSlot < startingSlot; ++extractionSlot)
        {
            itemMoveCount = tryInsert(extractor, extractData, extractionSlot, inserters, itemMoveCount, moveStacks, tick);

            if (itemMoveCount <= 0)
                return 0;
        }

        return itemMoveCount;
    }

	private static <Type> int tryInsert(PipeTransferHandler<Type> extractor,
        	PipeTransferData<Type> extractData,
    		int extractionSlot,
    		TreeMap<Integer, HashSet<PipeTransferHandler<Type>>> inserters,
    		int itemMoveCount,
            boolean moveStacks,
    		int tick)
    {
        Type stack = extractor.getStack(extractionSlot);
        if (extractor.isEmpty(stack))
            return itemMoveCount;

        if (extractData.filter != null)
        {
            FilterMatchResult<Filter<Type>> matchResult = extractData.filter.filter(stack);
            if (!matchResult.matches)
                return itemMoveCount;
        }

		Set<Entry<Integer, HashSet<PipeTransferHandler<Type>>>> set = inserters.entrySet();
		for (Entry<Integer, HashSet<PipeTransferHandler<Type>>> entry : set)
		{
			// TODO: It would be great to allow round-robin of things that are the same priority level.
			// I don't currently see a clean way to handle this outside of implementing a custom hash table or something...
	        Iterator<PipeTransferHandler<Type>> iter = entry.getValue().iterator();
	        while (iter.hasNext())
	        {
	        	PipeTransferHandler<Type> inserter = iter.next();
	        	@SuppressWarnings("unchecked")
				PipeTransferData<Type> insertData = (PipeTransferData<Type>)inserter.lookupTransferData();
	            if (extractData.color == Color.Transparent || insertData.color == Color.Transparent
	                    || extractData.color == insertData.color)
	            {
	            	if (isRedstoneValid(insertData.redstoneControl, inserter.pipe.hasRedstoneSignal()))
	            	{
		                itemMoveCount = tryInsert(extractor, extractionSlot, inserter, insertData,
		                        stack, itemMoveCount, moveStacks, tick);
	            	}
	            }
	        }
		}

        return itemMoveCount;
    }

	private static <Type> int tryInsert(PipeTransferHandler<Type> extractor,
    		int extractionSlot,
    		PipeTransferHandler<Type> inserter,
    		PipeTransferData<Type> insertData,
    		Type stack,
    		int itemMoveCount,
    		boolean moveStacks,
    		int tick)
    {
		// Cache the handler beforehand so we can bail out quickly (before a potentially
		// costly filter operation) if the handler is not connected.
    	inserter.cacheHandler(tick);
    	if (!inserter.hasHandler())
    		return itemMoveCount;
    	
        FilterMatchResult<Filter<Type>> matchResult = new FilterMatchResult<Filter<Type>>();
        if (insertData.filter != null)
        {
            // Passing true here reduces the filter so on a match only the matching filter
            // is returned. This allows for better handling of item-count matching when
            // ore dictionary is used.
            matchResult = insertData.filter.filter(stack, true);
            if (!matchResult.matches)
                return itemMoveCount;
        }

        int targetWhitelistCount = matchResult.getMatchCount();
        int currentItemCount = 0;
        if (targetWhitelistCount > 0)
        {
            for (int insertionSlot = 0; insertionSlot < inserter.getSize(); ++insertionSlot)
            {
                Type existingStack = inserter.getStack(insertionSlot);
                FilterMatchResult<Filter<Type>> c = matchResult.filter.filter(existingStack);
                if (c.matches)
                    currentItemCount += inserter.getCount(existingStack);
            }

            if (currentItemCount >= targetWhitelistCount)
                return itemMoveCount;
        }

        for (int insertionSlot = 0; insertionSlot < inserter.getSize(); ++insertionSlot)
        {
            // Simulate the insertion.
            int numItemsToInsert = inserter.simulateInsertion(insertionSlot, stack);
            if (targetWhitelistCount > 0)
                numItemsToInsert = Math.min(numItemsToInsert, targetWhitelistCount - currentItemCount);

            // Make sure that we could actually insert items into the stack. If we can't, then just
            // bail out. As a side-effect, we also can determine the number of items
            if (numItemsToInsert > 0)
            {
                if (moveStacks)
                {
                    // Transfer the item(s).
                    int extractCount = extractor.transferTo(extractionSlot, inserter, insertionSlot, stack, numItemsToInsert);
                    currentItemCount += extractCount;
                    
                    extractor.tickOfLastTransfer = tick;

                    --itemMoveCount;
                    if (itemMoveCount <= 0)
                        return 0;

                    // After insertion, bail out. Just go to the next stack. Trying to finish
                    // transferring this entire stack is prohibitively CPU-intensive.
                    // TODO: This would be nice to implement...
                    break;
                }
                else
                {
                    int moveCount = Math.min(numItemsToInsert, itemMoveCount);

                    // Transfer the item(s).
                    int extractCount = extractor.transferTo(extractionSlot, inserter, insertionSlot, stack, moveCount);
                    currentItemCount += extractCount;
                    
                    extractor.tickOfLastTransfer = tick;

                    itemMoveCount -= extractCount;
                    if (itemMoveCount <= 0)
                        return 0;
                }
            }
        }
        
        return itemMoveCount;
    }

    private static boolean isRedstoneValid(RedstoneControl rc, boolean hasRedstone)
    {
        switch (rc)
        {
        case Ignore:
            return true;
        case Inverted:
            return !hasRedstone;
        case Normal:
            return hasRedstone;
        default:
            return false;
        }
    }
}
