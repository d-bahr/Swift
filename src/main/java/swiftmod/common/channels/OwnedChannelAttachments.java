package swiftmod.common.channels;

import java.util.HashMap;
import java.util.HashSet;

public class OwnedChannelAttachments extends HashMap<ChannelKey, HashSet<ChannelAttachment>>
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

    public void add(ChannelKey key, ChannelAttachment attachment)
    {
        HashSet<ChannelAttachment> s = get(key);
        if (s != null)
        {
            s.add(attachment);
        }
        else
        {
            s = new HashSet<ChannelAttachment>();

            s.add(attachment);

            put(key, s);
        }
    }

    public void remove(ChannelKey key, ChannelAttachment attachment)
    {
        HashSet<ChannelAttachment> s = get(key);
        if (s != null)
            s.remove(attachment);
    }

    public HashSet<ChannelAttachment> getOrEmpty(ChannelKey key)
    {
        HashSet<ChannelAttachment> s = get(key);
        if (s != null)
            return s;
        else
            return new HashSet<ChannelAttachment>();
    }
}
