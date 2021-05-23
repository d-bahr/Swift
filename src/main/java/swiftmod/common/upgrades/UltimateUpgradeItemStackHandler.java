package swiftmod.common.upgrades;

public class UltimateUpgradeItemStackHandler extends UpgradeItemStackHandler
{
    public UltimateUpgradeItemStackHandler()
    {
        super(4);
    }

    @Override
    public int getMaxUpgrades(UpgradeType type)
    {
        switch (type)
        {
        case SpeedUpgrade:
            return 19;
        case SpeedDowngrade:
            return 64;
        case StackUpgrade:
            return 64;
        case UltimateStackUpgrade:
            return 1;
        case ChunkLoaderUpgrade:
            return 1;
        case TeleportUpgrade:
            return 1;
        case InterdimensionalUpgrade:
            return 1;
        default:
            return 0;
        }
    }

    @Override
    public int getSlotForUpgrade(UpgradeType type)
    {
        switch (type)
        {
        case SpeedUpgrade:
            return 0;
        case SpeedDowngrade:
            return 0;
        case StackUpgrade:
            return 1;
        case UltimateStackUpgrade:
            return 1;
        case ChunkLoaderUpgrade:
            return 2;
        case TeleportUpgrade:
            return 3;
        case InterdimensionalUpgrade:
            return 3;
        default:
            return -1;
        }
    }
}
