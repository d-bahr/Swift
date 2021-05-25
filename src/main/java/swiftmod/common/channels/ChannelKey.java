package swiftmod.common.channels;

public class ChannelKey
{
    public ChannelKey()
    {
        name = "";
        tag = 0;
    }

    public ChannelKey(String name, int tag)
    {
        this.name = name;
        this.tag = tag;
    }

    @Override
    public int hashCode()
    {
        return name.hashCode() ^ tag;
    }

    @Override
    public boolean equals(Object obj)
    {
        if ((null == obj) || (obj.getClass() != ChannelKey.class))
            return false;
        ChannelKey other = (ChannelKey)obj;
        return name.equals(other.name) && tag == other.tag;
    }

    public String name;
    public int tag;
}
