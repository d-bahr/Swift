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
