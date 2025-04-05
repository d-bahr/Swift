package swiftmod.pipes.networks;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.function.Consumer;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import swiftmod.pipes.PipeTransferHandler;
import swiftmod.pipes.PipeType;

public class PipeHandlerMap<U> extends TreeMap<Integer, HashSet<PipeTransferHandler<U>>>
{
	public PipeHandlerMap(PipeType type)
	{
		super();
		m_type = type;
	}
	
	public PipeHandlerMap(PipeType type, Comparator<? super Integer> comparator)
	{
		super(comparator);
		m_type = type;
	}
	
	public PipeType getType()
	{
		return m_type;
	}
	
	@SuppressWarnings("unchecked")
	public boolean addHandler(PipeTransferHandler<?> handler)
	{
		if (handler.type == m_type)
		{
			super.computeIfAbsent(handler.priority, v -> new HashSet<PipeTransferHandler<U>>()).add((PipeTransferHandler<U>)handler);
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void forEachHandler(Consumer<? super PipeTransferHandler<U>> consumer)
	{
		Collection<HashSet<PipeTransferHandler<U>>> collection = values();
		for (HashSet<PipeTransferHandler<U>> handlers : collection)
		{
			for (PipeTransferHandler<U> handler : handlers)
			{
				consumer.accept(handler);
			}
		}
	}
	
	public boolean removeHandler(PipeTransferHandler<?> handler)
	{
		if (handler.type == m_type)
		{
			HashSet<PipeTransferHandler<U>> set = super.get(handler.priority);
			if (set != null)
			{
				if (set.remove(handler))
				{
					if (set.isEmpty())
					{
						// Slight inefficiency, as this requires a double-lookup.
						// TODO: Might be able to make this more efficient with something involving the
						// upper submap and removing the first element, which may not require reiterating.
						super.remove(handler.priority);
					}
					return true;
				}
			}
		}
		return false;
	}
	
	public static PipeHandlerMap<?> create(PipeType type)
	{
		switch (type)
		{
		case Item:
			return new PipeHandlerMap<ItemStack>(type);
		case Fluid:
			return new PipeHandlerMap<FluidStack>(type);
		case Energy:
			return new PipeHandlerMap<Integer>(type);
		default:
			return null;
		}
	}
	
	public static PipeHandlerMap<?> create(PipeType type, Comparator<? super Integer> comparator)
	{
		switch (type)
		{
		case Item:
			return new PipeHandlerMap<ItemStack>(type, comparator);
		case Fluid:
			return new PipeHandlerMap<FluidStack>(type, comparator);
		case Energy:
			return new PipeHandlerMap<Integer>(type, comparator);
		default:
			return null;
		}
	}
	
	private final PipeType m_type;
	
	private static final long serialVersionUID = -6307374342044635597L;
}
