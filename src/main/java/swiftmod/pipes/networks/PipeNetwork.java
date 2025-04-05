package swiftmod.pipes.networks;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import swiftmod.common.Swift;
import swiftmod.common.TransferDirection;
import swiftmod.pipes.PipeTransfer;
import swiftmod.pipes.PipeTransferHandler;
import swiftmod.pipes.PipeType;

public class PipeNetwork
{
	public PipeNetwork(PipeType type)
	{
		m_type = type;
		m_channelNetworks = new HashMap<PipeChannelNetwork, Integer>();
		// TreeMaps use reverse ordering so that higher priorities (i.e. larger numbers) are iterated first
		// (by default, TreeMap iterates from lowest number to highest number).
		m_insertHandlers = PipeHandlerMap.create(type, Collections.reverseOrder());
		m_extractHandlers = PipeHandlerMap.create(type, Collections.reverseOrder());
		m_numPipes = 0;
	}
	
	public PipeType getType()
	{
		return m_type;
	}
	
	public void incrementPipeCounter()
	{
		m_numPipes++;
	}
	
	public void decrementPipeCounter()
	{
		if (m_numPipes > 0)
			m_numPipes--;
	}
	
	public void addInsertHandler(PipeTransferHandler<?> handler)
	{
		Swift.doAssert(handler.type == m_type, "Attempting to add handler to wrong network type.");
		if (m_insertHandlers.addHandler(handler))
		{
			PipeHyperNetwork hyperNetwork = getHyperNetwork();
			if (hyperNetwork != null)
				hyperNetwork.addInsertHandler(handler);
		}
	}
	
	public void addExtractHandler(PipeTransferHandler<?> handler)
	{
		Swift.doAssert(handler.type == m_type, "Attempting to add handler to wrong network type.");
		if (m_extractHandlers.addHandler(handler))
		{
			PipeHyperNetwork hyperNetwork = getHyperNetwork();
			if (hyperNetwork != null)
				hyperNetwork.addExtractHandler(handler);
		}
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
		Swift.doAssert(handler.type == m_type, "Attempting to remove handler from wrong network type.");
		if (m_insertHandlers.removeHandler(handler))
		{
			PipeHyperNetwork hyperNetwork = getHyperNetwork();
			if (hyperNetwork != null)
				hyperNetwork.removeInsertHandler(handler);
		}
	}
	
	public void removeExtractHandler(PipeTransferHandler<?> handler)
	{
		Swift.doAssert(handler.type == m_type, "Attempting to remove handler from wrong network type.");
		if (m_extractHandlers.removeHandler(handler))
		{
			PipeHyperNetwork hyperNetwork = getHyperNetwork();
			if (hyperNetwork != null)
				hyperNetwork.removeExtractHandler(handler);
		}
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
	
	@SuppressWarnings("unchecked")
	public TreeMap<Integer, HashSet<PipeTransferHandler<?>>> cloneInsertHandlers()
	{
		return (TreeMap<Integer, HashSet<PipeTransferHandler<?>>>) m_insertHandlers.clone();
	}
	
	@SuppressWarnings("unchecked")
	public TreeMap<Integer, HashSet<PipeTransferHandler<?>>> cloneExtractHandlers()
	{
		return (TreeMap<Integer, HashSet<PipeTransferHandler<?>>>) m_extractHandlers.clone();
	}
	
	public void addChannelNetwork(PipeChannelNetwork network)
	{
		m_channelNetworks.compute(network, (n, v) -> { return (v == null) ? 1 : v + 1; });
	}
	
	public void addChannelNetwork(PipeChannelNetwork network, int count)
	{
		m_channelNetworks.compute(network, (n, v) -> { return (v == null) ? count : v + count; });
	}
	
	public void removeChannelNetwork(PipeChannelNetwork network)
	{
		m_channelNetworks.compute(network, (n, v) -> { return (v == null || v <= 1) ? null : v - 1; });
	}
	
	public void purgeChannelNetwork(PipeChannelNetwork network)
	{
		m_channelNetworks.remove(network);
	}
	
	public void setChannelNetworkCount(PipeChannelNetwork network, int count)
	{
		m_channelNetworks.put(network, count);
	}
	
	public Set<PipeChannelNetwork> getChannelNetworks()
	{
		return m_channelNetworks.keySet();
	}
	
	public int numChannelNetworks()
	{
		return m_channelNetworks.size();
	}
	
	public boolean takeChannelsFrom(PipeNetwork other)
	{
		boolean newChannels = false;
		Set<Entry<PipeChannelNetwork, Integer>> otherEntries = other.m_channelNetworks.entrySet();
		for (Entry<PipeChannelNetwork, Integer> entry : otherEntries)
		{
			if (m_channelNetworks.compute(entry.getKey(), (n, v) -> (v == null) ? entry.getValue() : v + entry.getValue())
					<= entry.getValue())
			{
				newChannels = true;
			}
			entry.getKey().moveNetwork(other, this);
		}
		other.m_channelNetworks.clear();
		return newChannels;
	}
	
	public boolean moveChannelsTo(PipeNetwork other)
	{
		return other.takeChannelsFrom(this);
	}
	
	public boolean isAttachedToChannelNetwork(PipeChannelNetwork network)
	{
		return m_channelNetworks.containsKey(network);
	}
	
	public PipeHyperNetwork getHyperNetwork()
	{
		if (m_channelNetworks.isEmpty())
			return null;
		// All channels should, by definition, be connected to the same hyper network,
		// so pick any one and return its hyper network.
		Iterator<PipeChannelNetwork> iter = m_channelNetworks.keySet().iterator();
		if (iter.hasNext())
			return iter.next().getHyperNetwork();
		else
			return null;
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
    	m_numPipes = 0;
    }
    
    public boolean isEmpty()
    {
    	return m_numPipes == 0;
    }
    
    public int numPipes()
    {
    	return m_numPipes;
    }
    
	private final PipeType m_type;
	private HashMap<PipeChannelNetwork, Integer> m_channelNetworks;
	private PipeHandlerMap<?> m_extractHandlers;
	private PipeHandlerMap<?> m_insertHandlers;
	private int m_numPipes;
}
