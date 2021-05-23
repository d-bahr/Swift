package swiftmod.common.channels;

import java.util.HashMap;

public class OwnedChannels<T extends ChannelData> extends HashMap<String, T>
{
    @java.io.Serial
    private static final long serialVersionUID = 123456789L;

    public OwnedChannels()
    {
        super();
    }

    public OwnedChannels(int initialCapacity)
    {
        super(initialCapacity);
    }

    public OwnedChannels(int initialCapacity, float loadFactor)
    {
        super(initialCapacity, loadFactor);
    }
}
