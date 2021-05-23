package swiftmod.common.upgrades;

public enum UpgradeType
{
    SpeedUpgrade(0),
    SpeedDowngrade(1),
    StackUpgrade(2),
    UltimateStackUpgrade(3),
    ChunkLoaderUpgrade(4),
    TeleportUpgrade(5),
    InterdimensionalUpgrade(6),
    BasicItemFilterUpgrade(7),
    BasicFluidFilterUpgrade(8),
    WildcardFilterUpgrade(9),
    SideUpgrade(10);

    private int index;

    private UpgradeType(int i)
    {
        index = i;
    }

    public int getIndex()
    {
        return index;
    }

    private static final UpgradeType[] BY_INDEX =
    {
        SpeedUpgrade,
        SpeedDowngrade,
        StackUpgrade,
        UltimateStackUpgrade,
        ChunkLoaderUpgrade,
        TeleportUpgrade,
        InterdimensionalUpgrade,
        BasicItemFilterUpgrade,
        BasicFluidFilterUpgrade,
        WildcardFilterUpgrade,
        SideUpgrade
    };

    public static UpgradeType fromIndex(int index)
    {
        return BY_INDEX[index];
    }
    
    public static final int NumUpgradeTypes = BY_INDEX.length;
}
