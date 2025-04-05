package swiftmod.pipes.networks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ibm.icu.text.RelativeDateTimeFormatter.Direction;

import swiftmod.common.Swift;
import swiftmod.common.TransferDirection;
import swiftmod.pipes.PipeTileEntity;
import swiftmod.pipes.PipeTransfer;
import swiftmod.pipes.PipeTransferHandler;
import swiftmod.pipes.PipeType;

// Contains multiple PipeChannelNetworks. This is used in the case where networks are joined
// via wormholes. This optimizes pipe addition and removal so that it doesn't affect
// other local networks, at the expense of needing to iterate each local network TreeMap
// on extraction/insertion.
//
// A possible use case is:
// PipeNetwork 1 -> Wormhole A -> PipeNetwork 2 -> Wormhole B -> PipeNetwork 3
//
// In the above case, if Wormhole A and Wormhole B are using channels A and B respectively,
// then PipeNetwork 1, Wormhole A, and PipeNetwork 2 form one PipeChannelNetwork, and
// PipeNetwork 2, Wormhole B, and PipeNetwork 3 form a second PipeChannelNetwork.
// A PipeHyperNetwork comprises the entire thing.
//
// Note that PipeNetworks which are not attached to a wormhole are entirely independent.
// However, by definition, all PipeChannelNetworks *must* be part of a PipeHyperNetwork, even
// if there is only a single channel. (In fact, this is probably the most likely use case.)
// Additionally, a PipeChannelNetwork *must* be part of only a *single* PipeHyperNetwork,
// because the hyper network must encompass all possible connected local networks.
public class PipeHyperNetwork
{
	public PipeHyperNetwork(PipeType type)
	{
		m_type = type;
		m_channelNetworks = new HashSet<PipeChannelNetwork>();
		// TreeMaps use reverse ordering so that higher priorities (i.e. larger numbers) are iterated first
		// (by default, TreeMap iterates from lowest number to highest number).
		m_insertHandlers = PipeHandlerMap.create(type, Collections.reverseOrder());
		m_extractHandlers = PipeHandlerMap.create(type, Collections.reverseOrder());
	}
	
	public PipeType getType()
	{
		return m_type;
	}
	
	public void addNetwork(PipeChannelNetwork network)
	{
		m_channelNetworks.add(network);
	}
	
	public void removeNetwork(PipeChannelNetwork network)
	{
		m_channelNetworks.remove(network);
	}
	
	public Set<PipeChannelNetwork> getNetworks()
	{
		return m_channelNetworks;
	}
	
	public int numNetworks()
	{
		return m_channelNetworks.size();
	}
	
	public void refreshExtractAndInsertHandlers()
	{
		m_insertHandlers.clear();
		m_extractHandlers.clear();
		
		// TODO: This should really be avoided at all costs. Find other ways to limit the amount
		// of the network that needs to be traversed. Going through the entire thing should really
		// only be done as a last resort.
		for (PipeChannelNetwork channelNetwork : m_channelNetworks)
		{
			for (PipeNetwork network : channelNetwork.getNetworks())
			{
				copyHandlersFrom(network.getInsertHandlers(), network.getExtractHandlers());
			}
		}
	}
	
	public void takeFrom(PipeHyperNetwork other)
	{
		copyHandlersFrom(other.m_insertHandlers, other.m_extractHandlers);
		for (PipeChannelNetwork network : other.m_channelNetworks)
			network.setHyperNetwork(this);
		other.clear();
	}
	
	public void moveTo(PipeHyperNetwork other)
	{
		other.takeFrom(this);
	}
	
	public void addHandlersFrom(PipeNetwork network)
	{
		copyHandlersFrom(network.getInsertHandlers(), network.getExtractHandlers());
	}
	
	public void addHandlersFrom(PipeTileEntity tileEntity)
	{
		int numDirs = Direction.values().length;
		List<PipeTransferHandler<?>> insertHandlers = new ArrayList<PipeTransferHandler<?>>(numDirs);
		List<PipeTransferHandler<?>> extractHandlers = new ArrayList<PipeTransferHandler<?>>(numDirs);
		tileEntity.getHandlers(m_type, insertHandlers, extractHandlers);
		for (PipeTransferHandler<?> handler : insertHandlers)
			addInsertHandler(handler);
		for (PipeTransferHandler<?> handler : extractHandlers)
			addExtractHandler(handler);
	}
	
	public void removeHandlersFrom(PipeNetwork network)
	{
		network.getInsertHandlers().forEachHandler(this::removeInsertHandler);
		network.getExtractHandlers().forEachHandler(this::removeExtractHandler);
	}
	
	public void addInsertHandler(PipeTransferHandler<?> handler)
	{
		Swift.doAssert(m_insertHandlers.addHandler(handler), "Attempting to add handler to wrong hyper network type.");
	}
	
	public void addExtractHandler(PipeTransferHandler<?> handler)
	{
		Swift.doAssert(m_extractHandlers.addHandler(handler), "Attempting to add handler to wrong hyper network type.");
	}
	
	private void copyHandlersFrom(PipeHandlerMap<?> insertHandlers, PipeHandlerMap<?> extractHandlers)
	{
		// Doing a putAll() doesn't work here, because we need to combine the underlying HashSets.
		insertHandlers.forEachHandler(this::addInsertHandler);
		extractHandlers.forEachHandler(this::addExtractHandler);
	}
	
	public void addHandler(PipeTransferHandler<?> handler, TransferDirection direction)
	{
		if (direction == TransferDirection.Insert)
			addInsertHandler(handler);
		else
			addExtractHandler(handler);
	}
	
	public void removeInsertHandler(PipeTransferHandler<?> handler)
	{
		Swift.doAssert(m_insertHandlers.removeHandler(handler), "Attempting to remove handler from wrong hyper network type.");
	}
	
	public void removeExtractHandler(PipeTransferHandler<?> handler)
	{
		Swift.doAssert(m_extractHandlers.removeHandler(handler), "Attempting to remove handler from wrong hyper network type.");
	}
	
	public void removeHandler(PipeTransferHandler<?> handler, TransferDirection direction)
	{
		if (direction == TransferDirection.Insert)
			removeInsertHandler(handler);
		else
			removeExtractHandler(handler);
	}
	
	public PipeHandlerMap<?> getExtractHandlers()
	{
		return m_extractHandlers;
	}

	public PipeHandlerMap<?> getInsertHandlers()
	{
		return m_insertHandlers;
	}
    
    public void tick(int t)
    {
    	PipeTransfer.transfer(m_extractHandlers, m_insertHandlers, t);
    }
    
    public void clear()
    {
    	m_channelNetworks.clear();
    	m_extractHandlers.clear();
    	m_insertHandlers.clear();
    }
    
    public boolean isEmpty()
    {
    	return m_channelNetworks.isEmpty();
    }

    private final PipeType m_type; // TODO: Refactor to ChannelType.
	private HashSet<PipeChannelNetwork> m_channelNetworks;
	private PipeHandlerMap<?> m_extractHandlers;
	private PipeHandlerMap<?> m_insertHandlers;
}
