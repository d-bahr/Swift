package swiftmod.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;

public abstract class WildcardFilter<T, U> implements Filter<T>
{
    public WildcardFilter()
    {
        super();
        regexes = new ArrayList<Pattern>();
    }
    
    public WildcardFilter(ItemStack itemStack)
    {
        super();
        regexes = new ArrayList<Pattern>();
    	if (itemStack != null && !itemStack.isEmpty())
    	{
	    	List<String> filters = itemStack.get(SwiftDataComponents.WILDCARD_LIST_DATA_COMPONENT);
	        for (String filter : filters)
	        {
	            if (!filter.isEmpty())
	            {
	                Pattern regex = Pattern.compile(convertGlobToRegex(filter));
	                regexes.add(regex);
	            }
	        }
    	}
    }

    public WildcardFilter(String filter)
    {
        super();
        regexes = new ArrayList<Pattern>();
        if (!filter.isEmpty())
        {
            Pattern regex = Pattern.compile(convertGlobToRegex(filter));
            regexes.add(regex);
        }
    }

    public WildcardFilter(Pattern regex)
    {
        super();
        regexes = new ArrayList<Pattern>();
        regexes.add(regex);
    }

    public WildcardFilter(Collection<String> filters)
    {
        super();
        regexes = new ArrayList<Pattern>();
        for (String filter : filters)
        {
            if (!filter.isEmpty())
            {
                Pattern regex = Pattern.compile(convertGlobToRegex(filter));
                regexes.add(regex);
            }
        }
    }

    public boolean getMatchCount()
    {
        return false;
    }

    public FilterMatchResult<Filter<T>> filter(ResourceKey<U> tag)
    {
    	return filter(tag, false);
    }

    public FilterMatchResult<Filter<T>> filter(ResourceKey<U> tag, boolean reduceFilter)
    {
        if (regexes.isEmpty())
            return new FilterMatchResult<Filter<T>>(this, true);

        String tagName = tag.location().toString();
        for (Pattern regex : regexes)
        {
            Matcher m = regex.matcher(tagName);
            if (m.matches())
                return new FilterMatchResult<Filter<T>>(createReducedFilter(regex), true);
        }

        return new FilterMatchResult<Filter<T>>(this, false);
    }

    public FilterMatchResult<Filter<T>> filter(Stream<TagKey<U>> tags)
    {
        return filter(tags, false);
    }

    public FilterMatchResult<Filter<T>> filter(Stream<TagKey<U>> tags, boolean reduceFilter)
    {
        if (regexes.isEmpty())
            return new FilterMatchResult<Filter<T>>(this, true);

    	Iterator<TagKey<U>> iter = tags.iterator();
    	while (iter.hasNext())
        {
    		TagKey<U> tagKey = iter.next();
    		ResourceKey<? extends Registry<U>> tag = tagKey.registry();
    		String tagName1 = tag.location().toString();
    		String tagName2 = tagKey.location().toString();
	        for (Pattern regex : regexes)
	        {
                Matcher m = regex.matcher(tagName1);
                if (m.matches())
                    return new FilterMatchResult<Filter<T>>(createReducedFilter(regex), true);
                m = regex.matcher(tagName2);
                if (m.matches())
                    return new FilterMatchResult<Filter<T>>(createReducedFilter(regex), true);
	        }
	    }

        return new FilterMatchResult<Filter<T>>(this, false);
    }

    protected abstract WildcardFilter<T, U> createReducedFilter(Pattern regex);

    /**
     * Converts a standard POSIX Shell globbing pattern into a regular expression pattern. The result
     * can be used with the standard {@link java.util.regex} API to recognize strings which match the
     * glob pattern.
     * <p/>
     * See also, the POSIX Shell language:
     * http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_13_01
     * 
     * @param pattern A glob pattern.
     * @return A regex pattern to recognize the given glob pattern.
     */
    private static final String convertGlobToRegex(String pattern)
    {
        StringBuilder sb = new StringBuilder(pattern.length());
        int inGroup = 0;
        int inClass = 0;
        int firstIndexInClass = -1;
        char[] arr = pattern.toCharArray();
        for (int i = 0; i < arr.length; i++)
        {
            char ch = arr[i];
            switch (ch)
            {
            case '\\':
                if (++i >= arr.length)
                {
                    sb.append('\\');
                }
                else
                {
                    char next = arr[i];
                    switch (next)
                    {
                    case ',':
                        // escape not needed
                        break;
                    case 'Q':
                    case 'E':
                        // extra escape needed
                        sb.append('\\');
                    default:
                        sb.append('\\');
                    }
                    sb.append(next);
                }
                break;
            case '*':
                if (inClass == 0)
                    sb.append(".*");
                else
                    sb.append('*');
                break;
            case '?':
                if (inClass == 0)
                    sb.append('.');
                else
                    sb.append('?');
                break;
            case '[':
                inClass++;
                firstIndexInClass = i + 1;
                sb.append('[');
                break;
            case ']':
                inClass--;
                sb.append(']');
                break;
            case '.':
            case '(':
            case ')':
            case '+':
            case '|':
            case '^':
            case '$':
            case '@':
            case '%':
                if (inClass == 0 || (firstIndexInClass == i && ch == '^'))
                    sb.append('\\');
                sb.append(ch);
                break;
            case '!':
                if (firstIndexInClass == i)
                    sb.append('^');
                else
                    sb.append('!');
                break;
            case '{':
                inGroup++;
                sb.append('(');
                break;
            case '}':
                inGroup--;
                sb.append(')');
                break;
            case ',':
                if (inGroup > 0)
                    sb.append('|');
                else
                    sb.append(',');
                break;
            default:
                sb.append(ch);
            }
        }
        return sb.toString();
    }

    private ArrayList<Pattern> regexes;
}
