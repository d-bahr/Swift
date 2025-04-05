package swiftmod.common;

// This is a fake filter just for sake of using with PipeTransferData
// when the transfer element in question cannot be filtered.
public class FakeFilter implements Filter<Integer>
{
	public FakeFilter()
	{
	}

	@Override
	public boolean getMatchCount()
	{
		return false;
	}

	@Override
	public FilterMatchResult<Filter<Integer>> filter(Integer stack, boolean reduceFilter)
	{
		return new FilterMatchResult<Filter<Integer>>();
	}
}
