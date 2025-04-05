package swiftmod.common.upgrades;

public class BasicUpgradeItemStackHandler extends UpgradeItemStackHandler
{
    public BasicUpgradeItemStackHandler()
    {
        super(1);
    }

    @Override
    public int getMaxUpgrades(UpgradeType type)
    {
        switch (type)
        {
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
        case ChunkLoaderUpgrade:
            return 0;
        default:
            return -1;
        }
    }
}
