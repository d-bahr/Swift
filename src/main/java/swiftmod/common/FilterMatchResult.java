package swiftmod.common;

public class FilterMatchResult<T extends Filter<?>>
{
    public FilterMatchResult()
    {
        this(null, false);
        matchCount = -1;
    }

    public FilterMatchResult(boolean m)
    {
        this(null, m);
        matchCount = -1;
    }

    public FilterMatchResult(T f, boolean m)
    {
        filter = f;
        matches = m;
        matchCount = -1;
    }

    public FilterMatchResult(T f, int c)
    {
        filter = f;
        matches = true;
        matchCount = c;
    }

    public int getMatchCount()
    {
        return (filter != null && filter.getMatchCount()) ? matchCount : 0;
    }

    public T filter;
    public boolean matches;
    public int matchCount;
}
