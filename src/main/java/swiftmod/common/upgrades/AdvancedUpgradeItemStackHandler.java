package swiftmod.common.upgrades;

public class AdvancedUpgradeItemStackHandler extends UpgradeItemStackHandler
{
    public AdvancedUpgradeItemStackHandler()
    {
        super(3);
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
            return 1;
        case ChunkLoaderUpgrade:
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
        case ChunkLoaderUpgrade:
            return 2;
        default:
            return -1;
        }
    }
}
