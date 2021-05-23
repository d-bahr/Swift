package swiftmod.common.channels;

import java.util.HashMap;
import java.util.HashSet;

public class OwnedChannelAttachments extends HashMap<String, HashSet<ChannelAttachment>>
{
    @java.io.Serial
    private static final long serialVersionUID = 123456789L;

    public OwnedChannelAttachments()
    {
        super();
    }

    public OwnedChannelAttachments(int initialCapacity)
    {
        super(initialCapacity);
    }

    public OwnedChannelAttachments(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
    }

    public void add(String name, ChannelAttachment attachment)
    {
        HashSet<ChannelAttachment> s = get(name);
        if (s != null)
        {
            s.add(attachment);
        }
        else
        {
            s = new HashSet<ChannelAttachment>();

            s.add(attachment);

            put(name, s);
        }
    }

    public void remove(String name, ChannelAttachment attachment)
    {
        HashSet<ChannelAttachment> s = get(name);
        if (s != null)
            s.remove(attachment);
    }

    public HashSet<ChannelAttachment> getOrEmpty(String name)
    {
        HashSet<ChannelAttachment> s = get(name);
        if (s != null)
            return s;
        else
            return new HashSet<ChannelAttachment>();
    }
}
