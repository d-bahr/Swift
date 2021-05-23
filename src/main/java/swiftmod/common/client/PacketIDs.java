package swiftmod.common.client;

public enum PacketIDs
{
    RedstoneControlConfiguration(0),
    TransferDirectionConfiguration(1),
    ItemFilterConfiguration(2),
    ItemFilterSlot(3),
    ClearFilter(4),
    ChannelConfiguration(5),
    SideConfiguration(6),
    SlotConfiguration(7),
    WildcardFilter(8),
    FluidFilterConfiguration(9),
    FluidFilterSlot(10);

    private PacketIDs(int v)
    {
        m_value = v;
    }
    
    public int value()
    {
        return m_value;
    }
    
    private int m_value;
}
