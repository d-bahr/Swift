package swiftmod.common;

public interface Filter<T>
{
    public boolean getMatchCount();
    public FilterMatchResult<Filter<T>> filter(T stack, boolean reduceFilter);
    default FilterMatchResult<Filter<T>> filter(T stack)
    {
        return filter(stack, false);
    }
}
