package swiftmod.pipes.networks;

import java.util.Comparator;
import java.util.TreeSet;

public class PipeNetworkPrioritizedConnectionList<T extends Comparable<? super T>>
{
	public PipeNetworkPrioritizedConnectionList()
	{
		m_list = new TreeSet<T>();
		m_comparator = Comparator.naturalOrder();
	}
	
	public PipeNetworkPrioritizedConnectionList(Comparator<T> comparator)
	{
		m_list = new TreeSet<T>();
		m_comparator = comparator;
	}
	
	TreeSet<T> m_list;
	Comparator<T> m_comparator;
}
