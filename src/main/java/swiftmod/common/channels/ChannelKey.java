package swiftmod.common.channels;

import java.util.Objects;

public class ChannelKey
{
    public ChannelKey()
    {
        name = "";
        type = ChannelType.Items;
    }

    public ChannelKey(String name, ChannelType type)
    {
        this.name = name;
        this.type = type;
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(type, name);
    }

    @Override
    public boolean equals(Object obj)
    {
        if ((null == obj) || (obj.getClass() != ChannelKey.class))
            return false;
        ChannelKey other = (ChannelKey)obj;
        return name.equals(other.name) && type == other.type;
    }

    public String name;
    public ChannelType type;
}
