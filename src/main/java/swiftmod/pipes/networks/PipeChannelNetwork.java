package swiftmod.pipes.networks;

import java.util.HashMap;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import swiftmod.common.Swift;
import swiftmod.common.channels.ChannelData;
import swiftmod.common.channels.ChannelSpec;
import swiftmod.pipes.PipeType;

// A channel network is a collection of networks that are all attached via the same channel (i.e via wormholes).
public class PipeChannelNetwork extends ChannelData
{
	public PipeChannelNetwork(PipeType type)
	{
		m_type = type;
		m_networks = new HashMap<PipeNetwork, Integer>();
		m_hyperNetwork = new PipeHyperNetwork(type);
		m_hyperNetwork.addNetwork(this);
	}
	
	private PipeChannelNetwork(CompoundTag nbt, PipeType type)
	{
		super(nbt);
		m_type = type;
		m_networks = new HashMap<PipeNetwork, Integer>();
		m_hyperNetwork = new PipeHyperNetwork(type);
		m_hyperNetwork.addNetwork(this);
	}
	
	public PipeType getType()
	{
		return m_type;
	}
	
	public boolean addNetwork(PipeNetwork network)
	{
		checkNetworkType(network);
		Integer value = m_networks.compute(network, (n, v) -> { return (v == null) ? 1 : v + 1; });
		network.addChannelNetwork(this);
		return value == 1; // Value will be one if it is actually a new addition.
	}
	
	public boolean addNetwork(PipeNetwork network, int count)
	{
		checkNetworkType(network);
		Integer value = m_networks.compute(network, (n, v) -> { return (v == null) ? count : v + count; });
		network.addChannelNetwork(this, count);
		return value == count; // Value will be == count if it is actually a new addition.
	}
	
	public boolean removeNetwork(PipeNetwork network)
	{
		checkNetworkType(network);
		network.removeChannelNetwork(this);
		return m_networks.compute(network, (n, v) -> { return (v == null || v <= 1) ? null : v - 1; }) == null;
	}
	
	public int purgeNetwork(PipeNetwork network)
	{
		checkNetworkType(network);
		Integer value = m_networks.remove(network);
		network.purgeChannelNetwork(this);
		return (value == null) ? 0 : value;
	}
	
	public void setNetworkCount(PipeNetwork network, int count)
	{
		if (count == 0)
		{
			purgeNetwork(network);
			return;
		}
		checkNetworkType(network);
		m_networks.put(network, count);
		network.setChannelNetworkCount(this, count);
	}
	
	public void moveNetwork(PipeNetwork from, PipeNetwork to)
	{
		checkNetworkType(from);
		checkNetworkType(to);
		Integer value = m_networks.remove(from);
		if (value != null)
			m_networks.compute(to, (n, v) -> { return (v == null) ? value : v + value; });
	}
	
	public PipeHyperNetwork setHyperNetwork(PipeHyperNetwork hyperNetwork)
	{
		checkNetworkType(hyperNetwork);
		if (m_hyperNetwork != null)
			m_hyperNetwork.removeNetwork(this);
		PipeHyperNetwork old = m_hyperNetwork;
		m_hyperNetwork = hyperNetwork;
		m_hyperNetwork.addNetwork(this);
		return old;
	}
	
	public PipeHyperNetwork getHyperNetwork()
	{
		return m_hyperNetwork;
	}
	
	public Set<PipeNetwork> getNetworks()
	{
		return m_networks.keySet();
	}
	
	private void checkNetworkType(PipeNetwork network)
	{
		Swift.doAssert(network.getType() == getType(), "Mixing different local network and channel network types.");
	}
	
	private void checkNetworkType(PipeHyperNetwork network)
	{
		Swift.doAssert(network.getType() == getType(), "Mixing different local network and channel network types.");
	}
	
	public static PipeChannelNetwork create(ChannelSpec spec)
	{
		return new PipeChannelNetwork(spec.type.toPipeType());
	}
	
	public static PipeChannelNetwork decode(CompoundTag nbt, ChannelSpec spec)
	{
		return new PipeChannelNetwork(nbt, spec.type.toPipeType());
	}
	
	private final PipeType m_type; // TODO: Refactor to ChannelType.
	private HashMap<PipeNetwork, Integer> m_networks;
	private PipeHyperNetwork m_hyperNetwork;
}
